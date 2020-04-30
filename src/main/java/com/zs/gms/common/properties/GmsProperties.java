package com.zs.gms.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@PropertySource(value = {"classpath:properties/gms.properties"},encoding = "utf8")
@ConfigurationProperties(prefix = "gms")
@Data
public class GmsProperties {

    private boolean openAopLog=true;
    private SwaggerProperties swagger = new SwaggerProperties();
    private ShiroProperties shiro=new ShiroProperties();
    private Map<String,String> queues=new HashMap<>();
    private Map<String,String> exchanges=new HashMap<>();
    private Map<String,String> monitorQueues=new HashMap<>();
    private Map<String,List<String>>  binds=new HashMap();
}
