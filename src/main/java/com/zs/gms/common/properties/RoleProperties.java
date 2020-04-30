package com.zs.gms.common.properties;

import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource(value = {"classpath:properties/role.properties"},encoding = "utf8")
@ConfigurationProperties(prefix = "system")
@Data
public class RoleProperties {

    private Map<String,String[]> roles=new HashMap<>();
}
