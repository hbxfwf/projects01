package com.pinyougou.cart.service;

import com.pinyougou.group.Cart;

import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/11 09:23
 */
public interface CartService {
    //定义添加到购物车方法
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId,int num);

    //存放购物车到redis中
    public void saveToRedis(String username,List<Cart> cartList);

    //合并购物车
    public List<Cart> mergeCart(List<Cart> cartList1,List<Cart> cartList2);

    //从redis中取出购物车
    public List<Cart> getCartListFromRedis(String username);
}
