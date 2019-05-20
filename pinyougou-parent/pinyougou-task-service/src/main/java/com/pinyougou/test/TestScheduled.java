package com.pinyougou.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Date;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/16 09:27
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class TestScheduled {
    @Test
    public void test01(){
        try {
            //让spring容器一直运行
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
