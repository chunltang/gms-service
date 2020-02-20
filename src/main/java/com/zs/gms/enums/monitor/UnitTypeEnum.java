package com.zs.gms.enums.monitor;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 调度任务类型
 * */
public enum UnitTypeEnum implements IEnum {

    INTERACTIVE_DISPATCHTASK("0","空闲调度单元"),//空闲单元可以执行交互式任务
    SPECIAL_DISPATCHTASK("1","特殊调度单元"),
    LOAD_DISPATCHTASK("2","装卸调度单元");


    private String value;

    private String desc;

    UnitTypeEnum(String value, String desc){
        this.value=value;
        this.desc=desc;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public static UnitTypeEnum getEnumTypeByValue(String value){
        for (UnitTypeEnum unitType :UnitTypeEnum.values()) {
            if(unitType.getValue().equals(value)){
                return unitType;
            }
        }
        return null;
    }
}
