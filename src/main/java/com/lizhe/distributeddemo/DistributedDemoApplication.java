package com.lizhe.distributeddemo;

import com.lizhe.distributeddemo.config.DruidConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
/*import org.springframework.cloud.netflix.eureka.EnableEurekaClient;*/
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@MapperScan("com.lizhe.distributeddemo.mapper")
//开启配置文件读取功能
@EnableConfigurationProperties(DruidConfig.class)
/*@EnableEurekaClient*/
public class DistributedDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedDemoApplication.class, args);
    }

}
