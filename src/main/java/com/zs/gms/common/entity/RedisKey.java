package com.zs.gms.common.entity;

public class RedisKey {

    public final static String REDIS_MONITOR = "redis_monitor";//redis监听

    /**==================dispatch====================*/
    public final static String VAP_BASE_PREFIX = "vap_base_";//车辆基础信息，包括障碍物信息、异常信息

    public final static String VAP_LIST_PREFIX = "vap_list_";//地图点集

    public final static String VAP_TRAIL_PREFIX = "vap_trail_";//车辆轨迹

    public final static String VAP_PATH_PREFIX = "vap_path_";//车辆全局路径

    public final static String DISPATCH_AREA_PREFIX = "dispatch_task_area_";//任务区状态

    public final static String VAP_PREFIX = "vap";//车辆推送前缀

    public final static String DISPATCH_PREFIX = "dispatch";//其他调度服务前缀

    public final static String DISPATCH_INIT = "dispatch_init_lock";//调度初始化锁键

    public final static String DISPATCH_UNIT = "dispatch_unit_";//调度单元状态

    public final static String DISPATCH_SERVER_INIT = "dispatch_server_init";//监听调度初始化键

    public final static String DISPATCH_SERVER_HEARTBEAT = "dispatch_server_heartbeat";//监听调度初始化键

    public final static String GPS_PREFIX = "gps_";//监听调度初始化键

    /**==================map====================*/
    public final static String ACTIVITY_MAP = "activity_map_id";//活动地图

    public final static String SEMI_STATIC_DATA = "semi_static_data_";//半静态层数据
}
