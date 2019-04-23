package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/22 11:43
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
    @Reference(timeout = 50000) //timeout:设置访问服务的超时时间
    private BrandService brandService;

    /**
     * 查询所有的品牌（不带分页）
     * @return
     */
    @RequestMapping("/list")
    public List<TbBrand> findAll(){
        return brandService.findAll();
    }

    /**
     * 查询所有的品牌（带有分页功能）
     * @param page
     * @param pagesize
     * @return
     */
    @RequestMapping("/findByPage")
    public PageResult findByPage(int page,int pagesize){
        return brandService.findByPage(page,pagesize);
    }

    /**
     * 关于两个注解:@RequestBody与@ResponseBody的区别：
     * @RequestBody:代表将前台传来的 json串转换为java对象
     * @ResponseBody:代表将后台的java对象转换为json串并发送到前端
     * @param page
     * @param pagesize
     * @param brand
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(int page,int pagesize,@RequestBody(required = false) TbBrand brand){
        return brandService.search(page,pagesize,brand);
    }

    /**
     * 添加商品
     * @param brand
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"添加品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加品牌失败！");
        }
    }

    /**
     * 修改品牌
     * @param brand
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改品牌失败！");
        }
    }

    @RequestMapping("/delete")
    public Result delete(String[] ids){
        try {
            brandService.delete(ids);
//            List<String> newIds = new ArrayList<>();
//            for (String id : ids) {
//               if(id.matches("\\d+")){
//                   newIds.add(id);
//               }
//            }
//            brandService.delete(newIds.toArray(new String[newIds.size()]));
//            System.out.println(newIds);
            return new Result(true,"修改品牌成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改品牌失败！");
        }
    }

}
