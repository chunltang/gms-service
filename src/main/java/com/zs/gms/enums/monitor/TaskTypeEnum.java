package com.zs.gms.enums.monitor;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;

/**
 * 任务类型
 * */
public enum TaskTypeEnum implements IEnum, Desc {

    LEISURE("0","空闲"),
    PARK("1","泊车"),
    LOAD("2","装载"),
    UNLOAD("3","卸载"),
    WASTE("4","排土"),
    PETROL("5","加油"),
    REFILL("6","加水");

    private String value;

    private String desc;

    TaskTypeEnum(String value,String desc){
        this.value=value;
        this.desc=desc;
    }

    public String getValue(){
        return this.value;
    }

    @JsonValue
    public String getDesc(){
        return this.desc;
    }

    public static TaskTypeEnum getEnum(String value){
        for (TaskTypeEnum typeEnum : values()) {
            if(typeEnum.getValue().equals(value)){
                return typeEnum;
            }
        }
        return null;
    }
}
