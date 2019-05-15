package com.pinyougou.seckill.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillGoodsService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description:
 * @Date: Create in 2019/5/14 10:18
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WeixinPayService payService;
    @Reference
    private SeckillGoodsService seckillGoodsService;
    @Autowired
    private IdWorker idWorker;
    @Reference
    private OrderService orderService;
    //微信下单
    @RequestMapping("/createNative")
    public Map createNative(){
        //1.得到当前登录的用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.根据当前登录用户从redis中取出其秒杀订单信息
        TbSeckillOrder order = seckillGoodsService.findSeckillOrderFromRedis(userId);
        if(order != null){
            double money = order.getMoney().doubleValue() * 100;
            //3.向微信后台发出下单请求
            return payService.createNative(order.getId() + "", money + "");

        }else{
            return new HashMap();
        }

    }
    //订单查询:原理：每隔3秒就去微信后台查询一次
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
        //得到当前登录用户id
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        int x = 0;
        while(true){
            try {
                /**
                 * Trade_State 交易状态可取值如下：（参考官方文档）
                 * SUCCESS—支付成功
                 *
                 * REFUND—转入退款
                 *
                 * NOTPAY—未支付
                 *
                 * CLOSED—已关闭
                 *
                 * REVOKED—已撤销（付款码支付）
                 *
                 * USERPAYING--用户支付中（付款码支付）
                 *
                 * PAYERROR--支付失败(其他原因，如银行返回失败)
                 *
                 * 支付状态机请见下单API页面
                 */
                //查询订单
                Map map = payService.queryPayStatus(out_trade_no);
                if(map == null){
                    result = new Result(false,"支付失败！");
                    break;
                }else if(map.get("trade_state").equals("SUCCESS")){   //查询成功！
                    result = new Result(true,"支付成功成功！");
                    //参数1：当前登录用户id
                    //参数2：订单号
                    //参数3：微信后台返回的流水号
                    seckillGoodsService.saveOrderFromRedisToDB(userId,out_trade_no,map.get("transaction_id"));
                    break;
                }
                x++;
                if(x > 20){   //到达1分钟就可以退出程序
                    result = new Result(false,"二维码超时");
                    Map closeMap = payService.closePay(out_trade_no);  //调用关单
                    //下面的条件代表正常关单并且己支付(上面的关单请求失败)
                    if(!closeMap.get("return_code").equals("SUCCESS") && closeMap.get("err_code").equals("ORDERPAID")){
                        seckillGoodsService.saveOrderFromRedisToDB(userId,out_trade_no,map.get("transaction_id"));
                    }
                    if(result.isSuccess() == false){  //说明：二维码超时,清理redis中的订单，并更新redis中的商品
                        seckillGoodsService.deleteOrderFromRedis(userId,Long.parseLong(out_trade_no));
                    }
                    break;
                }
                //每隔3秒查询一次
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                result = new Result(false,"支付失败！");
            }
        }
        return result;
    }
}
