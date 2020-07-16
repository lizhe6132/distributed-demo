package com.lizhe.distributeddemo.mapper;

import com.lizhe.distributeddemo.vo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    void saveOrder(Order order);
    Order getOne(@Param("orderId") String orderId);
}
