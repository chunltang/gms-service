package com.zs.gms.service.init;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GmsClosedRunner implements ApplicationListener<ContextClosedEvent> {//启动接口ApplicationListener<ContextRefreshedEvent>

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("service closed");
    }
}
