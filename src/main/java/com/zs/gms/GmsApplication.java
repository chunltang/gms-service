package com.zs.gms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.zs.gms.mapper.**")
@EnableTransactionManagement
public class GmsApplication {

    public static void main(String[] args) {

        System.out.println("===============================================");
        System.out.println("                version:06016                 ");
        System.out.println("===============================================");

        System.setProperty("MYSQL_SERVER_HOST","192.168.43.232");
        System.setProperty("REDIS_SERVER_HOST","192.168.43.232");
        System.setProperty("RABBITMQ_SERVER_HOST","192.168.43.232");
        System.setProperty("NETTY_SERVER","192.168.43.232");
        System.setProperty("REMOTE_HOST","192.168.43.232");

        /*System.setProperty("MYSQL_SERVER_HOST","192.168.2.107");
        System.setProperty("REDIS_SERVER_HOST","192.168.2.107");
        System.setProperty("RABBITMQ_SERVER_HOST","192.168.2.107");
        System.setProperty("NETTY_SERVER","192.168.2.107");
        System.setProperty("REMOTE_HOST","192.168.2.107");*/

        /*System.setProperty("MYSQL_SERVER_HOST","192.168.2.126");
        System.setProperty("REDIS_SERVER_HOST","192.168.2.126");
        System.setProperty("RABBITMQ_SERVER_HOST","192.168.2.126");
        System.setProperty("NETTY_SERVER","192.168.2.124");
        System.setProperty("REMOTE_HOST","192.168.2.126");*/

        /*System.setProperty("MYSQL_SERVER_HOST","172.18.0.4");
        System.setProperty("REDIS_SERVER_HOST","172.18.0.3");
        System.setProperty("RABBITMQ_SERVER_HOST","172.18.0.2");
        System.setProperty("NETTY_SERVER","172.18.0.11");
        System.setProperty("REMOTE_HOST","172.18.0.1");*/

        System.setProperty("SERVER_PWD","123456");
        System.setProperty("REMOTE_USERNAME","root");
        System.setProperty("REMOTE_PASSWORD","gitlab");

        SpringApplication.run(GmsApplication.class, args);
    }
}

