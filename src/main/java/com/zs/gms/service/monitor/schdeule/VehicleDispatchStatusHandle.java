package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.annotation.Parser;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.interfaces.Desc;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.entity.monitor.TaskRule;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import com.zs.gms.enums.monitor.UnitTypeEnum;
import com.zs.gms.service.client.UserExcavatorLoadAreaService;
import com.zs.gms.service.client.impl.UserExcavatorLoadAreaServiceImpl;
import com.zs.gms.service.monitor.DispatchTaskService;
import com.zs.gms.service.monitor.TaskRuleService;
import com.zs.gms.service.monitor.impl.DispatchTaskServiceImpl;
import com.zs.gms.service.monitor.impl.TaskRuleServiceImpl;
import com.zs.gms.service.vehiclemanager.BarneyService;
import com.zs.gms.service.vehiclemanager.impl.BarneyServiceImpl;
import com.zs.gms.common.entity.LimitQueue;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.monitor.DispatchStatus;
import com.zs.gms.entity.monitor.VehicleStatus;
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
        log.debug("{}车辆调度状态改变:{}", vehicleStatus.getVehicleId(), ((Desc)(vehicleStatus.getStatus())).getDesc());
        super.changed(vehicleStatus);
    }

    @Override
    public void save(VehicleStatus vehicleStatus) {
        DispatchStatus dispatchStatus = getBean(vehicleStatus);
        if(addToQueue(dispatchStatus)){//第一次数据不保存
            dispatchStatusService.addDispatchStatus(dispatchStatus);
        }
    }

    private DispatchStatus getBean(VehicleStatus vehicleStatus){
        Integer vehicleId = vehicleStatus.getVehicleId();
        DispatchStatus dispatchStatus = dispatchStatusService.getBaseInfo(vehicleId);
        if(Parser.notNull(dispatchStatus)){
            dispatchStatus.setCreateTime(vehicleStatus.getCreateTime());
            dispatchStatus.setStatus((DispatchStateEnum)(vehicleStatus.getStatus()));
            return dispatchStatus;
        }
        return null;
    }

    @Override
    public void overtime(VehicleStatus vehicleStatus) {
        addToQueue(getBean(vehicleStatus));
    }

    /**
     * 添加数据到队列
     */
    public boolean addToQueue(DispatchStatus dispatchStatus) {
        boolean result=false;
        if(null==dispatchStatus){
            log.error("数据异常，请检查获取BeanInfo代码");
            return result;
        }
        Integer vehicleId = dispatchStatus.getVehicleId();
        if (historyStatusMap.containsKey(vehicleId)) {
            historyStatusMap.get(vehicleId).add(dispatchStatus);
            result= true;
        } else {
            LimitQueue<DispatchStatus> limitQueue = new LimitQueue<DispatchStatus>(LIMIT_QUEUE_SIZE);
            limitQueue.add(dispatchStatus);
            historyStatusMap.put(vehicleId, limitQueue);
        }
        return result;
    }

    /**
     * 数据推送
     * */
    @Override
    public String push(Integer vehicleId) {
        if(historyStatusMap.containsKey(vehicleId)){
            LimitQueue<DispatchStatus> queues = historyStatusMap.get(vehicleId);
            return GmsUtil.toJson(queues);
        }
        return "";
    }
}
