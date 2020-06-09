package com.zs.gms.common.entity;

public class GmsConstant {

    /**
     * 排序类型
     * */
    public static final String SORT_DESC="desc";
    public static final String SORT_ASC="asc";


    /**
     * 地图全局配置
     * */
    public static final String MAP_GLOBAL_CONFIG="map_global_config";

    /**
     * 地图版本
     * */
    public static final String MAP_VERSION="map_version";

    /**
     * 版本
     * */
    public static final String SERVICE_VERSION="service_version";

    /**
     * 服务描述
     * */
    public static final String DISPATCH="dispatch";
    public static final String MAP="map";
    public static final String VAP="vap";
    public static final String BUSI="busi";
    public static final String APPROVE="approve";

    public static final String MQ_HEART_BEAT="mq_heart_beat";

    public static final int DATA_MAP_INITIAL_CAPACITY=2;

    /**
     * shiro用于rememberMe的加密
     * */
    public static final String GMS_SHIRO_KEY="gms_shiro_key";

    public static final  String SHIRO_SESSION_PREFIX="shiro-session_";

    /**
     * MQ等待响应时间
     */
    public final static Long WAIT_TIME = 30000L;

    /**
     * 临时文件存放目录
     * */
    public final static String TEMP_DIR ="/temp";

    /**
     * 图标库
     * */
    public final static String ICON_LIB="/icon";

    public final static int CLIENT_SIGN = 1001;

    public final static int CLIENT_USER_SIGN = 2001;
}
