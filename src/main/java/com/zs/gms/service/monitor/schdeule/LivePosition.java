package com.zs.gms.service.monitor.schdeule;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.mapmanager.SemiStatic;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import com.zs.gms.entity.monitor.LiveInfo;
import com.zs.gms.entity.monitor.Monitor;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import com.zs.gms.service.mapmanager.MapDataUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆实时位置
 */
@Slf4j
public class LivePosition {

    /**
     * 过期时间
     */
    private static final long expire = 1000;

    private static LivePosition instance = new LivePosition();

    /**
     * 车辆上一个位置
     */
    private static Map<Integer, Position> lastPositionMap = new ConcurrentHashMap<>();

    public LivePosition getInstance() {
        return instance;
    }

    public static void setPosition(LiveInfo liveInfo) {
        instance.setArea(liveInfo);
    }

    public void setArea(LiveInfo liveInfo) {
        Integer mapId = GmsUtil.getActiveMap();
        if (null != mapId && liveInfo != null) {
            Integer vehicleId = liveInfo.getVehicleId();
            Position position = GmsUtil.mapPutAndGet(lastPositionMap, vehicleId, new Position());
            AnglePoint point = new AnglePoint();
            Monitor monitor = liveInfo.getMonitor();
            point.setX(monitor.getXworld());
            point.setY(monitor.getYworld());
            point.setYawAngle(monitor.getYawAngle());
            point.setZ(0);
            position.setPoint(point);
            position.setDispState(liveInfo.getDispState());
            position.setVehicleId(vehicleId);
            position.setLastLiveInfo(liveInfo);
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
        if (null != areaInfo && areaInfo.getAreaType().equals(typeEnum)) {
            return true;
        }
        return false;
    }

    public Position getLastPosition(Integer vehicleId) {
        return lastPositionMap.getOrDefault(vehicleId, null);
    }

    /**
     * 获取最新的位置信息
     */
    public List<Position> getLivePosition() {
        ArrayList<Position> positions = new ArrayList<>();
        ArrayList<Position> list = new ArrayList<>(lastPositionMap.values());
        for (Position position : list) {
            long lastDate = position.getLastDate();
            long newTime = System.currentTimeMillis();
            Integer lastArea = position.getLastArea();
            if (newTime - lastDate < expire && null != lastArea) {
                positions.add(position);
            }
            if (newTime - lastDate > 60 * expire) {
                Integer vehicleId = position.getVehicleId();
                lastPositionMap.remove(vehicleId);
            }
        }
        return positions;
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
        private LiveInfo lastLiveInfo;

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
