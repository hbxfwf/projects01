package com.pinyougou.search.service.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:监听运营商后台发送过来的消息(删除指定的商品列表)
 * @Date: Create in 2019/5/8 10:48
 */
@Component
public class MyMessageListener2 implements MessageListener {
    @Autowired
    private ItemSearchService searchService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            //1.得到要删除的商品id列表
            Long [] ids = (Long[]) objectMessage.getObject();
            //2.遍历并比索引库中删除sku商品
            searchService.deleteGoodsId(Arrays.asList(ids));
            //3.提示
            System.out.println("从索引库删除商品成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
