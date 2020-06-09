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
        super.sendMessage(session,message);
    }
}
