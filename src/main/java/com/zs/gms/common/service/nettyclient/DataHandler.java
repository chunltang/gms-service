package com.zs.gms.common.service.nettyclient;

import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.monitor.TaskAreaState;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.messagebox.ApproveService;
import com.zs.gms.service.system.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class DataHandler {

    public static void handle(WsData wsData) {
        switch (wsData.getType()) {
            case Protocol.WITH_LOGIN://登录信息
                whenLogin(wsData);
                break;
            case Protocol.WITH_FUNC_ADD:
                funcAdd(wsData);
                break;
        }
    }

    /**
     * 登录发送消息
     */
    private static void whenLogin(WsData wsData) {
        log.debug("websocket登录发送消息");
        String key = wsData.getUserId().toString();
        ApproveService approveService = SpringContextUtil.getBean(ApproveService.class);
        List<Approve> approves = approveService.getApproveRemaining(key);//没有审批,针对其他人的提交
        List<Approve> approveNoMark = approveService.getApproveNoMark(key);//没有确认,针对自己的提交
        approves.addAll(approveNoMark);
        if (CollectionUtils.isNotEmpty(approves)) {
            WsUtil.sendMessage(key, GmsUtil.toJsonIEnumDesc(approves), FunctionEnum.approve);
        }
    }

    /**
     * 功能订阅
     */
    private static void funcAdd(WsData wsData) {
        FunctionEnum funcName = wsData.getFuncName();
        switch (funcName) {
            case taskAreaState:
                sendByRole(wsData.getUserId());
                break;
            case unitStatus:
                sendUnitStatus();
                break;
        }
    }

    private static void sendUnitStatus() {
        Collection<String> keys = RedisService.getLikeKey(StaticConfig.MONITOR_DB, RedisKeyPool.DISPATCH_UNIT);
        if(GmsUtil.CollectionNotNull(keys)){
            for (String key : keys) {
                HashMap result = GmsService.getMessage(key, HashMap.class);
                if (GmsUtil.mapNotNull(result)) {
                    Unit.Status status = Unit.Status.getEnumTypeByValue(result.getOrDefault("state", "").toString());
                    if (null != status) {
                        result.put("state", status);
                        WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(result), FunctionEnum.unitStatus);
                    }
                }
            }
        }
    }

    /**
     * 根据角色发送消息
     */
    private static void sendByRole(Integer userId) {
        UserService userService = SpringContextUtil.getBean(UserService.class);
        User user = userService.findUserById(userId);
        if (null != user) {
            Role.RoleSign sign = Role.getEnum(user.getRoleSign());
            if (null != sign) {
                switch (sign) {
                    case EXCAVATORPERSON_ROLE://挖掘机,任务点状态推送
                        Collection<String> likeKeys = RedisService.getLikeKey(StaticConfig.MONITOR_DB, RedisKeyPool.DISPATCH_AREA_PREFIX);
                        if (GmsUtil.CollectionNotNull(likeKeys)) {
                            for (String key : likeKeys) {
                                TaskAreaState taskAreaState = GmsService.getMessage(key, TaskAreaState.class);
                                WsUtil.sendMessage(userId.toString(), GmsUtil.toJsonIEnumDesc(taskAreaState), FunctionEnum.taskAreaState);
                            }
                        }
                        break;
                }
            }
        }
    }
}
