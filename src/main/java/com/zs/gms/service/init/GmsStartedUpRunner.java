package com.zs.gms.service.init;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.properties.GmsProperties;
import com.zs.gms.common.service.RabbitMqService;
import com.zs.gms.common.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
@Slf4j
public class GmsStartedUpRunner implements ApplicationRunner {

    @Autowired
    private RabbitMqService service;

    //@Autowired
    private RedisTemplate<String, Object> template;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private GmsProperties gmsProperties;

    @Value("${server.port:8080}")
    private String port;

    @Value("${gms.server.name}")
    private String serverName;

    @Autowired
    private SystemInit systemInit;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //测试redis是否连接成功
        try {
            String str="return redis.call('config','set','notify-keyspace-events','$E')";
            String execute = RedisService.getTemplate(GmsConstant.KEEP_DB).execute(new DefaultRedisScript<String>(str), null);
            System.out.println(execute);
        } catch (Exception e) {
            log.error("redis start fail!",e);
            context.close();
        }
        if (context.isActive()) {
            systemInit.init();
            InetAddress address = InetAddress.getLocalHost();
            String url = String.format("http://%s:%s", address.getHostAddress(), port);
            String loginUrl = gmsProperties.getShiro().getLoginUrl();
            if (StringUtils.isNotBlank(loginUrl))
                url += loginUrl;
            log.info("系统启动完毕,地址:{}", url);
        }
    }
}