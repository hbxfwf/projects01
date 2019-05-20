package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/16 09:23
 */
@Component
public class SeckillTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    //每隔一分钟会自动触发
    //思路：每一分钟查询一次看是否有新的秒杀商品，有就添加到redis中
    @Scheduled(cron = "0 * * * * ?")
    public void refreshSeckillGoods(){
        //1.查询出原来redis中的秒杀商品的id列表
        //1.1)得到秒杀商品的id集合
        Set keys = redisTemplate.boundHashOps("secKillGoods").keys();
        //1.2)转换为List集合
        List<Long> ids = new ArrayList<>(keys);

        //2.在数据库的tbseckillGoods表中查询满足条件的秒杀商品，将id不在上面的id列表中的商品添加到redis中
        //2.1)构造查询实例
        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        //2.2)添加查询条件
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");                     //审核通过的商品
        criteria.andStartTimeLessThanOrEqualTo(new Date());         //开始时间小于等于当前时间
        criteria.andEndTimeGreaterThan(new Date());                 //结束时间大于当前时间
        criteria.andStockCountGreaterThan(0);               //库存量大于0
        criteria.andIdNotIn(ids);                                   //id不在原来redis的秒杀商品id的列表中
        //2.3)查询得到新的秒杀商品列表
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //3.将上面的新得到的秒杀商品放到redis中
        for (TbSeckillGoods goods : seckillGoodsList) {
            System.out.println("将新商品放到redis中....");
            redisTemplate.boundHashOps("secKillGoods").put(goods.getId(),goods);
        }
        if( seckillGoodsList.size() > 0){
            System.out.println("一共放到redis中有" + seckillGoodsList.size() + "件商品！");
        }

    }
    //每隔一秒就运行一次，
    // 思路：查询看是否有过期的商品，如果有，就从redis中删除它
    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoods(){
        //1.从redis中查询出所有的商品列表
        List<TbSeckillGoods> secKillGoods = redisTemplate.boundHashOps("secKillGoods").values();
        //2.遍历秒杀的商品列表
        for (TbSeckillGoods secKillGood : secKillGoods) {
            //如果结束时间要小于或等于当前时间，就从redis中清除它
            if(secKillGood.getEndTime().getTime() <= new Date().getTime()){
                System.out.println("正在从redis中清除一件过期商品...");
                redisTemplate.boundHashOps("secKillGoods").delete(secKillGood.getId());
            }
        }
    }
}
