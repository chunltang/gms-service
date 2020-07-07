package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.DelayedService;
import com.zs.gms.common.service.ScheduleService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.FunctionHandler;
import com.zs.gms.common.service.websocket.SessionEntry;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.messagebox.Approve;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.messagebox.ApproveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import javax.websocket.Session;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HandleCenter extends AbstractFunctionHandler {

    private final static int HEARTBEAT_INTERVAL = 20 * 1000;

    private static volatile HandleCenter instance;

    private Map<String, SessionEntry> userSessionCache = new ConcurrentHashMap<>();

    private Map<Session, Long> heartbeatMap;

    private Map<FunctionEnum, FunctionHandler> handles;

    private final static Object lock = new Object();

    private HandleCenter() {
        handles = new HashMap<>();
        heartbeatMap = new ConcurrentHashMap<>();
        ApplicationContext context = SpringContextUtil.getContext();
        Map<String, Object> beans = context.getBeansWithAnnotation(WsFunction.class);
        beans.forEach((key, bean) -> {
            WsFunction wsFunction = bean.getClass().getAnnotation(WsFunction.class);
            if (wsFunction != null) {
                handles.put(wsFunction.key(), (FunctionHandler) bean);
            }
        });
        heartbeatCheck();
    }

    public static HandleCenter getInstance() {
        /*if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new HandleCenter();
                }
            }
        }
        return instance;*/
        return null;
    }

    public void handleRequest(Map<String, Object> params, Session session) {
        if (params.containsKey(HEARTBEAT_FIELD)) {
            updateHeartbeat(session);
            return;
        }
        if (GmsUtil.mapContains(params, TYPE_FIELD, FUNCTION_FIELD)) {
            String type = params.get(TYPE_FIELD).toString();
            switch (type) {
                case ADD_FUNCTION:
                    params.put(SESSION_FIELD, session);
                    addFunction(params);
                    break;
                case REMOVE_FUNCTION:
                    String name = params.get(FUNCTION_FIELD).toString();
                    FunctionEnum anEnum = FunctionEnum.getFunction(name);
                    if (anEnum != null) {
                        removeFunction(session, anEnum);
                    }
                    break;
                default:
                    log.error("没有对应功能处理请求参数");
                    break;
            }
        }
    }

    /**
     * 心跳跟新
     */
    private void updateHeartbeat(Session session) {
        heartbeatMap.put(session, System.currentTimeMillis());
    }

    /**
     * 心跳检测
     */
    private void heartbeatCheck() {
        log.debug("添加websocket心跳检测");
        DelayedService.Task task = DelayedService.buildTask()
                .withNum(-1)
                .withDelay(HEARTBEAT_INTERVAL)
                .withAtOnce(true)
                .withAddTime(GmsUtil.getCurTime())
                .withDesc("websocket心跳检测")
                .withPrintLog(true)
                .withTask(() -> {
                    for (SessionEntry entry : userSessionCache.values()) {
                        for (Session session : entry.getSessions()) {
                            if (System.currentTimeMillis() - heartbeatMap.getOrDefault(session, 0L) > HEARTBEAT_INTERVAL) {
                                synchronized (session) {
                                    try {
                                        log.debug("心跳检测失败，关闭连接!,key={}", entry.getKey());
                                        session.close();
                                    } catch (Exception e) {
                                        log.error("关闭过期websocket连接异常", e);
                                    }
                                }
                                removeSession(session);
                            }
                        }
                    }
                });
        DelayedService.addTask(task);
    }

    public void newSession(String key, Session session) {
        log.debug("websocket新建连接");
        if (userSessionCache.containsKey(key)) {
            userSessionCache.get(key).addSession(session);
        } else {
            SessionEntry entry = new SessionEntry();
            entry.setKey(key);
            entry.addSession(session);
            userSessionCache.put(key, entry);
        }
        updateHeartbeat(session);
        addDefaultFunction(session);
        whenLogin(key);
        addSessionByRole(session);
        log.debug("新增连接key={},当前连接:{}", key, Arrays.toString(userSessionCache.keySet().toArray()));
    }

    /**
     * 添加默认功能能
     */
    private void addDefaultFunction(Session session) {
        log.debug("websocket添加默认功能能");
        Map<String, Object> params = new HashMap<>();
        params.put(SESSION_FIELD, session);
        handles.get(FunctionEnum.approve).addFunction(params);
        handles.get(FunctionEnum.checkServer).addFunction(params);
    }

    /**
     * 登录发送消息
     */
    private void whenLogin(String key) {
        log.debug("websocket登录发送消息");
        ApproveService approveService = SpringContextUtil.getBean(ApproveService.class);
        List<Approve> approves = approveService.getApproveRemaining(key);//没有审批,针对其他人的提交
        List<Approve> approveNoMark = approveService.getApproveNoMark(key);//没有确认,针对自己的提交
        approves.addAll(approveNoMark);
        if (CollectionUtils.isNotEmpty(approves)) {
            sendMessage(key, GmsUtil.toJsonIEnumDesc(approves), FunctionEnum.approve);
        }
    }

    /**
     * 根据角色添加功能
     */
    private void addSessionByRole(Session session) {
        User user = getLoginUser(session);
        if (null != user) {
            String roleSign = user.getRoleSign();
            Role.RoleSign sign = Role.getEnum(roleSign);
            if (null != sign) {
                switch (sign) {
                    case DESPATCHER_ROLE:
                    case CHIEFDESPATCHER_ROLE:
                        Map<String, Object> params = new HashMap<>();
                        params.put(SESSION_FIELD, session);
                        handles.get(FunctionEnum.maintainTask).addFunction(params);
                        break;
                }
            }
        }
    }

    /**
     * 订阅
     */
    @Override
    public void addFunction(Map<String, Object> params) {
        if (MapUtils.isNotEmpty(params) && params.containsKey(FUNCTION_FIELD)) {
            FunctionEnum anEnum = FunctionEnum.getFunction(params.get(FUNCTION_FIELD).toString());
            if (anEnum != null) {
                log.debug("websocket订阅,{}", anEnum.name());
                handles.get(anEnum).addFunction(params);
            }
        }
    }

    /**
     * 删下层处理
     */
    private void removeFunction(Session session, FunctionEnum anEnum) {
        log.debug("websocket删除订阅");
        handles.get(anEnum).removeFunction(session);
    }

    /**
     * 删除session
     */
    public void removeSession(Session session) {
        log.debug("websocket删除session");
        String key = "";
        for (Map.Entry<String, SessionEntry> entry : userSessionCache.entrySet()) {
            SessionEntry value = entry.getValue();
            if (value.removeSession(session)) {
                heartbeatMap.remove(session);
                if (value.getSessions().size() == 0) {
                    key = entry.getKey();
                }
            }
        }
        for (FunctionHandler handler : handles.values()) {
            handler.removeFunction(session);
        }
        if (StringUtils.isNotEmpty(key)) {
            userSessionCache.remove(key);
            log.debug("删除连接key={},当前连接:{}", key, Arrays.toString(userSessionCache.keySet().toArray()));
        }
    }

    /**
     * 发送数据
     */
    public void sendMessage(String key, String message, FunctionEnum nEnum) {
        if (userSessionCache.containsKey(key)) {
            FunctionHandler handler = handles.get(nEnum);
            Set<Session> sessions = userSessionCache.get(key).getSessions();
            for (Session session : sessions) {
                if (handler.hasSession(session)) {
                    handler.sendMessage(session, getResult(message, nEnum.name()));
                }
            }
        }
    }

    /**
     * 指定用户和sessionKey推送
     */
    public void sendMessage(String key, String message, FunctionEnum nEnum, Integer sessionKey) {
        if (userSessionCache.containsKey(key)) {
            FunctionHandler handler = handles.get(nEnum);
            Set<Session> sessions = handler.getSession(sessionKey);
            if (CollectionUtils.isNotEmpty(sessions)) {
                for (Session session : sessions) {
                    handler.sendMessage(session, getResult(message, nEnum.name()));
                }
            }
        }
    }

    /**
     * 指定sessionKey发送
     */
    public void sendMessage(String message, FunctionEnum nEnum, Integer sessionKey) {
        FunctionHandler handler = handles.get(nEnum);
        Set<Session> sessions = handler.getSession(sessionKey);
        if (CollectionUtils.isNotEmpty(sessions)) {
            for (Session session : sessions) {
                handler.sendMessage(session, getResult(message, nEnum.name()));
            }
        }
    }

    /**
     * 指定类型发送
     */
    public void sendMessage(String message, FunctionEnum nEnum) {
        if(isNeed(nEnum)){
            FunctionHandler handler = handles.get(nEnum);
            handler.sendMessage(null, getResult(message,nEnum.name()));
        }
    }

    /**
     * 是否有session可以发送
     */
    public boolean isNeed(FunctionEnum nEnum, Object... params) {
        if (nEnum != null && handles.containsKey(nEnum)) {
            return handles.get(nEnum).isNeed(params);
        }
        return false;
    }
}
