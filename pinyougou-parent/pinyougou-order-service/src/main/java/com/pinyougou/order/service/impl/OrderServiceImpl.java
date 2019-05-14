package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.pojo.TbOrderExample.Criteria;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private TbOrderItemMapper orderItemMapper;
    @Autowired
    private TbPayLogMapper payLogMapper;
    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //1.从redis中得到购物车信息
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        //定义存放订单列表的集合
        List<String> orderList = new ArrayList<>();
        //2.向tborder表添加数据
        for (Cart cart : cartList) {
            //2.1）构造订单对象
            long orderId = idWorker.nextId();
            orderList.add(orderId + "");
            System.out.println("orderId:" + orderId);
            TbOrder tbOrder = new TbOrder();
            tbOrder.setOrderId(orderId);
            tbOrder.setSourceType(order.getSourceType());
            tbOrder.setUserId(order.getUserId());
            tbOrder.setReceiverMobile(order.getReceiverMobile());
            tbOrder.setSellerId(order.getSellerId());
            tbOrder.setCreateTime(new Date());
            tbOrder.setUpdateTime(new Date());
            tbOrder.setReceiverAreaName(order.getReceiverAreaName());
            tbOrder.setReceiver(order.getReceiver());

            tbOrder.setPaymentType(order.getPaymentType());
            tbOrder.setStatus("1");

            //3.遍历购物车添加数据到tbOrderItem表中
            double money = 0;

            //3.1)遍历购物车的购物项集合，向订单项中添加数据
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                orderItem.setSellerId(order.getSellerId());
                orderItem.setOrderId(orderId);
                orderItem.setId(idWorker.nextId());
                orderItem.setTotalFee(new BigDecimal(orderItem.getNum() * orderItem.getPrice().doubleValue()));
                money += orderItem.getTotalFee().doubleValue();
                orderItemMapper.insert(orderItem);
            }
            order.setPayment(new BigDecimal(money));
            //4.添加订单
            orderMapper.insert(tbOrder);

            //5.如果支付类型为微信支付，就在支付日志表中添加记录
            if(order.getPaymentType().equals("1")){  //代表微信支付
                TbPayLog payLog = new TbPayLog();
                payLog.setUserId(order.getUserId());
                payLog.setTotalFee(new Long(money*100+""));
                payLog.setTradeState("0");  //代表未支付
                payLog.setCreateTime(new Date());
                payLog.setOutTradeNo(idWorker.nextId()+"");  //订单号
                payLog.setPayTime(new Date());
                payLog.setPayType("1");                      //支付类型
                String orders = orderList.toString().replace("[", "").replace("]", "").replace(" ", "");
                payLog.setOrderList(orders);
                //插入到数据库中
                payLogMapper.insert(payLog);
                //将日志信息放到redis中
                redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);
            }


        }
    }

    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            orderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }

        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 修改订单状态与日志中的订单状态
     * @param out_trade_no 支付日志的编号
     * @param transaction_id 微信返回的交易流水号
     */
    @Override
    public void updateOrderStatus(String out_trade_no, String transaction_id) {
       //1.根据支付日志编号查询支付日志对象
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        payLog.setTransactionId(transaction_id);  //从微信支付平台返回的流水号
        payLog.setPayTime(new Date());
        payLog.setTradeState("1");
       //2.修改日志
        payLogMapper.updateByPrimaryKey(payLog);

        //3.得到日志中的订单id值
        String orderList = payLog.getOrderList();
        String[] split = orderList.split(",");
        for (String orderId : split) {
            //3.1)根据订单编号查询出订单对象
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            tbOrder.setStatus("2");         //代表支付成功
            tbOrder.setPaymentTime(new Date());

            //3.2)修改
            if(tbOrder != null){
                orderMapper.updateByPrimaryKey(tbOrder);
            }
        }
        //4.从redis中删除订单日志
        redisTemplate.boundHashOps("payLog").delete(payLog.getUserId());
    }
}
