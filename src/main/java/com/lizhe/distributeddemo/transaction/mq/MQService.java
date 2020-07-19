package com.lizhe.distributeddemo.transaction.mq;

import com.lizhe.distributeddemo.service.OrderMsgService;
import com.lizhe.distributeddemo.vo.OrderMsg;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MQService {
    private static final String TRANS_EXCAHGER = "ordersExcahnger";
    @Autowired
    private OrderMsgService orderMsgService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @PostConstruct
    public void confirm() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    // 重发
                    OrderMsg orderMsg = orderMsgService.getOne(correlationData.getId());
                    sendMsg(orderMsg.getMsgContent(), correlationData.getId());
                }
                // 修改消息状态为已发送
                String sql = "UPDATE order_msg SET msg_status = '1' WHERE unique_id=?";
                jdbcTemplate.update(sql, correlationData.getId());

            }
        });

    }
    public void sendMsg(String s, String orderId) {
        rabbitTemplate.convertAndSend(TRANS_EXCAHGER, "", s, new CorrelationData(orderId));
    }
}
