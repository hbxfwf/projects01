package com.zelin.test;

import com.zelin.springjms.QueueProducer;
import com.zelin.springjms.TopicProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 测试发布/订阅发送消息
 * @Date: Create in 2019/5/8 10:04
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-producer.xml")
public class TestTopicProducer {
    @Autowired
    private TopicProducer topicProducer;
    @Test
    public void test01(){
        topicProducer.sendMessage();
    }
}
