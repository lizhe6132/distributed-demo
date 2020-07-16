package com.lizhe.distributeddemo.service.impl;

import com.lizhe.distributeddemo.mapper.OrderMsgMapper;
import com.lizhe.distributeddemo.service.OrderMsgService;
import com.lizhe.distributeddemo.vo.OrderMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OrderMsgServiceImpl implements OrderMsgService {
    @Autowired
    private OrderMsgMapper orderMsgMapper;
    @Transactional
    @Override
    public void saveMsg(OrderMsg orderMsg) {

    }

    @Override
    public OrderMsg getOne(String uniqueId) {
        return null;
    }
}
