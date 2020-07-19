package com.lizhe.distributeddemo.transaction.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 模拟分布式订单消息消费者，更改订单状态，正常一般在其它服务中，这里懒得建项目了
 */
@Component
public class OrderMsgConsumer {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(OrderMsgConsumer.class);
    @RabbitListener(queues = "ordersQueen")
    public void messageConsumer (String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            LOG.info("收到ordersQueen消息:" + message);
            JSONObject jsonObject = JSONObject.parseObject(message);
            String orderId = jsonObject.getString("orderId");
            //更新订单状态，这里要做幂等性处理
            try {
                String sql = "UPDATE orders SET order_status = '1' WHERE order_id = ?";
                jdbcTemplate.update(sql, orderId);
            }catch (DataAccessException e) {
                //消息处理失败，但不需要重发
                channel.basicNack(tag, false, true);
            }
            // 正确处理，不需要重发
            channel.basicAck(tag, false);
        } catch (Exception e) {
            try {
                // 不能处理，重新投递
                channel.basicNack(tag, false, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }



    }
}
