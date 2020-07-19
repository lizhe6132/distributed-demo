package com.lizhe.distributeddemo.controller;

import com.lizhe.distributeddemo.service.DisTransService;
import com.lizhe.distributeddemo.transaction.mq.MQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 分布式事务测试
 */
@RestController
public class DisTransController {
    @Autowired
    private DisTransService disTransService;

    @RequestMapping("/order_add")
    public void createOrders() {

        disTransService.createOne();


    }
}
