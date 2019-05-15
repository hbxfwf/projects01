package com.pinyougou.seckill.service;
import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbSeckillGoods;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbSeckillOrder;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillGoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbSeckillGoods seckillGoods);
	
	
	/**
	 * 修改
	 */
	public void update(TbSeckillGoods seckillGoods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillGoods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize);


	/**
	 * 返回当前正在参与秒杀的商品
	 * @return
	 */
	public List<TbSeckillGoods> findList();

	/**
	 * 从redis中取出秒杀商品
	 * @param id
	 * @return
	 */
	TbSeckillGoods findSeckillGoodsById(Long id);

	/**
	 * 提交订单
	 * @param seckillId
	 * @param userId
	 */
	public void submitOrder(Long seckillId,String userId);

	/**
	 * 从redis中取出当前用户的秒杀订单
	 * @param userId
	 * @return
	 */
	TbSeckillOrder findSeckillOrderFromRedis(String userId);

	/**
	 * 将订单从redis中保存到数据库中
	 * @param userId
	 * @param out_trade_no
	 * @param transaction_id
	 */
	void saveOrderFromRedisToDB(String userId, String out_trade_no, Object transaction_id);

	/**
	 * 从缓存中删除订单
	 * @param userId
	 * @param orderId
	 */
	public void deleteOrderFromRedis(String userId,Long orderId);

}
