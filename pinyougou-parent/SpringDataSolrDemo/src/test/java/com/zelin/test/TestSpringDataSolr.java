package com.zelin.test;

import com.zelin.pojo.TbItem;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/30 09:14
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext*.xml")
public class TestSpringDataSolr {
    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 添加一条记录到索引库
     */
    @Test
    public void testAddIndex(){
        //1.构造要添加到索引库的对象
        TbItem tbItem = new TbItem();
        tbItem.setId(1L);
        tbItem.setPrice(new BigDecimal(1000));
        tbItem.setBrand("华为");
        tbItem.setCategory("手机");
        tbItem.setImage("images/huawei.jpg");
        tbItem.setSeller("华为");
        tbItem.setTitle("华为meta9");
        //2.添加到索引库
        solrTemplate.saveBean(tbItem);
        //3.提交
        solrTemplate.commit();
        System.out.println("添加成功！");
    }

    /**
     * 一次性添加多个到索引库
     */
    @Test
    public void testAddIndexes(){
        for(int i = 1;i <= 100;i++) {
            //1.构造要添加到索引库的对象
            TbItem tbItem = new TbItem();
            tbItem.setId(i + 1L);
            tbItem.setPrice(new BigDecimal(1000+i));
            tbItem.setBrand("华为");
            tbItem.setCategory("手机");
            tbItem.setImage("images/huawei"+i+".jpg");
            tbItem.setSeller("华为");
            tbItem.setTitle("华为meta"+i);
            //2.添加到索引库
            solrTemplate.saveBean(tbItem);
            //3.提交
            solrTemplate.commit();
        }
        System.out.println("添加多条记录成功！");
    }

    /**
     * 根据id查询记录
     */
    @Test
    public void testFindOne(){
        //1.根据id从索引库查询
        TbItem tbItem = solrTemplate.getById(10L, TbItem.class);
        //2.打印输出
        System.out.println(tbItem);
    }
    /**
     * 修改某条记录
     */
    @Test
    public void testUpdate(){
        //1.根据id查询出某条记录
        TbItem tbItem = solrTemplate.getById(10L, TbItem.class);
        //2.修改上面的对象
        tbItem.setTitle("经过修改了的【华为meta10】");
        solrTemplate.saveBean(tbItem);
        //3.提交修改
        solrTemplate.commit();
        System.out.println("修改成功！");
    }

    /**
     * 根据id删除某条记录
     */
    @Test
    public void testDelete(){
        //1.根据id删除
        solrTemplate.deleteById("10");
        //2.提交
        solrTemplate.commit();
        System.out.println("删除成功！");
    }

    /**
     * 根据查询条件删除
     */
    @Test
    public void testDelByQuery(){
        //1.构造查询条件
        SolrDataQuery query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title");
        criteria.contains("2");  //只要包含2就会查询出来
        query.addCriteria(criteria);
        //2.根据查询条件删除
        UpdateResponse response = solrTemplate.delete(query);
        //3.提交
        solrTemplate.commit();
    }
    @Test
    public void testPage(){
        //1.构造查询条件
        SimpleQuery query = new SimpleQuery("*:*");
        //2.添加查询条件
        Criteria criteria = new Criteria("item_title");
        criteria.contains("3");
        query.addCriteria(criteria);
        //3.设置分页选项
        query.setOffset(0);    //默认从0开始
        query.setRows(10);     //每页显示的条数
        //4.进行分页查询
        ScoredPage<TbItem> pageItems = solrTemplate.queryForPage(query, TbItem.class);
        //4.1)得到分页后的总记录数
        long totalElements = pageItems.getTotalElements();
        int totalPages = pageItems.getTotalPages();
        System.out.println("总记录数：" + totalElements + ",总页数：" + totalPages);
        //4.2)得到每页的集合
        List<TbItem> content = pageItems.getContent();
        //4.3)打印记录
        for (TbItem tbItem : content) {
            System.out.println(tbItem);
        }
        //5.提交
        solrTemplate.commit();
    }

    /**
     * 删除索引库所有记录
     */
    @Test
    public void testDeleteAll(){
        //1.构造查询对象
        SolrDataQuery query = new SimpleQuery("*:*");
        //2.删除
        solrTemplate.delete(query);
        //3.提交
        solrTemplate.commit();
    }
}
