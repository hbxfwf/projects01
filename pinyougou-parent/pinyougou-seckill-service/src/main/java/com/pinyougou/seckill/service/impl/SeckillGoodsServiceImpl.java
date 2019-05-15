package com.pinyougou.seckill.service.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoodsExample.Criteria;


import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillGoodsServiceImpl  implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private TbSeckillOrderMapper orderMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	@Autowired
	private IdWorker idWorker;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page=   (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillGoods!=null){			
						if(seckillGoods.getTitle()!=null && seckillGoods.getTitle().length()>0){
				criteria.andTitleLike("%"+seckillGoods.getTitle()+"%");
			}
			if(seckillGoods.getSmallPic()!=null && seckillGoods.getSmallPic().length()>0){
				criteria.andSmallPicLike("%"+seckillGoods.getSmallPic()+"%");
			}
			if(seckillGoods.getSellerId()!=null && seckillGoods.getSellerId().length()>0){
				criteria.andSellerIdLike("%"+seckillGoods.getSellerId()+"%");
			}
			if(seckillGoods.getStatus()!=null && seckillGoods.getStatus().length()>0){
				criteria.andStatusLike("%"+seckillGoods.getStatus()+"%");
			}
			if(seckillGoods.getIntroduction()!=null && seckillGoods.getIntroduction().length()>0){
				criteria.andIntroductionLike("%"+seckillGoods.getIntroduction()+"%");
			}
	
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	//查询正在秒杀的所有商品
	@Override
	public List<TbSeckillGoods> findList() {
		//1.从redis中查询出所有秒杀商品
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("secKillGoods").values();
		//2.判断是否有此商品
		if(seckillGoodsList == null || seckillGoodsList.size() == 0){   //没有此商品就从数据库查询商品
			//2.1)构造查询条件
			TbSeckillGoodsExample example = new TbSeckillGoodsExample();
			//2.2)添加查询条件
			Criteria criteria = example.createCriteria();
			criteria.andStatusEqualTo("1");			//经过审核通过的商品
			//2.3)开始时间小于等于当前时间，结束时间要大于当前时间
			criteria.andStartTimeLessThanOrEqualTo(new Date());
			criteria.andEndTimeGreaterThan(new Date());
			//2.4)库存量大于0
			criteria.andStockCountGreaterThan(0);
			//3.查询出满足条件的秒杀商品列表
			seckillGoodsList = seckillGoodsMapper.selectByExample(example);
			//4.遍历并将其放放redis中
			for (TbSeckillGoods goods : seckillGoodsList) {
				redisTemplate.boundHashOps("secKillGoods").put(goods.getId(),goods);
			}
			System.out.println("1.从数据库中取秒杀商品。。。");
		}
		System.out.println("2.从redis中取秒杀商品。。。");
		return seckillGoodsList;
	}

	/**
	 * 从redis中取出一件商品
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findSeckillGoodsById(Long id) {
		return (TbSeckillGoods)redisTemplate.boundHashOps("secKillGoods").get(id);
	}

	/**
	 * 下订单
	 * @param seckillId
	 * @param userId
	 */
	@Override
	public void submitOrder(Long seckillId, String userId) {
		//1.从redis查询出商品
		TbSeckillGoods secKillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoods").get(seckillId);
		//2.判断此商品是否存在
		if(secKillGoods == null){
			throw new RuntimeException("商品不存在");
		}
		if(secKillGoods.getStockCount() == 0){
			throw new RuntimeException("商品库存不足！");
		}
		//修改库存量
		secKillGoods.setStockCount(secKillGoods.getStockCount()-1);
		//重新放回到redis中，供其它人购买
		redisTemplate.boundHashOps("secKillGoods").put(seckillId,secKillGoods);
		//如果修改库存量后，商品剩余数量为0，
		// ①修改回数据库
		// ②从redis中清除此商品信息
		if(secKillGoods.getStockCount() == 0){
			seckillGoodsMapper.updateByPrimaryKey(secKillGoods);   //商品销售完了，就修改回数据库
			redisTemplate.boundHashOps("secKillGoods").delete(seckillId); //从redis中清除此商品
		}

		//3.构造订单并放到redis中
		TbSeckillOrder order = new TbSeckillOrder();
		order.setStatus("0");		//未支付
		order.setUserId(userId);
		order.setCreateTime(new Date());
		order.setId(idWorker.nextId());
		order.setMoney(secKillGoods.getCostPrice());  //支付金额
		order.setSeckillId(secKillGoods.getId());
		order.setSellerId(secKillGoods.getSellerId());
		//key:用户id
		//value：用户的秒杀订单（预订单）
		redisTemplate.boundHashOps("seckillOrder").put(userId,order);
	}

	@Override
	public TbSeckillOrder findSeckillOrderFromRedis(String userId) {
		return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
	}

	/**
	 * 将redis中的订单添加到数据库中，并同时从redis中清除此订单
	 * @param userId
	 * @param out_trade_no
	 * @param transaction_id
	 */
	@Override
	public void saveOrderFromRedisToDB(String userId, String out_trade_no, Object transaction_id) {
		//1.从redis中取得订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		//2.判断订单是否存在
		if(null == seckillOrder){
			throw new RuntimeException("订单不存在！");
		}
		if(seckillOrder.getId().longValue() != Long.parseLong(out_trade_no)){
			throw new RuntimeException("不是同一个订单！");
		}
		seckillOrder.setPayTime(new Date());
		seckillOrder.setStatus("1");   //代表己支付
		seckillOrder.setTransactionId(transaction_id.toString());  //流水号
		//3.将订单从redis中保存到数据库中
		orderMapper.insert(seckillOrder);
		//4.从redis删除订单
		redisTemplate.boundHashOps("seckillOrder").delete(userId);
	}

	/**
	 * 如果超时，就从redis中删除此秒杀订单，并更新redis中的此商品的库存
	 * @param userId
	 * @param orderId
	 */
	@Override
	public void deleteOrderFromRedis(String userId, Long orderId) {
		//1.从redis中取出订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
		//2.比较是否是同一个订单
		if(seckillOrder != null &&seckillOrder.getId().longValue() == orderId.longValue()){
			//从redis中删除此订单
			redisTemplate.boundHashOps("seckillOrder").delete(userId);
		}
		//3.更新库存
		//3.1)从redis中取出此商品
		TbSeckillGoods secKillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoods").get(seckillOrder.getSeckillId());
		if(secKillGoods != null){
			//3.2)修改库存
			secKillGoods.setStockCount(secKillGoods.getStockCount()+1);
			//3.3)重新放回到redis
			redisTemplate.boundHashOps("secKillGoods").put(secKillGoods.getId(),secKillGoods);
		}

	}



}
