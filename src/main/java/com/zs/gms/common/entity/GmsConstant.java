package com.zs.gms.common.entity;

import com.zs.gms.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.zip.ZipEntry;

@Component
public class GmsConstant {

    public static final String SORT_DESC="desc";
    public static final String SORT_ASC="asc";

    /**
     * 服务描述
     * */
    public static final String DISPATCH="dispatch";
    public static final String MAP="map";
    public static final String VAP="vap";
    public static final String APPROVE="approve";

    /**
     * {@link BaseController}
     * getDataTable 中 HashMap 默认的初始化容量
     * */
    public static final int DATA_MAP_INITIAL_CAPACITY=2;

    /**
     * shiro用于rememberMe的加密
     * */
    public static final String GMS_SHIRO_KEY="gms_shiro_key";

    /**
     * MQ等待响应时间
     */
    public final static Long waitTime = 120000L;

    /**
     * 车辆标识
     * */
    public final static String VEHICLE_SIGN="vehicleId";

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
