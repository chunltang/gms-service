package com.zs.gms.common.utils;

import com.zs.gms.enums.monitor.TaskTypeEnum;

/**
 * 测试方法
 */
public class Main {

    /*public static void main(String[] args) {
        System.out.println("原始数据：");
        for (Season season : Season.values()) {
            System.out.println(season);
        }
        System.out.println("-----------------------------");
        DynamicEnumUtil.addEnum(Season.class, "WINTER", new Class[] {
                java.lang.String.class, SeasonPattern.class }, new Object[] {
                "winter", SeasonPattern.SNOW });
        System.out.println("添加后的数据：");
        for (Season season : Season.values()) {
            System.out.println(season);
        }
        System.out.println("-----------------------------");
        Season season = Season.valueOf("WINTER");
        System.out.println("新添加的枚举类型可以正常使用：");
        System.out.println(season.name());
        System.out.println(season.getKey());
        System.out.println(season.getSeasonPattern());
    }*/

    public static void main(String[] args) {
        DynamicEnumUtil.addEnum(TaskTypeEnum.class,
                "PAK",
                new Class[]{java.lang.String.class,java.lang.String.class}
                ,new Object[]{"7","新增"});

        for (TaskTypeEnum value : TaskTypeEnum.values()) {
            System.out.println(value);
        }
        TaskTypeEnum pak = TaskTypeEnum.valueOf("PAK");
        System.out.println(pak.name());
        System.out.println(pak.getValue());
        System.out.println(pak.getDesc());
    }
}

