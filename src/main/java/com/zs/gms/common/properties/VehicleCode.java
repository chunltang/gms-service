package com.zs.gms.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@PropertySource(factory = YmlPropertySourceFactory.class,value = "classpath:properties/vehicleCode.yml",encoding = "utf-8")
@ConfigurationProperties(prefix = "warn")
@Data
public class VehicleCode {

    private Map<Integer,Code> code =new HashMap<>();

    /**
     * 使用哪种故障描述<编号,<告警码,描述>>
     * */
    private Map<Integer,Map<Integer,String>> warnDesc=new HashMap<>();

    @Data
    public static class Code{//要使用静态内部类，不能非静态内部类，不然不能正常注入

        /**
         * 部件名称
         * */
        private  String partName;

        /**
         * 故障等级
         * */
        private Integer level;

        /**
         * 告警描述编号,使用哪种配置的告警描述
         * */
        private Integer warnDescCode;
    }
}

