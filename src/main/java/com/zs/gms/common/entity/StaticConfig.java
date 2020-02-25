package com.zs.gms.common.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 静态属性配置
 * */
@Component
public class StaticConfig {

    /**
     * 当前实列服务名称
     * */
    public  static String SERVICE_NAME;

    /**
     * redis监听库号
     * */
    public static int MONITOR_DB;

    public static int KEEP_DB;

    @Value("${gms.server.name}")
    public void setService(String service){
        SERVICE_NAME=service;
    }

    @Value("${gms.listener.redisMonitorDB}")
    public void setMonitorDB(int monitorDB){
        MONITOR_DB=monitorDB;
    }

    @Value("${gms.keep.redisDB}")
    public void setKeepDB(int keepDB){
        KEEP_DB=keepDB;
    }
}
