package com.pinyougou.content.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import com.pinyougou.pojo.TbContentExample.Criteria;
import com.pinyougou.content.service.ContentService;

import com.pinyougou.pojo.*;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	@Autowired
	private RedisTemplate redisTemplate;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page=   (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		//redis缓存同步数据库,清除指定小key（广告分类id）
		redisTemplate.boundHashOps("contents").delete(content.getCategoryId()+"");
		contentMapper.insert(content);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		redisTemplate.boundHashOps("contents").delete(content.getCategoryId()+"");
		contentMapper.updateByPrimaryKey(content);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			//1.根据广告id查询出此广告的分类id
			TbContent tbContent = contentMapper.selectByPrimaryKey(id);
			System.out.println("categoryId:" + tbContent.getCategoryId());
			redisTemplate.boundHashOps("contents").delete(tbContent.getCategoryId()+"");
			contentMapper.deleteByPrimaryKey(id);
		}		
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		
		if(content!=null){			
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 根据分类id查询广告列表
	 * @param categoryId
	 * @return
	 */
	@Override
	public List<TbContent> findContentByCategoryId(String categoryId) {
		//使用缓存：第一步：就要从缓存中根据广告分类id直接取出此分类下的所有广告列表
		List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contents").get(categoryId);
		//第二步：判断上面的广告列表是否存在（第一次就不存在）
		if(contentList != null){
			System.out.println("从缓存中取出数据...");
		}else{ //第三步：如果缓存中不存，就从数据库中取数据，并添加到缓存中
			System.out.println("从数据库中取数据...");
			//① 从数据库中取出数据
			//1.定义广告查询实例
			TbContentExample example = new TbContentExample();
			//2.定义查询条件
			Criteria criteria = example.createCriteria();
			//3.添加查询条件
			criteria.andCategoryIdEqualTo(Long.parseLong(categoryId));
			//4.进行条件查询（外键查询）
			contentList = contentMapper.selectByExample(example);
			//② 将从数据库中取出的数据放到缓存中
			redisTemplate.boundHashOps("contents").put(categoryId,contentList);
		}

		return contentList;
	}

}
