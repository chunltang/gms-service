package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;

@WsFunction(key = FunctionEnum.maintainTask)
@Component
@Slf4j
public class MaintainTaskHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        synchronized (session) {
            try {
                if (session.isOpen()) {
                    session.getBasicRemote().sendText(message);
                } else {
                    sessions.remove(session);
                }
            } catch (IOException e) {
                log.error("ws-maintainTask发送数据失败", e);
            }
        }
    }
}
