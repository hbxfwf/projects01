package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/22 11:39
 */
public interface BrandService {
    public List<TbBrand> findAll();

    PageResult findByPage(int page, int pagesize);

    PageResult search(int page, int pagesize, TbBrand brand);

    void add(TbBrand brand);

    void update(TbBrand brand);

    void delete(String[] ids);
}
