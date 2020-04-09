package com.zs.gms.enums.statistics;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public enum DateFormatEnum {

    MONTH("1","%Y-%m"),
    DATE("2","%Y-%m-%d"),
    HOUR("3","%Y-%m-%d %H");

    private String format;


    private String value;

    DateFormatEnum(String value,String format){
        this.format=format;
        this.value=value;
    }

    public String getValue(){
        return this.value;
    }

    public String getFormat(){
        return this.format;
    }

    @JsonCreator
    public static DateFormatEnum getInstance( String value){
        for (DateFormatEnum anEnum : DateFormatEnum.values()) {
            if(anEnum.getValue().equals(value)){
                return anEnum;
            }
        }
        log.debug("未找到对应枚举类型");
        return null;
    }
}
