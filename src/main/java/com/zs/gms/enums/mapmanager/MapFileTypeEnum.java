package com.zs.gms.enums.mapmanager;

import com.baomidou.mybatisplus.core.enums.IEnum;

public enum MapFileTypeEnum implements IEnum {//路径，边界，不可通行区域

    path("0"),
    border("1"),
    impassableArea("2");

    private String value;

    MapFileTypeEnum(String value){
        this.value=value;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
