package com.zs.gms.enums.vehiclemanager;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public enum DateEnum implements IEnum, Desc {

    MONTH("0", "月",30 * 24 * 60 * 60),
    WEEK("1", "周",7 * 24 * 60 * 60),
    DATE("2", "天",24 * 60 * 60),
    HOUR("3", "时",60 * 60);

    private String value;

    private long num;//延时时间，秒

    private String desc;

    DateEnum(String value, String desc, long num) {
        this.value = value;
        this.num = num;
        this.desc = desc;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    public long getNum() {
        return this.num;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }

    @JsonCreator
    public DateEnum getInstance(String value) {
        for (DateEnum anEnum : DateEnum.values()) {
            if (anEnum.getValue().equals(value)) {
                return anEnum;
            }
        }
        log.debug("未找到对应枚举类型");
        return null;
    }

    /**
     * 根据策略参数时间戳
     */
    public static Long getTime(DateEnum dateEnum) {
        Long time = 0l;
        if (null != dateEnum) {
            switch (dateEnum) {
                case WEEK:
                case HOUR:
                case DATE:
                    time = dateEnum.getNum() * 1000;
                    break;
                case MONTH:
                    Calendar calendar = Calendar.getInstance();
                    Date date = new Date();
                    calendar.setTime(date);
                    calendar.add(Calendar.MONDAY, 1);
                    time = calendar.getTimeInMillis() - date.getTime();
                    break;
            }
        }
        return time;
    }
}
