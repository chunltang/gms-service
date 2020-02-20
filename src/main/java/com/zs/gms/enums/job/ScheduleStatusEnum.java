package com.zs.gms.enums.job;

public enum ScheduleStatusEnum {
    /**
     * 正常
     */
    NORMAL("0"),
    /**
     * 暂停
     */
    PAUSE("1");

    private String value;

    ScheduleStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}