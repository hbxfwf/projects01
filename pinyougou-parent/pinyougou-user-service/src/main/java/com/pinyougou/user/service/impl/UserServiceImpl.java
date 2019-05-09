package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/9 10:55
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination sms;
    //添加用户
    @Override
    public void add(TbUser user) {
        //修改用户创建及修改时间
        user.setCreated(new Date());
        user.setUpdated(new Date());
        //设置密码为md5加密
        String password = DigestUtils.md5Hex(user.getPassword());
        user.setPassword(password);
        //添加用户
        userMapper.insert(user);
    }

    /**
     * 根据手机号生成验证码
     * @param phone
     */
    @Override
    public void createCode(final String phone) {
        //1.生成一个六位数的验证码
        final String num = (long) (Math.random()*1000000) + "";
        //2.将上面生成的验证码放到redis中
        redisTemplate.boundHashOps("validCode").put(phone,num);
        //3.向手机发送验证码
        //3.1)发送信息到微服务，由微服务的监听器再向阿里大于后台发送请求，由阿里大于向你的手机发送短信
        jmsTemplate.send(sms, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                //3.2)构造MapMessage向微服务后台发送消息
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phone",phone);
                mapMessage.setString("signName","品优购");
                mapMessage.setString("templateCode","SMS_148593167");
                //3.2）定义参数的map对象
                Map paramMap = new HashMap();
                paramMap.put("code",num);
                //3.3)将map对象转换为json串
                String param = JSON.toJSONString(paramMap);
                mapMessage.setString("param",param);
                return mapMessage;
            }
        });
    }

    /**
     * 验证码验证
     * @param checkCode
     * @return
     */
    @Override
    public boolean isCheckCode(String checkCode,String phone) {
        //1.从redis中取出验证码
        String validCode = (String) redisTemplate.boundHashOps("validCode").get(phone);
        //2.比较两次验证码是否相等
        if(validCode == null) return false;
        //3.根据两次的验证码是否相等决定返回的值
        return validCode.equals(checkCode);
    }
}
