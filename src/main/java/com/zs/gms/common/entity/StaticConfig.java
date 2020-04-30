package com.zs.gms.common.entity;

import com.zs.gms.common.properties.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 静态属性配置
 * */
@Component
public class StaticConfig {

    public  static String SERVICE_NAME;//当前实列服务名称

    public static int MONITOR_DB;//redis监听库号

    public static int KEEP_DB;

    public static int CACHE_DB;

    public static String bMRequest;

    public static String bDRequest;

    public static String bVRequest;


    @Value("${gms.server.name}")
    public void setService(String service){
        SERVICE_NAME=service;
    }

    @Value("${gms.listener.redisMonitorDB}")
    public void setMonitorDB(int monitorDB){
        MONITOR_DB=monitorDB;
    }

    @Value("${gms.cache.redisDB}")
    public void setCacheDB(int cacheDB){
        CACHE_DB=cacheDB;
    }

    @Value("${gms.keep.redisDB}")
    public void setKeepDB(int keepDB){
        KEEP_DB=keepDB;
    }

    @Value("${gms.exchanges.bMRequest}")
    public void setBMRequest(String exchangeName){
        bMRequest=exchangeName;
    }

    @Value("${gms.exchanges.bDRequest}")
    public void setbDRequest(String exchangeName){
        bDRequest=exchangeName;
    }

    @Value("${gms.exchanges.bVRequest}")
    public void setbVRequest(String exchangeName){
        bVRequest=exchangeName;
    }
}
