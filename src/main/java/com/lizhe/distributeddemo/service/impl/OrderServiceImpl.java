package com.lizhe.distributeddemo.service.impl;

import com.lizhe.distributeddemo.mapper.OrderMapper;
import com.lizhe.distributeddemo.service.OrderService;
import com.lizhe.distributeddemo.vo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Transactional
    @Override
    public void saveOrder(Order order) {

    }

    @Override
    public Order getOne(String orderId) {
        return null;
    }
}
