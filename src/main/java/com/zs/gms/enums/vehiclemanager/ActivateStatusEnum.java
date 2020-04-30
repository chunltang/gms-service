package com.zs.gms.enums.vehiclemanager;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;


public enum ActivateStatusEnum implements IEnum, Desc {

    UNACTIVATED("0", "未激活"),
    ACTIVATED("1", "已激活");
    private String value;
    private String desc;

    ActivateStatusEnum(String value, String desc) {
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
