package com.zs.gms.enums.system;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.io.Serializable;

public enum MenuTypeEnum implements IEnum {

    PAGE("0","页面"),FUNCATION("1","功能");

    private final String type;
    private final String desc;

    MenuTypeEnum(final String type,final String desc){
        this.type=type;
        this.desc=desc;
    }
    @Override
    public Serializable getValue() {
        return type;
    }

    public String getDesc(){
        return desc;
    }

    @JsonCreator
    public MenuTypeEnum getMenuType(String value){
        for (MenuTypeEnum menuTypeEnum : MenuTypeEnum.values()) {
            if(menuTypeEnum.getValue().equals(value)){
                return menuTypeEnum;
            }
        }
        return null;
    }
}
