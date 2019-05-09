package com.zelin.listener;

import com.aliyuncs.exceptions.ClientException;
import com.zelin.jms.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 定义监听器
 * @Date: Create in 2019/5/9 09:32
 */
@Component
public class MyMessageListener {
    @Autowired
    private SmsUtils smsUtils;
    //监听获取文本数据
    @JmsListener(destination = "zelin")
    public void readMessage(String text){
        System.out.println(text);
    }
    //监听map数据
    @JmsListener(destination = "zelin_map")
    public void readMessageMap(Map map){
        System.out.println(map);
    }

    //监听到控制器发来的map信息
    @JmsListener(destination = "sms")
    public void getMessage(Map<String,String> map) throws ClientException {
        //监听到控制器发来的消息后，再向阿里大于发送消息
        smsUtils.sendSms(map.get("phone"),map.get("signName"),map.get("templateCode"),map.get("param"));
    }
}
