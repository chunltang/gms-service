package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.annotation.Parser;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.interfaces.Desc;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.mineralmanager.AreaMineral;
import com.zs.gms.entity.monitor.*;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import com.zs.gms.service.mineralmanager.AreaMineralService;
import com.zs.gms.service.monitor.DispatchTaskService;
import com.zs.gms.service.monitor.TaskRuleService;
import com.zs.gms.common.entity.LimitQueue;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.service.monitor.DispatchStatusService;
import com.zs.gms.service.monitor.impl.DispatchStatusServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 车辆调度状态处理
 */
@Slf4j
public class VehicleDispatchStatusHandle extends AbstractVehicleStatusHandle {

    private final static Integer LIMIT_QUEUE_SIZE = 10;
    private Map<Integer, LimitQueue<DispatchStatus>> historyStatusMap;
    private DispatchStatusService dispatchStatusService;

    public VehicleDispatchStatusHandle() {
        super();
        historyStatusMap = new ConcurrentHashMap<>();
        dispatchStatusService = SpringContextUtil.getBean(DispatchStatusServiceImpl.class);


    }

    @Override
    public void handleStatus(VehicleStatus vehicleStatus) {
        super.handleStatus(vehicleStatus);
    }


    @Override
    public void changed(VehicleStatus vehicleStatus) {
        log.debug("{}车辆调度状态改变:{}", vehicleStatus.getVehicleId(), ((Desc) (vehicleStatus.getStatus())).getDesc());
        super.changed(vehicleStatus);
    }

    @Override
    public void save(VehicleStatus vehicleStatus) {
        DispatchStatus dispatchStatus = getBean(vehicleStatus);
        if (addToQueue(dispatchStatus, vehicleStatus.getVehicleId())) {//第一次数据不保存
            dispatchStatusService.addDispatchStatus(dispatchStatus);
        }
    }

    private DispatchStatus getBean(VehicleStatus vehicleStatus) {
        Integer vehicleId = vehicleStatus.getVehicleId();
        DispatchStatus dispatchStatus = dispatchStatusService.getBaseInfo(vehicleId);
        if (Parser.notNull(dispatchStatus)) {
            dispatchStatus.setCreateTime(vehicleStatus.getCreateTime());
            dispatchStatus.setStatus((DispatchStateEnum) (vehicleStatus.getStatus()));
            return dispatchStatus;
        }
        return null;
    }

    @Override
    public void overtime(VehicleStatus vehicleStatus) {
        addToQueue(getBean(vehicleStatus), vehicleStatus.getVehicleId());
    }

    /**
     * 添加数据到队列
     */
    public boolean addToQueue(DispatchStatus dispatchStatus, Integer vehicleId) {
        boolean result = false;
        if (null == dispatchStatus) {
            checkProblem(vehicleId);
            return false;
        }
        log.debug("车{}调度状态:{}，添加调度状态到数据库", vehicleId, GmsUtil.toJsonIEnumDesc(dispatchStatus));
        if (historyStatusMap.containsKey(vehicleId)) {
            historyStatusMap.get(vehicleId).add(dispatchStatus);
            result = true;
        } else {
            LimitQueue<DispatchStatus> limitQueue = new LimitQueue<DispatchStatus>(LIMIT_QUEUE_SIZE);
            limitQueue.add(dispatchStatus);
            historyStatusMap.put(vehicleId, limitQueue);
        }
        return result;
    }

    /**
     * 数据检查
     * */
    private void checkProblem(Integer vehicleId){
        DispatchTaskService dispatchTaskService = SpringContextUtil.getBean(DispatchTaskService.class);
        DispatchTask dispatchTask = dispatchTaskService.getUnitByVehicleId(vehicleId);
        if(null==dispatchTask){
            log.debug("车[{}]没有分配调度单元",vehicleId);
            return;
        }

        Integer bUnitId = dispatchTask.getUnitId();
        LiveInfo liveInfo = LivePosition.getLastLiveInfo(vehicleId);
        if(null!=liveInfo){
            VehicleLiveInfo info = (VehicleLiveInfo) liveInfo;
            Integer dUnitId = info.getUnitId();
            if(!bUnitId.equals(dUnitId)){
                log.debug("车[{}]业务层调度单元[{}]与调度服务[{}]不匹配",vehicleId,bUnitId,dUnitId);
            }
        }

        TaskRuleService taskRuleService = SpringContextUtil.getBean(TaskRuleService.class);
        TaskRule taskRule = taskRuleService.getTaskRuleByVehicleId(null, vehicleId);
        if(null==taskRule){
            log.debug("车[{}]没有分配任务规则,调度单元[{}]",vehicleId,bUnitId);
            return;
        }

        UserExcavatorLoadAreaService bindService = SpringContextUtil.getBean(UserExcavatorLoadAreaService.class);
        Integer loadAreaId = dispatchTask.getLoadAreaId();
        UserExcavatorLoadArea userExcavatorLoadArea = bindService.getBindByLoad(loadAreaId);
        if(null==userExcavatorLoadArea){
            log.debug("车[{}]所在调度单元[{}]的装载区[{}]没有绑定挖掘机",vehicleId,bUnitId,loadAreaId);
            return;
        }

        AreaMineralService areaMineralService = SpringContextUtil.getBean(AreaMineralService.class);
        AreaMineral areaMineral = areaMineralService.getAreaMineral(loadAreaId);
        if(null==areaMineral){
            log.debug("车[{}]所在调度单元[{}]的装载区[{}]没有绑定矿物种类",vehicleId,bUnitId,loadAreaId);
        }
    }

    /**
     * 数据推送
     */
    @Override
    public String push(Integer vehicleId) {
        if (historyStatusMap.containsKey(vehicleId)) {
            LimitQueue<DispatchStatus> queues = historyStatusMap.get(vehicleId);
            return GmsUtil.toJson(queues);
        }
        return "";
    }
}
