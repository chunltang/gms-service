package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.TaskAreaState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collection;

@WsFunction(key = FunctionEnum.taskAreaState)
@Component
@Slf4j
public class StateHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        for (Session sn : sessions) {
            send(sn,message);
        }
    }

    public void afterAdd(Session session){
        Collection<String> likeKeys = RedisService.getLikeKey(StaticConfig.MONITOR_DB, RedisKeyPool.DISPATCH_AREA_PREFIX);
        for (String key : likeKeys) {
            TaskAreaState taskAreaState = GmsUtil.getMessage(key, TaskAreaState.class);
            send(session, GmsUtil.toJsonIEnumDesc(taskAreaState));
        }
    }

    private void send(Session session, String message){
        String result = getResult(message,FunctionEnum.taskAreaState.name());
        synchronized (session) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(result);
                } else {
                    sessions.remove(session);
                }
            } catch (IOException e) {
                log.error("ws-taskAreaState发送数据失败", e);
            }
        }
    }
}

