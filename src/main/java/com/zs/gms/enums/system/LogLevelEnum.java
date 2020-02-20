package com.zs.gms.enums.system;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LogLevelEnum implements IEnum {

    TRACE("0","TRACE"),DEBUG("1","DEBUG"),INFO("2","INFO"),WARN("3","WARN"),ERROR("4","ERROR"),FATAL("5","FATAL");

    private final String value;
    private final String desc;

    LogLevelEnum(String value,String desc){
        this.value=value;
        this.desc=desc;
    }

    /**
     * 获取所有枚举值
     * */
    public static List<String> getAllEnumDesc(){
        return Arrays.asList(LogLevelEnum.values()).stream().map(LogLevelEnum::getDesc).collect(Collectors.toList());
    }


    public String getDesc(){
        return desc;
    }

    @Override
    public String getValue(){
        return value;
    }

}
