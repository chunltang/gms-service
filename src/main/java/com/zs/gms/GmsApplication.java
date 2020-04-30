package com.zs.gms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.zs.gms.mapper.**")
@EnableScheduling
@EnableTransactionManagement
public class GmsApplication {

    public static void main(String[] args) {
        /*System.setProperty("MYSQL_SERVER_HOST","192.168.2.114");
        System.setProperty("REDIS_SERVER_HOST","192.168.2.114");
        System.setProperty("RABBITMQ_SERVER_HOST","192.168.2.114");*/
        System.setProperty("MYSQL_SERVER_HOST","172.18.0.4");
        System.setProperty("REDIS_SERVER_HOST","172.18.0.3");
        System.setProperty("RABBITMQ_SERVER_HOST","172.18.0.2");
        System.setProperty("SERVER_PWD","123456");
        SpringApplication.run(GmsApplication.class, args);
    }
}
