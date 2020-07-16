package com.lizhe.distributeddemo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private static final Logger LOG = LoggerFactory.getLogger(RabbitConfig.class);
    @Value("${spring.rabbitmq.host}")
    private String addresses;

    @Value("${spring.rabbitmq.port}")
    private String port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;

    //TODO 连接工厂
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        //连接名称前缀
        connectionFactory.setConnectionNameStrategy(connectionFactory1 -> "rabbit-connection");
        //集群模式下设置address("host1:port,host2:port")
        connectionFactory.setAddresses(addresses+":"+port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        //如果要配置通道缓存的大小(默认值为25)，您也可以在此处调用setChannelCacheSize()方法
        //connectionFactory.setChannelCacheSize(25);
        // 可以切换连接缓存模式，默认是1个连接，缓存channel(适合非集群模式),可以修改为缓存连接和通道
        //connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        //消息发送方确认
        /** 如果要进行消息回调，则这里必须要设置为true */
        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        return connectionFactory;
    }
    //rabbitAdmin类封装对RabbitMQ的管理操作
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    //使用Template
    @Bean
    public RabbitTemplate newRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        //失败通知
        template.setMandatory(true);
        //发送方确认
        template.setConfirmCallback(confirmCallback());
        //失败回调
        template.setReturnCallback(returnCallback());
        return template;
    }
    //===============生产者发送确认==========
    @Bean
    public RabbitTemplate.ConfirmCallback confirmCallback(){
        return new RabbitTemplate.ConfirmCallback(){
            @Override
            public void confirm(CorrelationData correlationData,
                                boolean ack, String cause) {
                if (ack) {
                    LOG.info("发送者确认发送给mq成功" + correlationData.getReturnedMessage());
                } else {
                    //处理失败的消息
                    LOG.info("发送者发送给mq失败,考虑重发:"+cause);
                }
            }
        };
    }
    //===============失败通知==========
    @Bean
    public RabbitTemplate.ReturnCallback returnCallback() {
        return new RabbitTemplate.ReturnCallback() {

            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                LOG.error("发送失败:" + message);
            }
        };
    }
    // 定义订单topic交换器
    @Bean(name="orderExchanger")
    public Exchange orderExchanger() {
        return new TopicExchange("order.topic");
    }
    // 定义订单保存队列
    @Bean(name="order")
    public Queue orderSaveQueue() {
        return new Queue("saveOrder");
    }
    // 绑定队列与交换器
    @Bean
    public Binding bindingExchangeOrder(@Qualifier("order") Queue queueOrder, @Qualifier("orderExchanger")TopicExchange exchange){
        return BindingBuilder.bind(queueOrder).to(exchange).with("order.save");
    }
}
