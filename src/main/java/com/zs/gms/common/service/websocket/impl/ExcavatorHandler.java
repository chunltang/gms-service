package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsFunction;
import com.zs.gms.common.utils.Assert;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.client.UserExcavatorLoadArea;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WsFunction(key = FunctionEnum.excavator)
@Component
@Slf4j
public class ExcavatorHandler extends SetHandler {

    @Autowired
    @Lazy
    private UserExcavatorLoadAreaService bindService;

    private Map<Integer,Session> areaSessionMap=new ConcurrentHashMap<>();

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
                log.error("ws-excavator发送数据失败", e);
            }
        }
    }

    @Override
    public Set<Session> getSession(Integer areaId) {
        Set<Session> sessions = new HashSet<>();
        if(areaSessionMap.containsKey(areaId)){
            sessions.add(areaSessionMap.get(areaId));
        }
        return sessions;
    }

    @Override
    public boolean isNeed(Object ...params) {
        if(null!=params && params.length>0){
            Integer areaId=GmsUtil.typeTransform(params[0],Integer.class);
            return !sessions.isEmpty() && areaSessionMap.keySet().contains(areaId);
        }
        return !sessions.isEmpty();
    }

    /**
     * 获取挖掘机所在区域
     * */
    public void afterAdd(Session session){
        try {
            User user = getLoginUser(session);
            Assert.notNull(user,"websocket获取登录用户异常");
            UserExcavatorLoadArea bind = bindService.getBindByUser(user.getUserId());
            if(null!=bind){
                areaSessionMap.put(bind.getLoadAreaId(),session);
            }else{
                String message="挖掘机绑定数据获取失败";
                log.error(message);
                sendError(session,message);
                sessions.remove(session);
            }
        }catch (Exception e){
            sendError(session,"获取挖掘机所在区域异常");
            sessions.remove(session);
        }
    }
}
