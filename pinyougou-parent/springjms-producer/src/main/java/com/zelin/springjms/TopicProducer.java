package com.zelin.springjms;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:发布/订阅模式--发送方
 * @Date: Create in 2019/5/8 10:10
 */
@Component
public class TopicProducer {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Topic activeMQTopic;
    public void sendMessage(){
        jmsTemplate.send(activeMQTopic, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage("发布/订阅+springJMS消息...");
            }
        });
    }
}
