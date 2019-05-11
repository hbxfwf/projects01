package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.pojo.Result;
import com.pinyougou.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/11 09:54
 */
@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference(timeout = 40000)
    private CartService cartService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    /**
     * 查询购物车列表
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart>  findCartList(){
        //1.从cookie中取出购物车
        String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        //2.判断是否存在
        if(cartListString == null || cartListString.equals("")){
            cartListString = "[]";
        }
        //2.1）取得当前用户的登录名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //3.转换为集合
        List<Cart> cartList = JSON.parseArray(cartListString, Cart.class);
        //2.2)判断是否登录
        if("anonymousUser".equals(username)){  //代表未登录,返回的就是cookie中的购物车信息
            return cartList;
        }else{                                  //代表己登录
            //① 从redis中根据登录名取出购物车列表
            List<Cart> redisCartList = cartService.getCartListFromRedis(username);
            //② 合并两个购物车
            //判断cookie中是否有购物车数据
            if(cartList.size() > 0){
                //1.合并cookie中的购物车到redis中
                List<Cart> newCartList = cartService.mergeCart(redisCartList, cartList);
                //2.将新的购物车保存到redis中
                cartService.saveToRedis(username,newCartList);
                //3.删除以前cookie中的购物车
                CookieUtil.deleteCookie(request,response,"cartList");
                return newCartList;
            }
            //如果cookie中没有购物车就返回redis中的购物车列表
            return redisCartList;
        }
    }
    /**
     * 添加到购物车集合
     * @param itemId
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId,int num){
        try {
            //1.将购物车列表查询出来
            List<Cart> cartList = findCartList();
            System.out.println("cartList:" + cartList);
            //2.添加商品到购物车列表中
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);
            //3.将购物车添加到cookie中
            CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"UTF-8");
            return new Result(true,"添加到购物车成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加到购物车失败！");
        }

    }
}
