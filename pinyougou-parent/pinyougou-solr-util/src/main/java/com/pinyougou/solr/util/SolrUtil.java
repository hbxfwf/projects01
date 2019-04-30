package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/30 10:04
 */
@Component
public class SolrUtil {
    @Autowired
    private TbItemMapper tbItemMapper;
    @Autowired
    private SolrTemplate solrTemplate;
    public void importData(){
        //1.从数据库中查询tbItem表中的所有记录
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");  //审核通过
        List<TbItem> itemList = tbItemMapper.selectByExample(example);
        //2.遍历商品列表
        for (TbItem tbItem : itemList) {
            //2.1)得到tbItem中的spec这个json串
            String spec = tbItem.getSpec();
            //2.2）将上面json串转换为map对象
            Map<String,String> specMap = JSON.parseObject(spec, Map.class);
            //2.3)为tbItem设置specMap属性
            tbItem.setSpecMap(specMap);
        }
        //3.将上面的集合保存到索引库中
        solrTemplate.saveBeans(itemList);
        //4.提交保存
        solrTemplate.commit();
    }
}
