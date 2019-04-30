package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.ScoredPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/30 10:43
 */
@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    @Autowired
    private SolrTemplate solrTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //0.构造map对象用于存放查询结果
        Map<String, Object> maps = new HashMap<>();
        //1.定义查询对象
        Query query = new SimpleQuery("*:*");
        //2.为查询对象添加条件(在item_keywords这个域中进行查询)
        Criteria criteria = new Criteria("item_keywords");
        String keywords = (String) searchMap.get("keywords");
        System.out.println("keywords:" + keywords);
        //3.如果条件的关键字不为null，就添加查询条件
        if(StringUtils.isNotEmpty(keywords)){
            criteria.is(keywords);
        }
        //4.将查询条件添加到查询对象中
        query.addCriteria(criteria);
        //5.进行分页查询
        //5.1)设置分页查询的选项
        query.setOffset(0);
        query.setRows(40);
        //5.2)进行分页查询
        ScoredPage<TbItem> pageItems = solrTemplate.queryForPage(query, TbItem.class);
        //5.3)取出查询的结果
        List<TbItem> itemList = pageItems.getContent();
        //5.4)将查询结果存放到maps集合中
        maps.put("rows",itemList);
        return maps;
    }
}
