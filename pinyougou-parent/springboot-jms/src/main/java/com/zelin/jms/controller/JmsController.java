package com.zelin.jms.controller;

import com.zelin.jms.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/9 09:29
 */
@RestController
public class JmsController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    //发送普通文件消息
    @RequestMapping("/send")
    public void sendMessage(String text){
        jmsMessagingTemplate.convertAndSend("zelin",text);
    }
    //发送map数据
    @RequestMapping("/sendMap")
    public void sendMessagemap(){
         Map map = new HashMap();
         map.put("phone","13345678900");
         map.put("addr","上海");
        jmsMessagingTemplate.convertAndSend("zelin_map",map);
    }
    @RequestMapping("/sendMsg")
    public void sendMsg(){
       Map<String,String> map = new HashMap();
       map.put("phone","15179287025");
       map.put("signName","品优购");
       map.put("templateCode","SMS_148593167");
       map.put("param","{'code':'234512'}");
       jmsMessagingTemplate.convertAndSend("sms",map);
    }
}
