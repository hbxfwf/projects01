package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/7 10:02
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    /**
     * 生成静态页面
     * @param goodsId
     * @return
     */
    @Override
    public boolean genItemHtml(Long goodsId) {
        try {
            //1.通过freeMarkerConfigurer得到配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2.得到模板对象
            Template template = configuration.getTemplate("item.ftl");

            //3.定义dataModel对象，用于存放数据
            Map dataModel = new HashMap<>();
            //4.从数据库中根据当前的goodsId，查询goods表的信息
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            //5.从数据库中根据当前的goodsId，查询goodsDesc表的信息
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            //6.将上面查询到的goods与goodsDesc表的数据放到dataModel数据模型中
            dataModel.put("goods",tbGoods);
            dataModel.put("goodsDesc",goodsDesc);
            //添加商品的三级分类到dataModel中
            String category1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName(); //一级分类
            String category2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName(); //二级分类
            String category3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName(); //三级分类
            //将三级分类放到dataModal中
            dataModel.put("category1",category1);
            dataModel.put("category2",category2);
            dataModel.put("category3",category3);
            //添加sku商品列表信息
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");
            criteria.andGoodsIdEqualTo(goodsId);
            example.setOrderByClause("is_default desc"); //默认将最大的is_default值放到前面，方便后面获取
            //查询sku商品列表
            List<TbItem> itemList = itemMapper.selectByExample(example);
            //将sku商品列表添加到dataModal中
            dataModel.put("itemList",itemList);
            //7.定义输出流对象
            Writer out = new FileWriter(pagedir + goodsId + ".html");
            //8.生成静态页面
            template.process(dataModel,out);
            //9.关闭流
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return false;
    }
}
