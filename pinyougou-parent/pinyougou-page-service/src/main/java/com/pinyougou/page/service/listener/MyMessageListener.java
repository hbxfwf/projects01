package com.pinyougou.page.service.listener;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 监听运营商后台商品审核通过后生成静态页面
 * @Date: Create in 2019/5/8 11:34
 */
@Component
public class MyMessageListener implements MessageListener {
    @Autowired
    private ItemPageService pageService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            //1.得到要生成静态页面的商品列表
            Long[] ids = (Long[]) objectMessage.getObject();
            //2.遍历商品列表
            for (Long id : ids) {
                pageService.genItemHtml(id);
            }
            //提示
            System.out.println("生成静态页面成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
