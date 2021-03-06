package com.pinyougou.sellergoods.service.impl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.pojo.TbGoodsExample.Criteria;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper descMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemCatMapper catMapper;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}
	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(Goods goods) {
		//1.在商品表（spu）中添加数据
		goodsMapper.insert(goods.getGoods());
		//2.设置商品描述表的主键goodsId
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		//3.添加商品描述
		descMapper.insert(goods.getGoodsDesc());
		//4.添加sku商品信息
		saveItem(goods);

	}
	//添加sku商品信息
	private void saveItem(Goods goods) {
		//4.得到sku的商品列表(添加sku商品信息)
		List<TbItem> items = goods.getItems();
		//5.遍历商品列表
		String title = goods.getGoods().getGoodsName(); //商品名称
		for (TbItem item : items) {
			item.setCreateTime(new Date());
			item.setUpdateTime(new Date());
			//5.1）根据品牌id查询品牌
			Long brandId = goods.getGoods().getBrandId();
			TbBrand tbBrand = brandMapper.selectByPrimaryKey(brandId);
			//5.2）设置品牌名称
			item.setBrand(tbBrand.getName());
			//5.3)设置商家id
			item.setSellerId(goods.getGoods().getSellerId());
			//5.4)设置商家名称
			TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
			item.setSeller(tbSeller.getNickName());
			//5.5)设置图片
			//5.5.1)得到图片集合的json串
			String itemImages = goods.getGoodsDesc().getItemImages();
			//5.5.2)将json串转换成java对象
			List<Map> list = JSON.parseArray(itemImages, Map.class);
			//5.5.3)取出第一个图片
			if(list != null && list.size() > 0){
				String url = (String) list.get(0).get("url");
				item.setImage(url);
			}
			//5.6)设置商品编号
			item.setGoodsId(goods.getGoods().getId());
			//5.7)设置商品分类编号（三级分类即可）
			item.setCategoryid(goods.getGoods().getCategory3Id());
			//5.8)查询分类
			TbItemCat tbItemCat = catMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
			item.setCategory(tbItemCat.getName());
			//5.9）得到规格字符串
			String spec = item.getSpec();
			//转换为java对象
			Map map = JSON.parseObject(spec, Map.class);
			//5.10)遍历map
			for (Object key : map.keySet()) {
				String attrubiteValue  = (String) map.get(key);
				title += attrubiteValue + " ";
			}
			//5.11)设置标题
			item.setTitle(title);
			//5.12)添加商品
			itemMapper.insert(item);
		}
	}
	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods){
		//1.修改tbgoods表，spu商品表
		goodsMapper.updateByPrimaryKey(goods.getGoods());
		int a = 1/0;
		//2.修改tbgoodsdesc表，spu商品描述表
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		descMapper.updateByPrimaryKey(goods.getGoodsDesc());
		//3.对于sku商品表的处理思路：先根据goodsId外键删除其记录，再添加
		//3.1)先删除
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//3.2)再添加
		saveItem(goods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		 Goods goods = new Goods();
		 //1.查询出spu商品表
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		//2.查询出spu商品描述表的信息
		TbGoodsDesc goodsDesc = descMapper.selectByPrimaryKey(id);
		//3.将它们直接与goods对象绑定
		goods.setGoods(tbGoods);
		goods.setGoodsDesc(goodsDesc);
		//4.根据goodsId查询sku商品列表
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);
		List<TbItem> items = itemMapper.selectByExample(example);
		//5.将上面的sku列表添加到组合类 goods中
		 goods.setItems(items);
		return goods;
	}

	/**
	 * 批量删除(逻辑删除：将数据表tbgoods中的is_delete字段的值设置为1即可)
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//goodsMapper.deleteByPrimaryKey(id);
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			tbGoods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(tbGoods);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(goods!=null){			
						if(goods.getSellerId()!=null && goods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+goods.getSellerId()+"%");
			}
			if(goods.getGoodsName()!=null && goods.getGoodsName().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName()+"%");
			}
			if(goods.getAuditStatus()!=null && goods.getAuditStatus().length()>0){
				criteria.andAuditStatusLike("%"+goods.getAuditStatus()+"%");
			}
			if(goods.getIsMarketable()!=null && goods.getIsMarketable().length()>0){
				criteria.andIsMarketableLike("%"+goods.getIsMarketable()+"%");
			}
			if(goods.getCaption()!=null && goods.getCaption().length()>0){
				criteria.andCaptionLike("%"+goods.getCaption()+"%");
			}
			if(goods.getSmallPic()!=null && goods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+goods.getSmallPic()+"%");
			}
			if(goods.getIsEnableSpec()!=null && goods.getIsEnableSpec().length()>0){
				criteria.andIsEnableSpecLike("%"+goods.getIsEnableSpec()+"%");
			}
			if(goods.getIsDelete()!=null && goods.getIsDelete().length()>0){
				criteria.andIsDeleteLike("%"+goods.getIsDelete()+"%");
			}
	
		}
		
		Page<TbGoods> page= (Page<TbGoods>)goodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void updateStatus(String status, Long[] ids) {
		for (Long id : ids) {
			//1.根据id查询商品
			TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
			//2.修改商品的状态
			tbGoods.setAuditStatus(status);
			//3.修改商品的状态
			goodsMapper.updateByPrimaryKey(tbGoods);
		}
	}

	@Override
	public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
		System.out.println("goodsIds:" + goodsIds[0]);
		System.out.println("status:" +status);
		//1.创建查询实例
		TbItemExample example = new TbItemExample();
		TbItemExample.Criteria criteria = example.createCriteria();
		//2.添加查询条件
		criteria.andStatusEqualTo(status);
		criteria.andGoodsIdIn(Arrays.asList(goodsIds));
		//3.根据查询实例查询出sku商品列表
		List<TbItem> itemList = itemMapper.selectByExample(example);
		return itemList;
	}

}
