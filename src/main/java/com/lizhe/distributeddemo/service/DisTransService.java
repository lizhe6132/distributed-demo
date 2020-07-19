package com.lizhe.distributeddemo.service;

import com.alibaba.fastjson.JSONObject;
import com.lizhe.distributeddemo.transaction.mq.MQService;
import com.lizhe.distributeddemo.vo.Order;
import com.lizhe.distributeddemo.vo.OrderMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

@Service
public class DisTransService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderMsgService orderMsgService;
    @Autowired
    private MQService mqService;
    @Transactional
    public void createOne() {
        Order order = new Order();
        order.setCreateTime(new Date(System.currentTimeMillis()));
        String orderId = UUID.randomUUID().toString().replaceAll("-", "");
        order.setOrderId(orderId);
        String orderInfo = orderId + "-" + Math.random();
        order.setOrderInfo(orderInfo);
        OrderMsg msg = new OrderMsg();
        msg.setCreateTime(new Date(System.currentTimeMillis()));
        msg.setUniqueId(orderId);
        msg.setMsgContent(JSONObject.toJSONString(order));
        //1 创建订单和订单事务消息
        orderMsgService.saveMsg(msg);
        orderService.saveOrder(order);
        //2 发送事务消息至mq并更新消息为已发送
        mqService.sendMsg(JSONObject.toJSONString(order), orderId);
    }
}
