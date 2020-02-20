package com.zs.gms.common.entity;

public class RedisKey {

    /**==================dispatch====================*/
    public final static String VAP_BASE_PREFIX = "vap_base_";//车辆基础信息，包括障碍物信息、异常信息

    public final static String VAP_LIST_PREFIX = "vap_list_";//地图点集

    public final static String VAP_TRAIL_PREFIX = "vap_trail_";//车辆轨迹

    public final static String VAP_PATH_PREFIX = "vap_path_";//车辆全局路径

    public final static String TASK_AREA_PREFIX = "task_area_";//任务区状态

    public final static String VAP_PREFIX = "vap";//车辆推送前缀

    public final static String TASK_PREFIX = "task";//车辆状态前缀

    public final static String DISPATCH_INIT = "dispatchInit";//调度初始化锁键

    public final static String DISPATCH_SERVER_INIT = "dispatch_server_init";//监听调度初始化键

    public final static String GPS_PREFIX = "gps_";//监听调度初始化键

    /**==================map====================*/
    public final static String ACTIVITY_MAP = "activity_map_id";//活动地图

    public final static String SEMI_STATIC_DATA = "semi_static_data_";//半静态层数据

    public final static String VEHICLE_POSITION_PREFIX = "vehicle_position_";//车辆实时所在区域

}
