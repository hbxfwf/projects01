package com.pinyougou.user.controller;

import com.pinyougou.pojo.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/10 11:22
 */
@RestController
public class LoginController {
    /**
     * 获取用户名
     * @return
     */
    @RequestMapping("/login")
    public Result loginName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return new Result(true,name);
    }
}
