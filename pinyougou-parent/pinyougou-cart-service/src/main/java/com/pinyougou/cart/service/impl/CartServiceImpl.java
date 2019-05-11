package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/11 09:25
 */
@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 添加商品到购物车
     * @param cartList
     * @param itemId   sku商品编号
     * @return
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId,int num) {
        //1.根据sku商品编号itemId查询出tbItem对象
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //2.对查询出来的商品进行判断并处理
        if(tbItem == null){
            throw new RuntimeException("查询的商品不存在！");
        }
        if(!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法！");
        }
        //3.根据sku商品查询出商家id
        String sellerId = tbItem.getSellerId();
        //4.根据sellerId来查询出是否存在购物车对象
        Cart cart = searchCartBySellerId(cartList,sellerId);
        //5.判断是否有购物车对象
        if(cart == null){       //没有购物车对象
            createCart(cartList, num, tbItem);
        }else{                  //存在此购物车对象
            //6.从购物车中取得订单项集合
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            //7.在上面的订单项集合中根据itemId查询看是否有此订单项
            TbOrderItem orderItem = searchOrderItem(orderItemList,itemId);
            //7.1)根据查询结果判断是否存在订单项
            if(orderItem == null){      //① 不存在此订单项，就要重新生成一个订单项
                //7.1.1）生成新的订单项
                TbOrderItem orderItem1 = createOrderItem(tbItem, num);
                //7.1.2) 将订单项添加到购物车的订单列表中
                cart.getOrderItemList().add(orderItem1);
            }else{                      //② 存在此订单项，就修改商品数量及金额
                orderItem.setNum(orderItem.getNum() + num);  //修改数量
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
                //如果数量操作后小于等于0，则从订单项列表中移除此商品
                if(orderItem.getNum() <= 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //如果订单项总数小于等于0，就将此购物车从购物车列表中移除
                if(cart.getOrderItemList().size() <= 0){
                    cartList.remove(cart);
                }
            }
        }

        return cartList;
    }
    //保存到redis中
    @Override
    public void saveToRedis(String username, List<Cart> cartList) {
        System.out.println("将购物车列表保存到redis中");
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     *  将cookie中的购物车合并到redis中
      * @param cartList1 代表redis中的购物车
     * @param cartList2  代表cookie中的购物车
     * @return
     */
    @Override
    public List<Cart> mergeCart(List<Cart> cartList1, List<Cart> cartList2) {
        for (Cart cart : cartList2) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addGoodsToCartList(cartList1,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return cartList1;
    }

    /**
     *  登录登录名从redis中取购物车
     * @param username
     * @return
     */
    @Override
    public List<Cart> getCartListFromRedis(String username) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        if(cartList == null){
            cartList = new ArrayList<>();
        }
        return cartList;
    }

    //根据sku商品编号查询在订单项列表中查询是否有此订单项
    private TbOrderItem searchOrderItem(List<TbOrderItem> orderItemList,Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            //使用longvalue()将Long对象转换为long基本类型来进行比较
            if(orderItem.getItemId().longValue() == itemId.longValue()){
                return orderItem;
            }
        }
        return  null;
    }

    //创建购物车并放到购物车集合中
    private void createCart(List<Cart> cartList, int num, TbItem tbItem) {
        //5.1）创建一个购物车对象
        Cart cart1 = new Cart();
        //5.2）创建一个购物项列表
        List<TbOrderItem> orderItems = new ArrayList<>();
        //5.3）创建一个订单项（购物项）
        TbOrderItem orderItem = createOrderItem(tbItem,num);
        //5.4）将订单项放到集合中
        orderItems.add(orderItem);
        //5.5)将订单项集合与购物车绑定
        cart1.setOrderItemList(orderItems);
        cart1.setSellerId(tbItem.getSellerId());
        cart1.setSellerName(tbItem.getSeller());
        //5.6)将购物车放到购物车集合中
        cartList.add(cart1);
    }

    //通过sku商品创建一条订单项数据
    private TbOrderItem createOrderItem(TbItem tbItem,int num) {
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setSellerId(tbItem.getSellerId());
        orderItem.setNum(num);
        orderItem.setTitle(tbItem.getTitle());
        orderItem.setPicPath(tbItem.getImage());
        orderItem.setPrice(tbItem.getPrice());
        orderItem.setGoodsId(tbItem.getGoodsId());
        orderItem.setItemId(tbItem.getId());
        //设置小计
        orderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*num));
        return orderItem;
    }

    //通过商家id查询是否存在购物车对象
    private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){
                return cart;
            }
        }
        return null;
    }
}
