package com.zs.gms.service.monitor.schdeule;

import com.company.Interval;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import com.zs.gms.entity.monitor.GpsLiveInfo;
import com.zs.gms.entity.monitor.LiveInfo;
import com.zs.gms.entity.monitor.Monitor;
import com.zs.gms.entity.monitor.VehicleLiveInfo;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import com.zs.gms.service.mapmanager.MapDataUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆实时位置
 */
@Slf4j
public class LivePosition {

    /**
     * 车辆上一个位置
     */
    private static Map<Integer, Position> lastPositionMap = new ConcurrentHashMap<>();

    @Interval(interval = 2000,
            before = "com.zs.gms.common.service.GmsService.preIntervalHandler",
            isReturn = true)
    public static void handleDelegate(LiveInfo liveInfo,Integer deviceId) {
        switch (liveInfo.getType()){
            case GPS:
                handle((GpsLiveInfo)liveInfo);
                break;
            case VEHICLE:
                handle((VehicleLiveInfo)liveInfo);
                break;
        }
    }

    private static void handle(GpsLiveInfo gpsLiveInfo){

    }

    private static void handle(VehicleLiveInfo vehicleLiveInfo) {
        Integer mapId = MapDataUtil.getActiveMap();
        if (null != mapId && vehicleLiveInfo != null) {
            Integer vehicleId = vehicleLiveInfo.getVehicleId();
            Position position = GmsUtil.mapPutAndGet(lastPositionMap, vehicleId, new Position());
            AnglePoint point = new AnglePoint();
            Monitor monitor = vehicleLiveInfo.getMonitor();
            point.setX(monitor.getXworld());
            point.setY(monitor.getYworld());
            point.setYawAngle(monitor.getYawAngle());
            point.setZ(0);
            position.setPoint(point);
            position.setDispState(vehicleLiveInfo.getDispState());
            position.setVehicleId(vehicleId);
            position.setLastVehicleLiveInfo(vehicleLiveInfo);
            position.setMapId(mapId);
            position.setLastDate(System.currentTimeMillis());
            lastPositionMap.put(vehicleId, position);
            if (WsUtil.isNeed(FunctionEnum.excavator)) {
                MapDataUtil.getCoordinateArea(position);
            }
        }
    }

    public static SemiStatic getAreaInfo(Position position) {
        Integer lastArea = position.getLastArea();
        if (null != lastArea) {
            return MapDataUtil.getAreaInfo(position.getMapId(), lastArea);
        }
        return null;
    }

    public static boolean isAreaType(Position position, AreaTypeEnum typeEnum) {
        SemiStatic areaInfo = getAreaInfo(position);
        return null != areaInfo && areaInfo.getAreaType().equals(typeEnum);
    }

    public static Position getLastPosition(Integer vehicleId) {
        return lastPositionMap.getOrDefault(vehicleId, null);
    }

    public static LiveInfo getLastLiveInfo(Integer vehicleId){
        Position position = getLastPosition(vehicleId);
        return null==position?null:position.getLastVehicleLiveInfo();
    }

    @Data
    public static class Position {

        /**
         * 地图id
         */
        private Integer mapId;

        /**
         * 车辆编号
         */
        private Integer vehicleId;

        /**
         * 当前位置区域
         */
        private Integer lastArea;

        @JsonIgnore
        private LiveInfo lastVehicleLiveInfo;

        /**
         * 车辆位置
         */
        private AnglePoint point;

        /**
         * 调度状态
         * */
        private DispatchStateEnum dispState;

        /**
         * 获取时间
         */
        private long lastDate;

        /**
         * 判断是否出了装载区,true为在装载区
         */
        private boolean isLoadArea = false;
    }
}
