package com.zs.gms.common.service.websocket;

/**
 * 功能点
 */
public enum FunctionEnum {
    vehicle,//车辆信息
    globalPath,//全局路径
    checkServer,//检测系统服务
    taskAreaState,//任务点状态
    excavator,//挖掘机用户
    maintainTask,//维护任务
    unitStatus,//调度单元状态
    trail,//车辆轨迹
    collectMap,//地图数据采集
    approve,//审批结果
    console,//控制台
    linkError,//异常信息
    dispatchProcess,//调度任务过程
    login,//java客服端登录认证

    partPath,//局部路径
    fault,//告警信息
    videoPush,//视频推送
    obstacle;//障碍物信息


    public static FunctionEnum getFunction(String funcName){
        for (FunctionEnum value : values()) {
            if(funcName.equals(value.name())){
                return value;
            }
        }
        return null;
    }
}
