package com.zs.gms.common.service.websocket;

/**
 * 功能点
 */
public enum FunctionEnum {
    vehicle,//车辆信息
    globalPath,//全局路径
    taskAreaState,//任务点状态
    excavator,//挖掘机用户
    trail,//车辆轨迹
    partPath,//局部路径
    collectMap,//地图数据采集
    fault,//告警信息
    approve,//审批结果
    obstacle,//障碍物信息
    notification,//通知信息
    videoPush,//视频推送
    console,//控制台
    dispatchProcess,//调度任务过程
    notarize;//确认信息

    public static FunctionEnum getFunction(String funcName){
        for (FunctionEnum value : values()) {
            if(funcName.equals(value.name())){
                return value;
            }
        }
        return null;
    }
}
