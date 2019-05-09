package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/9 11:11
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Reference(timeout = 50000)
    private UserService userService;
    /**
     * 根据手机号获取验证码
     * @param phone
     * @return
     */
    @RequestMapping("/getCheckCode")
    public Result getCheckCode(String phone){
        try {
            userService.createCode(phone);
            return new Result(true,"短信发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"短信发送失败！");
        }
    }

    /**
     * 添加用户
     * @param user  要添加的用户
     * @param checkCode 用户的验证码
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody  TbUser user,String checkCode){
        //1.验证手机号是否合法
        if(!PhoneFormatCheckUtils.isChinaPhoneLegal(user.getPhone())){
            return new Result(false,"手机号输入有误！");
        }
        //2.对验证码进行验证
        boolean flag = userService.isCheckCode(checkCode,user.getPhone());
        //3.根据验证码返回结果返回不同的Result
        if(flag){   //验证码通过，添加用户
            userService.add(user);
            return new Result(true,"添加用户成功！");
        }else{
            return new Result(false,"输入的验证码有误！");
        }
    }
}
