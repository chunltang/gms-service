package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.GmsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@WsFunction(key = FunctionEnum.approve)
@Component
@Slf4j
public class ApproveHandler extends SetHandler {

    @Override
    public void sendMessage(Session session, String message) {
        super.sendMessage(session,message);
    }
}
