package com.zs.gms.common.entity;

/**
 * 可改动静态池
 * */
public class StaticPool {

    public static long monitor_last_time=System.currentTimeMillis();//重置监听时间

    public static boolean dispatch_service_status=false;

}
