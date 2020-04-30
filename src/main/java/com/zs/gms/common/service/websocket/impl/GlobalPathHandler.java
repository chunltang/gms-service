package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@WsFunction(key = FunctionEnum.globalPath)
@Component
@Slf4j
public class GlobalPathHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        if (null != session) {
            send(session, message);
        } else {
            for (Session s : sessions) {
                send(s, getResult(message, FunctionEnum.globalPath.name()));
            }
        }
    }

    private void send(Session session, String message) {
        if(!isRole(session,Role.RoleSign.CHIEFDESPATCHER_ROLE,Role.RoleSign.DESPATCHER_ROLE)){
            log.error("非调度长或调度员会话，关闭连接！");
            removeFunction(session);
            return;
        }
        synchronized (session) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                } else {
                    sessions.remove(session);
                }
            } catch (IOException e) {
                log.error("ws-globalPath发送数据失败", e);
            }
        }
    }
}

