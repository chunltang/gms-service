package com.zs.gms.common.entity;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

public enum UseStatus implements IEnum {

    ENABLE("1"),
    DISABLE("0");

    public String value;

    UseStatus(String value) {
        this.value = value;
    }

    @Override
    public Serializable getValue() {
        return value;
    }
}
