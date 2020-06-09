package com.zs.gms.common.entity;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;

/**
 * 是否枚举
 * */
public enum WhetherEnum implements IEnum, Desc {

    NO("0", "否"),
    YES("1", "是");
    private String value;
    private String desc;

    WhetherEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
