package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/4/25 09:49
 */
@RestController
public class LoginController {
    /**
     * 取得登录名
     * @return
     */
    @RequestMapping("/name")
    public Map findName(){
        Map map = new HashMap();
        //1.取得登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.将登录名放到map中
        map.put("name",name);
        return map;
    }
}
