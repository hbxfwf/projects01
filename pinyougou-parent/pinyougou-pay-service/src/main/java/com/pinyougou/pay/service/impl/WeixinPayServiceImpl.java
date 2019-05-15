package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Feng.Wang
 * @Company: Zelin.ShenZhen
 * @Description: 微信支付服务层实现
 * @Date: Create in 2019/5/14 09:51
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;
    @Value("${appid}")
    private String appid;
    //微信后台统一下单的url地址
    private String orderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信后台查询订单的url
    private String queryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
    //微信后台关闭订单的url
    private String closeUrl = "https://api.mch.weixin.qq.com/pay/closeorder";
    //微信支付下单
    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        try {
            //1.构造要发送的参数对象
            Map<String,String> params = new HashMap<>();
            params.put("appid",appid);                //公众账号ID
            params.put("mch_id",partner);             //商户号
            params.put("nonce_str", WXPayUtil.generateNonceStr()); //生成随机字符串
            params.put("sign","品优购");               //签名
            params.put("body","好商品");               //商品描述
            params.put("out_trade_no",out_trade_no);   //商户订单号
            params.put("total_fee",total_fee);         //订单金额
            params.put("spbill_create_ip","127.0.0.1");//终端IP
            params.put("notify_url","http://test.zelininfo.com");                   //通知地址
            params.put("trade_type","NATIVE ");         //交易类型
            //1.1）根据上面的参数和商户的密钥生成一个xml数据
            String paramsXml = WXPayUtil.generateSignature(params, partnerkey);

            //2.构造HttpClient对象并发送数据
            HttpClient httpClient = new HttpClient(orderUrl);   //参数为下单地址
            httpClient.setHttps(true);                          //设置发送的请求方式为http
            httpClient.setXmlParam(paramsXml);                  //设置发送的xml参数
            httpClient.post();                                  //发送请求和微信的后台


            //3.接受返回的结果并返回
            String contentXml = httpClient.getContent();                    //返回一个xml结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml); //将xml数据转换为map

            //4.构造要返回的Map对象
            Map map = new HashMap();
            map.put("code_url",resultMap.get("code_url"));                  //返回支付的二维码地址
            map.put("out_trade_no",out_trade_no);                           //订单号
            map.put("total_fee",total_fee);                                 //总金额

            //5.返回
            return map;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //1.构造要发送的参数对象
            Map<String,String> params = new HashMap<>();
            params.put("appid",appid);                //公众账号ID
            params.put("mch_id",partner);             //商户号
            params.put("nonce_str", WXPayUtil.generateNonceStr()); //生成随机字符串
            params.put("sign","品优购");               //签名
            params.put("out_trade_no",out_trade_no);   //商户订单号
            //1.1）根据上面的参数和商户的密钥生成一个xml数据
            String paramsXml = WXPayUtil.generateSignature(params, partnerkey);

            //2.构造HttpClient对象并发送数据
            HttpClient httpClient = new HttpClient(queryUrl);   //参数为下单地址
            httpClient.setHttps(true);                          //设置发送的请求方式为http
            httpClient.setXmlParam(paramsXml);                  //设置发送的xml参数
            httpClient.post();                                  //发送请求和微信的后台

            //3.接受返回的结果并返回
            String contentXml = httpClient.getContent();                    //返回一个xml结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml); //将xml数据转换为map

            return resultMap;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭订单
     * @param out_trade_no
     * @return
     */
    @Override
    public Map closePay(String out_trade_no) {
        try {
            //1.构造要发送的参数对象
            Map<String,String> params = new HashMap<>();
            params.put("appid",appid);                //公众账号ID
            params.put("mch_id",partner);             //商户号
            params.put("nonce_str", WXPayUtil.generateNonceStr()); //生成随机字符串
            params.put("sign","品优购");               //签名
            params.put("out_trade_no",out_trade_no);   //商户订单号
            //1.1）根据上面的参数和商户的密钥生成一个xml数据
            String paramsXml = WXPayUtil.generateSignature(params, partnerkey);

            //2.构造HttpClient对象并发送数据
            HttpClient httpClient = new HttpClient(closeUrl);   //参数为关单地址
            httpClient.setHttps(true);                          //设置发送的请求方式为http
            httpClient.setXmlParam(paramsXml);                  //设置发送的xml参数
            httpClient.post();                                  //发送请求和微信的后台

            //3.接受返回的结果并返回
            String contentXml = httpClient.getContent();                    //返回一个xml结果
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml); //将xml数据转换为map

            return resultMap;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
