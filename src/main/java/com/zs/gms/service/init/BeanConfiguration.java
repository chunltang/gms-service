package com.zs.gms.service.init;

import com.zs.gms.common.service.DelayedService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.LOWEST_PRECEDENCE)//指定@Configuration配置的执行顺讯
public class BeanConfiguration {//先定义的bean先执行

    @Bean
    public DelayedService getDelayedService() {
        return new DelayedService();
    }

    @Bean
    public HeartBeatCheck getHeartBeatCheck() {
        return new HeartBeatCheck();
    }

    @Bean
    public SyncRedisData getSyncRedisData() {
        return new SyncRedisData();
    }
}
