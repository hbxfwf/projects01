package com.pinyougou.search.service.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:监听运营商后台发送过来的消息(经过审核通过后的sku商品列表)
 * @Date: Create in 2019/5/8 10:48
 */
@Component
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemSearchService searchService;
    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage = (TextMessage) message;
            //1.得到商品列表集合
            String jsonString = textMessage.getText();
            //2.将商品列表转换为集合
            List<TbItem> itemList = JSON.parseArray(jsonString, TbItem.class);
            for (TbItem tbItem : itemList) {
                String spec = tbItem.getSpec();
                //将上面的spec对象转换为map对象
                Map map = JSON.parseObject(spec, Map.class);
                tbItem.setSpecMap(map);
            }
            //3.将上面的sku列表导入到索引库中
            searchService.importList(itemList);
            System.out.println("经过审核--->导入到索引库成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
