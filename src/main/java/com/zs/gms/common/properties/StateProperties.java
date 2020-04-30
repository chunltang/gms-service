package com.zs.gms.common.properties;

import com.zs.gms.common.utils.DynamicEnumUtil;
import com.zs.gms.entity.monitor.TaskAreaState;
import com.zs.gms.enums.monitor.*;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Data
@PropertySource(value = {"classpath:properties/state.properties"},encoding = "utf8")
@ConfigurationProperties(prefix = "state")
@SpringBootConfiguration
public class StateProperties {
    private  Map<String,String> vapMode=new HashMap<>();
    private  Map<String,String> vakMode=new HashMap<>();
    private  Map<String,String> currentTaskCode=new HashMap<>();
    private  Map<String,String> vapState=new HashMap<>();
    private  Map<String,String> modeState=new HashMap<>();
    private  Map<String,String> dispState=new HashMap<>();
    private  Map<String,String> taskState=new HashMap<>();
    private  Map<String,String> taskAreaState=new HashMap<>();

    @PostConstruct
    public void init() {
        initEnum(vapMode,VapModeEnum.class);
        initEnum(vakMode,VakModeEnum.class);
        initEnum(currentTaskCode,TaskCodeEnum.class);
        initEnum(vapState,VapStateEnum.class);
        initEnum(modeState,ModeStateEnum.class);
        initEnum(dispState,DispatchStateEnum.class);
        initEnum(taskState,TaskStateEnum.class);
        initEnum(taskAreaState,TaskAreaStateEnum.class);
    }

    public void initEnum(Map<String,String> targetMap,Class<? extends Enum> eClass){
        if(eClass.getEnumConstants()==null || eClass.getEnumConstants().length==0){
            char obj='a';
            for (Map.Entry<String, String> entry : targetMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                DynamicEnumUtil.addEnum(eClass,String.valueOf(obj),
                        new Class[]{String.class,String.class},
                        new Object[]{key,value});
                obj=(char)(obj+1);
            }
        }
    }
}
