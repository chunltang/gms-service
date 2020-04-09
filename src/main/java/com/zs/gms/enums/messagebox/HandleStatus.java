package com.zs.gms.enums.messagebox;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;


public enum HandleStatus implements IEnum, Desc {

    UNTREATED("0","未处理"),
    HANDING("1","处理中"),
    PROCESSED("2","已处理");

    private String value;

    private String desc;

    HandleStatus(String value, String desc){
        this.value=value;
        this.desc=desc;
    }

    @Override
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}