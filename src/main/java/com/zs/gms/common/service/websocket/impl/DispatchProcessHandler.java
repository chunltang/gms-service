package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.ScheduleService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.service.monitor.schdeule.StatusMonitor;
import com.zs.gms.service.monitor.schdeule.VehicleDispatchStatusHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@WsFunction(key = FunctionEnum.dispatchProcess)
@Component
@Slf4j
public class DispatchProcessHandler extends AbstractFunctionHandler {

    private Map<Session, Set<ScheduledFuture>> sessionMap = new ConcurrentHashMap();

    @Override
    public void addFunction(Map<String, Object> params) {
        if (GmsUtil.mapContains(params, SESSION_FIELD, VEHICLE_FIELD)) {
            Session session = (Session) params.get(SESSION_FIELD);
            Integer integer = Integer.valueOf(params.get(VEHICLE_FIELD).toString());
            log.debug("添加调度任务推送定时线程");
            ScheduledFuture future = ScheduleService.addTask(() -> {
                VehicleDispatchStatusHandle handle = StatusMonitor.getHandle(VehicleDispatchStatusHandle.class);
                if (handle != null) {
                    String message = handle.push(integer);
                    sendMessage(session, getResult(message, FunctionEnum.dispatchProcess.name()));
                }
            }, 5, TimeUnit.SECONDS);
            sessionMap.computeIfAbsent(session, s -> new CopyOnWriteArraySet<>()).add(future);
        }
    }

    @Override
    public void removeFunction(Session session) {
        if (sessionMap.containsKey(session)) {
            Set<ScheduledFuture> futures = sessionMap.get(session);
            sessionMap.remove(session);
            for (ScheduledFuture future : futures) {
                ScheduleService.cancel(future, false);
            }
        }
    }

    @Override
    public void sendMessage(Session session, String message) {
        synchronized (session) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                } else {
                    sessionMap.remove(session);
                }
            } catch (IOException e) {
                log.error("ws-dispatchProcess发送数据失败", e);
            }
        }
    }
}
