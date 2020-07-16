package com.lizhe.distributeddemo.service;

import com.lizhe.distributeddemo.vo.Order;

public interface OrderService {
    void saveOrder(Order order);
    Order getOne(String orderId);
}
