package com.pinyougou.user.service;

import com.pinyougou.pojo.TbUser;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/9 10:55
 */
public interface UserService {
    public void add(TbUser user);

    /**
     * 生成验证码
     * @param phone
     */
    void createCode(String phone);

    boolean isCheckCode(String checkCode,String phone);
}
