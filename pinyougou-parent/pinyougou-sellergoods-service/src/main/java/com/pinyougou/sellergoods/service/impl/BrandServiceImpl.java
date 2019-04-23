package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/22 11:41
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;

    /**
     * 查询所有的品牌
     * @return
     */
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    /**
     * 查询所有的品牌（带有分页功能）
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public PageResult findByPage(int page, int pagesize) {
        //1.开始分页
        PageHelper.startPage(page,pagesize);
        //2.查询所有的品牌
        TbBrandExample example = new TbBrandExample();
        List<TbBrand> tbBrands = brandMapper.selectByExample(example);
        //3.将上面的集合转换为Page<TbBrand>对象
        Page<TbBrand> brandList = (Page<TbBrand>) tbBrands;
        //4.构造自定义的分页page对象并返回
        return new PageResult(brandList.getTotal(),brandList.getResult());
    }

    @Override
    public PageResult search(int page, int pagesize, TbBrand brand) {
        //1.开始分页
        PageHelper.startPage(page,pagesize);
        //2.查询所有的品牌
        TbBrandExample example = new TbBrandExample();
        //3.为查询实例生成查询条件
        TbBrandExample.Criteria criteria = example.createCriteria();
        if(null != brand){
            if(StringUtils.isNotEmpty(brand.getName())){
                criteria.andNameLike("%" + brand.getName() + "%");
            }
            if(StringUtils.isNotEmpty(brand.getFirstChar())){
                criteria.andFirstCharLike("%" + brand.getFirstChar() + "%");
            }
        }
        //4.根据条件查询得到列表
        List<TbBrand> tbBrands = brandMapper.selectByExample(example);
        //5.转换上面得到的品牌列表为Page<TbBrand>对象
        Page<TbBrand> brandPage = (Page<TbBrand>) tbBrands;
        //6.返回自定义的PageResult对象
        return new PageResult(brandPage.getTotal(),brandPage.getResult());
    }

    /**
     * 添加品牌
     * @param brand
     */
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    /**
     * 修改品牌
     * @param brand
     */
    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKey(brand);
    }

    /**
     * 根据id删除品牌
     * @param ids
     */
    @Override
    public void delete(String[] ids) {
        for (String id : ids) {
            brandMapper.deleteByPrimaryKey(Long.parseLong(id));
        }
    }
}
