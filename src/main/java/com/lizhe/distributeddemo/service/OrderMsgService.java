package com.lizhe.distributeddemo.service;

import com.lizhe.distributeddemo.vo.OrderMsg;

public interface OrderMsgService {
    void saveMsg(OrderMsg orderMsg);
    OrderMsg getOne(String uniqueId);
}
