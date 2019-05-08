package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public Map<String, Object> search(Map searchMap) {
        //0.构造map对象用于存放查询结果
        Map<String, Object> maps = new HashMap<>();
        //1.定义查询对象
//        Query query = new SimpleQuery("*:*");
//        //2.为查询对象添加条件(在item_keywords这个域中进行查询)
//        Criteria criteria = new Criteria("item_keywords");
//        String keywords = (String) searchMap.get("keywords");
//        System.out.println("keywords:" + keywords);
//        //3.如果条件的关键字不为null，就添加查询条件
//        if(StringUtils.isNotEmpty(keywords)){
//            criteria.is(keywords);
//        }
//        //4.将查询条件添加到查询对象中
//        query.addCriteria(criteria);
//        //5.进行分页查询
//        //5.1)设置分页查询的选项
//        query.setOffset(0);
//        query.setRows(40);
//        //5.2)进行分页查询
//        ScoredPage<TbItem> pageItems = solrTemplate.queryForPage(query, TbItem.class);
//        //5.3)取出查询的结果
//        List<TbItem> itemList = pageItems.getContent();
//        //5.4)将查询结果存放到maps集合中
//        maps.put("rows",itemList);
        Object keywords = searchMap.get("keywords");
        //处理原始map数据中的空格
        if(keywords != null){
            searchMap.put("keywords",searchMap.get("keywords").toString().replace(" ",""));
        }
        //1.高亮查询
        maps.putAll(searchList(searchMap));
        //2.分组查询
        List<String> categorys = searchCategoryList(searchMap);
        maps.put("categoryList",categorys);
        //3.根据分类查询出品牌列表及规格列表
        String category = (String) searchMap.get("category");
        if(StringUtils.isNotEmpty(category)){
            //3.1）取得第一个默认分类进行查询，得到此分类下的品牌及规格列表
            Map brandsAndSpecList = findBrandsAndSpecList(category);
            //3.2）将上面得到的规格及品牌列表放到大的map中
            maps.putAll(brandsAndSpecList);
        }else if(categorys.size() > 0){
            //3.1）取得第一个默认分类进行查询，得到此分类下的品牌及规格列表
            Map brandsAndSpecList = findBrandsAndSpecList(categorys.get(0));
            //3.2）将上面得到的规格及品牌列表放到大的map中
            maps.putAll(brandsAndSpecList);
        }
        return maps;
    }
    //导入到索引库
    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
        System.out.println("导入到索引库成功！");
    }

    /**
     * 根据商品id从索引库中删除商品
     * @param goodsIds
     */
    @Override
    public void deleteGoodsId(List goodsIds) {
        //注意：这里不能根据id查询，因为这里的goodsid不是tbItem表的主键,只能使用条件查询去删除
        SolrDataQuery query = new SimpleQuery();
        query.addCriteria(new Criteria("item_goodsid").in(goodsIds));
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 搜索条件查询
     * @param maps
     * @return
     */
    private Map searchList(Map<String,Object> maps){
        //1.高亮查询
        //1.1)定义高亮查询对象
        HighlightQuery query = new SimpleHighlightQuery();
        //1.1.1)构造查询条件
        Criteria criteria = new Criteria("item_keywords");
        criteria.is(maps.get("keywords"));
        //1.1.2)将高亮查询与查询条件绑定
        query.addCriteria(criteria);

        //下面开始过滤查询(分类、品牌、规格、价格区间、排序)
        //① 分类过滤查询
        String category = (String) maps.get("category");
        if(StringUtils.isNotEmpty(category)){
            FilterQuery categoryFilterQuery = new SimpleFilterQuery(new Criteria("item_category").is(category));
            query.addFilterQuery(categoryFilterQuery);
        }
       //② 品牌过滤查询
        String brand = (String) maps.get("brand");
        if(StringUtils.isNotEmpty(brand)){
            FilterQuery brandFilterQuery = new SimpleFilterQuery(new Criteria("item_brand").is(brand));
            query.addFilterQuery(brandFilterQuery);
        }
        //③ 规格过滤查询
        if(maps.get("spec") != null){
            Map specMap = JSON.parseObject(maps.get("spec").toString(),Map.class);
            if(specMap.size() > 0){
                for (Object  key : specMap.keySet()) {
                    FilterQuery specFilterQuery = new SimpleFilterQuery(new Criteria("item_spec_"+key).is(maps.get(key).toString()));
                    query.addFilterQuery(specFilterQuery);
                }
            }
        }
        //④ 价格区间查询
        String price = (String) maps.get("price");
        if(StringUtils.isNotEmpty(price)){
            //1.进行字符串的拆分（按照-号进行拆分）
            String[] split = price.split("-");
            //2.根据这个数组的第一个值是否是0确定查询起始范围
            if(!split[0].equals("0")){
                FilterQuery priceFilterQuery = new SimpleFilterQuery(new Criteria("item_price").greaterThanEqual(split[0]));
                query.addFilterQuery(priceFilterQuery);
            }
            //0-500 1000-2000 3000-*
            if(!split[1].equals("*")){
                FilterQuery priceFilterQuery = new SimpleFilterQuery(new Criteria("item_price").lessThan(split[1]));
                query.addFilterQuery(priceFilterQuery);
            }
        }
        //⑤ 排序查询
        String sortStr = (String) maps.get("sort");                //排序方向（升序还是降序）
        String sortField = (String) maps.get("sortField");      //排序字段
        Sort sort = null;
        //构造排序对象
        if(StringUtils.isNotEmpty(sortStr)){
            if(sortStr.equals("ASC")){      //升序排序
                sort = new Sort(Sort.Direction.ASC,"item_"+sortField);
            }else if(sortStr.equals("DESC")){
                sort = new Sort(Sort.Direction.DESC,"item_"+sortField);
            }
        }
        //将排序对象添加到查询对象中
        query.addSort(sort);
        //1.2)定义高亮查询的选项
        HighlightOptions options = new HighlightOptions().addField("item_title");
        options.setSimplePrefix("<span style='color:red'>");   //设置高亮选项的前缀
        options.setSimplePostfix("</span>");                   //设置高亮选项的后缀
        //1.3)将高亮选项与高亮查询对象绑定
        query.setHighlightOptions(options);
        //分页查询
        //1.处理分页数据
        String pageStr = (String) maps.get("page");
        String pagesizeStr = (String) maps.get("pagesize");
        Integer page = 1;
        Integer pagesize = 20;
        if(StringUtils.isNotEmpty(pageStr)){
            page = Integer.parseInt(pageStr);
        }
        if(StringUtils.isNotEmpty(pagesizeStr)){
            pagesize = Integer.parseInt(pagesizeStr);
        }
        //2.定义分页选项
        query.setOffset((page-1)*pagesize);
        query.setRows(pagesize);
        //1.4)进行高亮查询,返回高亮页对象
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //1.5)查询得到高亮的入口对象
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        //1.6)遍历高亮入口对象
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            //1.6.1）得到高亮的实体对象
            TbItem entity = highlightEntry.getEntity();
            //1.6.2）得到高亮字段集合
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            //1.6.3)如果当前高亮字段有值，就得到其第一个多域的值并为entity的标题字段item_title重新赋值
            if(highlights != null && highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0){
                //highlights.get(0).getSnipplets().get(0)这个值其实就是带有高亮的前缀和后缀的值
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        //1.7)将设置了高亮字段后的entity放到map集合中
        Map highlightMap = new HashMap();
        highlightMap.put("rows",tbItems.getContent());
        highlightMap.put("total",tbItems.getTotalElements());
        highlightMap.put("totalPage",tbItems.getTotalPages());
        return highlightMap;
    }

    /**
     * 根据关键字查询分类列表
     * @param map
     * @return  分类的列表
     */
    private List<String> searchCategoryList(Map map){
        List<String> categoryList = new ArrayList<>();
        //1.定义查询对象
        Query query = new SimpleQuery();
        //2.为查询对象设置查询条件
        query.addCriteria(new Criteria("item_keywords").is(map.get("keywords")));
        //3.定义分组查询的选项
        GroupOptions options = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(options); //将分组选项与查询对象绑定
        //4.进行分组查询
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //5.得到分组字段的结果
        GroupResult<TbItem> resultCategory = groupPage.getGroupResult("item_category");
        //6.获取分组入口对象
        Page<GroupEntry<TbItem>> groupEntries = resultCategory.getGroupEntries();
        //7.得到分组条目的内容集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        //8.遍历分组条目
        for (GroupEntry<TbItem> groupEntity : content) {
            //8.1)得到分组的值
            String groupValue = groupEntity.getGroupValue();
            //8.2)将分组的值放到集合中
            categoryList.add(groupValue);
        }

        //7.返回分组后的结果
        return categoryList;
    }

    /**
     * 根据分类名称从redis中查询出规格及品牌列表
     * @param category
     * @return
     */
    private Map findBrandsAndSpecList(String category){
        Map maps = new HashMap();
        //1.从redis中根据分类查询对应的模板id
        Long typeId = (Long) redisTemplate.boundHashOps("categoryLists").get(category);
        //2.从redis中根据模板id查询出对应的规格及品牌列表
        List<Map> brandLists = (List<Map>) redisTemplate.boundHashOps("brandLists").get(typeId);
        List<Map> specLists = (List<Map>)redisTemplate.boundHashOps("specLists").get(typeId);
        //3.将规格列表及品牌列表放到map中
        maps.put("brandLists",brandLists);
        maps.put("specLists",specLists);
        return maps;
    }
}
