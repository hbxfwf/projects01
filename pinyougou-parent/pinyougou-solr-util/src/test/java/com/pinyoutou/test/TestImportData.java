package com.pinyoutou.test;

import com.pinyougou.solr.util.SolrUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/30 10:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext*.xml")
public class TestImportData {
    @Autowired
    private SolrUtil solrUtil;
    @Test
    public void testImportIndex(){
        solrUtil.importData();
        System.out.println("保存成功！");
    }

//    public static void main(String[] args) {
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
//        SolrUtil solrUtil = (SolrUtil) context.getBean("solrUtil");
//        solrUtil.importData();
//        System.out.println("保存成功！");
//    }
}
