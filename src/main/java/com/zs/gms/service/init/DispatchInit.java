package com.zs.gms.service.init;

import com.alibaba.fastjson.JSONObject;
import com.zs.gms.common.annotation.RedisLock;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.RedisKey;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.message.MessageEntry;
import com.zs.gms.common.message.MessageFactory;
import com.zs.gms.common.message.MessageResult;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.entity.monitor.TaskRule;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.enums.monitor.UnitTypeEnum;
import com.zs.gms.service.monitor.DispatchTaskService;
import com.zs.gms.service.monitor.TaskRuleService;
import com.zs.gms.service.system.RoleService;
import com.zs.gms.service.system.UserService;
import com.zs.gms.service.vehiclemanager.BarneyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Slf4j
public class DispatchInit {

    @Autowired
    private DispatchTaskService dispatchTaskService;

    @Autowired
    private TaskRuleService taskRuleService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Value("${gms.system.executeInitFlag}")
    private boolean executeInitFlag;

    @Autowired
    private BarneyService barneyService;

    /**
     * 初始化所有调度员的空闲单元信息
     */
    private void initLeisureInit() {
        Role role = roleService.getRoleIdByRoleSign(Role.RoleSign.DESPATCHER_ROLE.getValue());
        Integer mapId = GmsUtil.getActiveMap();
        if (null != role && mapId!=null) {
            List<User> users = userService.getUsersByRoleId(role.getRoleId());
            if (!CollectionUtils.isEmpty(users)) {
                for (User user : users) {
                    List<DispatchTask> dispatchTasks = dispatchTaskService.getDispatchTaskList(user.getUserId(), UnitTypeEnum.INTERACTIVE_DISPATCHTASK.getValue(),mapId);
                    if (CollectionUtils.isEmpty(dispatchTasks)) {
                        DispatchTask dispatchTask = new DispatchTask();
                        dispatchTask.setDispatchTaskType(UnitTypeEnum.INTERACTIVE_DISPATCHTASK);
                        dispatchTask.setUserId(user.getUserId());
                        dispatchTask.setName("leisure");
                        dispatchTask.setMapId(mapId);
                        dispatchTask.setStatus(DispatchTask.Status.RUNING);
                        dispatchTaskService.addDispatchTask(dispatchTask);
                    }
                }
            }
        }
    }

    /**
     * 初始化所有任务单元信息，将所有启动的任务单元置为停止状态
     */
    private void initUnits() throws GmsException {
        Integer mapId = GmsUtil.getActiveMap();
        if(mapId==null){
            return;
        }
        List<DispatchTask> dispatchTasks = dispatchTaskService.getDispatchTaskList(mapId);
        if (!CollectionUtils.isEmpty(dispatchTasks)) {
            for (DispatchTask dispatchTask : dispatchTasks) {
                dispatchTaskService.updateUnitStatusByUnitId(GmsUtil.typeTransform(dispatchTask.getUnitId(), Integer.class), DispatchTask.Status.STOP);
                UnitTypeEnum type = UnitTypeEnum.getEnumTypeByValue(dispatchTask.getDispatchTaskType().getValue());
                switch (type) {
                    case SPECIAL_DISPATCHTASK:
                        initSpecialDispatchTask(GmsUtil.typeTransform(dispatchTask.getUnitId(), Integer.class), dispatchTask.getTaskType().getValue(), dispatchTask.getTaskAreaId());
                        break;
                    case LOAD_DISPATCHTASK:
                        initLoadDispatchTask(GmsUtil.typeTransform(dispatchTask.getUnitId(), Integer.class), dispatchTask.getLoadAreaId(), dispatchTask.getUnLoadAreaId());
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 初始化装卸调度单元及车辆
     */
    private void initLoadDispatchTask(Integer unitId, Integer loadAreaId, Integer unLoadAreaId) throws GmsException {
        try {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("unitId", unitId);
            paramMap.put("loaderAreaId", loadAreaId);
            paramMap.put("unLoaderAreaId", unLoadAreaId);
            MessageEntry entry = MessageFactory.createMessageEntry("dispatch");
            entry.setHttp(false);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                    List<TaskRule> rules = taskRuleService.getTaskRulesByUnitId(unitId);
                    Map<String, Object> paramMap1;
                    for (TaskRule taskRule : rules) {
                        paramMap1 = new HashMap<>();
                        paramMap1.put("unitId", unitId);
                        paramMap1.put("vehicleId", taskRule.getVehicleId());
                        paramMap1.put("cycleTimes", taskRule.getCycleTimes());
                        String endTime = taskRule.getEndTime();
                        if(StringUtils.isNotEmpty(endTime)){
                            endTime=endTime.replaceAll("[-|\\s+]","");
                        }
                        paramMap1.put("endTime", endTime);
                        MessageEntry messageEntry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
                        messageEntry.setHttp(false);
                        MessageFactory.getDispatchMessage().sendMessageNoResWithID(messageEntry.getMessageId(),"AddLoadAIVeh", JSONObject.toJSONString(paramMap1));
                    }
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
     * 初始化特殊调度单元及车辆
     */
    private void initSpecialDispatchTask(Integer unitId, String taskType, Integer taskAreaId) throws GmsException {
        try {
            MessageEntry entry = MessageFactory.createMessageEntry("dispatch");
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("unitId", GmsUtil.typeTransform(unitId, Integer.class));
            paramMap.put("taskType", taskType);
            paramMap.put("taskAreaId", taskAreaId);
            entry.setHttp(false);
            entry.setAfterHandle(() -> {
                if (entry.getHandleResult().equals(MessageResult.SUCCESS)) {
                    List<TaskRule> rules = taskRuleService.getTaskRulesByUnitId(unitId);
                    Map<String, Object> paramMap1;
                    for (TaskRule taskRule : rules) {
                        paramMap1 = new HashMap<>();
                        paramMap1.put("unitId", unitId);
                        paramMap1.put("vehicleId", taskRule.getVehicleId());
                        MessageEntry messageEntry = MessageFactory.createMessageEntry(GmsConstant.DISPATCH);
                        messageEntry.setHttp(false);
                        MessageFactory.getDispatchMessage().sendMessageNoResWithID(messageEntry.getMessageId(),"AddSpecialAIVeh", JSONObject.toJSONString(paramMap1));
                    }
                }
            });
            MessageFactory.getDispatchMessage().sendMessageNoResWithID(entry.getMessageId(), "CreateSpecialAIUnit", JSONObject.toJSONString(paramMap));
        } catch (Exception e) {
            String message = "初始化特殊调度单元失败";
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


    @RedisLock(key = RedisKey.DISPATCH_INIT)
    public void init(String key) {
        log.info("初始化调度依赖");
        try {
            initVehicles();
            initLeisureInit();
            initUnits();
            initAreas();
        } catch (Exception e) {
            log.error("调度初始化失败", e);
        }
    }
}