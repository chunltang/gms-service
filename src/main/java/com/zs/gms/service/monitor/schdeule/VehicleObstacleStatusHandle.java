package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.utils.Assert;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.Obstacle;
import com.zs.gms.entity.monitor.VehicleStatus;
import com.zs.gms.service.mapmanager.MapDataUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 车辆障碍物处理
 * */
@Slf4j
public class VehicleObstacleStatusHandle  extends AbstractVehicleStatusHandle {

    public VehicleObstacleStatusHandle() {
        super();
    }

    @Override
    public void handleStatus(VehicleStatus vehicleStatus) {
        Assert.notNull(vehicleStatus,"障碍物信息不能为空");
        Object obj = vehicleStatus.getObj();
        if(null!=obj){
            Obstacle[] obstacles = (Obstacle[]) obj;
            if(GmsUtil.arrayNotNull(obstacles)){ log.debug("[{}]车辆发现障碍物:{}",vehicleStatus.getVehicleId(), GmsUtil.toJson(obj));
                sendObstacleToMap((Obstacle[])obj,vehicleStatus.getVehicleId());
            }
        }
    }

    /**
     * 发送障碍物到地图服务
     * */
    private void sendObstacleToMap(Obstacle[] obstacles, Integer vehicleId){
        MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.MAP);
        Integer mapId = MapDataUtil.getActiveMap();
        Map<String,Object> params=new HashMap<>();
        params.put("mapId",mapId);
        params.put("obstacles",obstacles);
        entry.setHttp(false);
        entry.setAfterHandle(()->{
            if(entry.getHandleResult().equals(MessageResult.SUCCESS)){
                String data = entry.getReturnData();
                WsUtil.sendMessage(data, FunctionEnum.obstacle);
                log.debug("创建障碍物返回信息:{}",data);
                aroundCreatePath(vehicleId);
            }else{
                log.error("发送障碍物到地图服务返回失败");
            }
        });
        MessageFactory.getMapMessage().sendMessageNoResWithID(entry.getMessageId(),"createObstacles",GmsUtil.toJson(params));
    }

    /**
     * 绕障路径生成
     * */
    //tcl 05-09 新增接口
    private void aroundCreatePath(Integer vehicleId){
        Map<String,Object> params=new HashMap<>();
        params.put("vehicleId",vehicleId);
        MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
        entry.setHttp(false);
        entry.setAfterHandle(()->{
            MessageResult result = entry.getHandleResult();
            log.debug("绕障路径生成结果:{}",result.name());
        });
        MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(),"aroundCreatePath",GmsUtil.toJson(params));
    }
}
