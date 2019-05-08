package com.zelin;

import com.zelin.springjms.MyMessageListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:测试点对点监听
 * @Date: Create in 2019/5/8 10:02
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-consumer.xml")
public class testQueueConsumer {
    @Test
    public void test01() throws IOException {
        System.in.read();
    }
}
