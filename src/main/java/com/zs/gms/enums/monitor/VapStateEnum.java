package com.zs.gms.enums.monitor;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;

import java.io.Serializable;

public enum VapStateEnum implements IEnum, Desc {
    ;

    private String value;

    private String desc;

    private VapStateEnum(String value,String desc){
        this.value=value;
        this.desc=desc;
    }
    @Override
    public Serializable getValue() {
        return value;
    }

    @JsonValue
    public String getDesc(){
        return desc;
    }
}
