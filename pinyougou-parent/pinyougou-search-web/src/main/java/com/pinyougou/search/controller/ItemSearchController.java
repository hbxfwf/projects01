package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/30 11:08
 */
@RestController
@RequestMapping("/itemSearch")
public class ItemSearchController {
    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 根据前端传过来的数据进行搜索
     * @param map
     * @return
     */
    @RequestMapping("/search")
    public Map search(@RequestBody Map<String,String> map){
        System.out.println("map:" + map);
        return itemSearchService.search(map);
    }
}
