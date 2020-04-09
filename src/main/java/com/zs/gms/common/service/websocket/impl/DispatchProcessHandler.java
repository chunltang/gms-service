package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.DelayedService;
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

    private Map<Session, Set<DelayedService.Task>> sessionMap = new ConcurrentHashMap();

    private final static long INTERVAL = 5 * 1000;

    @Override
    public void addFunction(Map<String, Object> params) {
        if (GmsUtil.mapContains(params, SESSION_FIELD, VEHICLE_FIELD)) {
            Session session = (Session) params.get(SESSION_FIELD);
            Integer integer = Integer.valueOf(params.get(VEHICLE_FIELD).toString());
            log.debug("添加调度进程数据推送");
            DelayedService.Task task = DelayedService.buildTask()
                    .withAtOnce(true)
                    .withDelay(INTERVAL)
                    .withNum(-1)
                    .withDesc("调度进程dispatchProcess数据推送")
                    .withTask(() -> {
                        VehicleDispatchStatusHandle handle = StatusMonitor.getHandle(VehicleDispatchStatusHandle.class);
                        if (handle != null) {
                            String message = handle.push(integer);
                            sendMessage(session, getResult(message, FunctionEnum.dispatchProcess.name()));
                        }
                    });
            DelayedService.addTask(task);
            sessionMap.computeIfAbsent(session, s -> new CopyOnWriteArraySet<>()).add(task);
        }
    }

    @Override
    public void removeFunction(Session session) {
        if (sessionMap.containsKey(session)) {
            Set<DelayedService.Task> tasks = sessionMap.get(session);
            sessionMap.remove(session);
            for (DelayedService.Task task : tasks) {
                task.setNeedExec(false);
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
