package com.pinyougou.cart.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.Result;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    private IdWorker idWorker;
    @Reference
    private OrderService orderService;
    //微信下单
    @RequestMapping("/createNative")
    public Map createNative(){
        return payService.createNative(idWorker.nextId()+"","1");
    }
    //订单查询:原理：每隔3秒就去微信后台查询一次
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;
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
                    //修改订单状态
                    orderService.updateOrderStatus(out_trade_no,map.get("transaction_id").toString());
                    break;
                }
                x++;
                if(x > 100){   //到达5分钟就可以退出程序
                    result = new Result(false,"二维码超时");
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
