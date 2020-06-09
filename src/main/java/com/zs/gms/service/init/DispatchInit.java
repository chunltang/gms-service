package com.zs.gms.service.init;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.RedisLock;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.utils.DateUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.monitor.UnitVehicle;
import com.zs.gms.service.mapmanager.MapDataUtil;
import com.zs.gms.service.monitor.UnitService;
import com.zs.gms.service.monitor.UnitVehicleService;
import com.zs.gms.service.vehiclemanager.BarneyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Slf4j
public class DispatchInit {

    @Value("${gms.system.executeInitFlag}")
    private boolean executeInitFlag;

    @Autowired
    private BarneyService barneyService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private UnitVehicleService unitVehicleService;

    /**
     * 初始化所有任务单元信息，将所有启动的任务单元置为停止状态
     */
    private void initUnits() throws GmsException {
        Integer mapId = MapDataUtil.getActiveMap();
        if(mapId==null){
            return;
        }
        unitService.clearUnitSAndVehicles(mapId);
        List<Unit> units = unitService.getUnitListByMapId(mapId);
        if (!CollectionUtils.isEmpty(units)) {
            for (Unit unit : units) {
                initLoadDispatchTask(unit);
            }
        }
    }

    /**
     * 初始化装卸调度单元及车辆
     */
    private void initLoadDispatchTask(Unit unit) throws GmsException {
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("unitId", unit.getUnitId());
            paramMap.put("loaderAreaId", unit.getLoadAreaId());
            paramMap.put("unLoaderAreaId", unit.getUnLoadAreaId());
            paramMap.put("cycleTimes", unit.getCycleTimes());
            paramMap.put("endTime", GmsUtil.objNotNull(unit.getEndTime())? DateUtil.getDateFormat(unit.getEndTime(),DateUtil.FULL_TIME_SPLIT_PATTERN):"");
            MessageEntry entry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
            entry.setHttp(false);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                    List<UnitVehicle> unitVehicles = unitVehicleService.getUnitVehicleListUnitId(unit.getUnitId());
                    Set<Integer> vehicleIds = unitVehicles.stream().map(UnitVehicle::getVehicleId).collect(Collectors.toSet());
                    Map<String,Object> paramMap1 = new HashMap<>();
                    paramMap1.put("unitId", unit.getUnitId());
                    paramMap1.put("vehicleIds", vehicleIds);
                    MessageEntry messageEntry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
                    messageEntry.setHttp(false);
                    MessageFactory.getDispatchMessage().sendMessageNoResWithID(messageEntry.getMessageId(),"AddLoadAIVeh", JSONObject.toJSONString(paramMap1));
                }
            });
            MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(), "CreateLoaderAIUnit", JSONObject.toJSONString(paramMap));
        } catch (Exception e) {
            String message = "初始化装卸调度单元失败";
            log.error(message, e);
            throw new GmsException(message);
        }
    }



    /**
     * 获取所有任务区和区域id的对应关系
     */
    private void initAreas() {
        //获取当前活动地图

        //获取活动地图的所有区域信息
    }

    /**
     * 初始化所有车辆
     */
    private void initVehicles() {
        List<Integer> allVehicleNos = barneyService.getAllVehicleNos();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("vehicles", allVehicleNos);
        MessageEntry messageEntry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
        messageEntry.setHttp(false);
        MessageFactory.getDispatchMessage().sendMessageNoResWithID(messageEntry.getMessageId(),"InitVeh", JSONObject.toJSONString(paramMap));
    }


    @RedisLock(key = RedisKeyPool.DISPATCH_INIT_LOCK)
    public void init() {
        log.info("初始化调度依赖");
        try {
            initVehicles();
            initUnits();
            initAreas();
        } catch (Exception e) {
            log.error("调度初始化失败", e);
        }
    }
}