package com.pinyougou.manager.controller;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.pinyougou.group.Goods;

import com.pinyougou.pojo.TbItem;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.query.QueryUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;

import javax.jms.*;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	//@Reference
	//private ItemSearchService searchService;
	//@Reference
	//private ItemPageService pageService;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Queue activeMQQueue;
	@Autowired
	private Queue activeMQQueue2;
	@Autowired
	private Topic activeMQTopic;
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	//修改状态
	//审核商品通过后，更新索引库
	@RequestMapping("/updateStatus")
	public Result updateStatus(String status, final Long[] ids){
		try {
			goodsService.updateStatus(status,ids);
			if(status.equals("1")){		//代表审核通过
				//审核通过后，根据商品id来查询商品列表
				List<TbItem> tbItems = goodsService.findItemListByGoodsIdandStatus(ids, status);
				System.out.println("tbItems:" + tbItems);
				//将得到的列表导入到索引库中
				if(tbItems.size() > 0){
					//searchService.importList(tbItems);
					//使用activeMQ发送消息，内容就是当前的tbItems转换后的字符串
					//① 将tbItems转换为字符串
					final String jsonString = JSON.toJSONString(tbItems);
					//② 将上面的sku商品列表信息发送出去，由searchService在后台监听得到，并更新索引库
					jmsTemplate.send(activeMQQueue, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(jsonString);
						}
					});

				}else{
					System.out.println("没有明细数据");
				}
				//只要商品审核通过就可以生成静态页面
//				for (Long id : ids) {
//					genhtml(id);
//				}
				//向生成静态页面的服务发送消息，有可能有多个服务来引用此服务，所以，使用发布/订阅模式较好
				//此时在pageService服务中监听到发过来的id列表，然后，再生成静态页面
				jmsTemplate.send(activeMQTopic, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}

			return new Result(true, "修改状态成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改状态失败");
		}
	}
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(final Long [] ids){
		try {
			goodsService.delete(ids);
			//从索引库中删除商品
			//searchService.deleteGoodsId(Arrays.asList(ids));

			//向后台（搜索服务）发送消息,然后，搜索服务就会从索引库中删除商品
			jmsTemplate.send(activeMQQueue2, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		return goodsService.findPage(goods, page, rows);		
	}

	/**
	 * 测试生成静态页面
	 * @param goodsId
	 */
	@RequestMapping("/genhtml")
	public void genhtml(Long goodsId){
		//pageService.genItemHtml(goodsId);
	}
}
