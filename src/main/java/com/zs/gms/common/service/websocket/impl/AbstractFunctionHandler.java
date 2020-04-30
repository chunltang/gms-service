package com.zs.gms.common.service.websocket.impl;

import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.FunctionHandler;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Slf4j
public abstract class AbstractFunctionHandler implements FunctionHandler {

    public final static String FUNCTION_FIELD = "funcName";

    public final static String VEHICLE_FIELD = "vehicleId";

    public final static String HEARTBEAT_FIELD = "heartbeat";

    public final static String SESSION_FIELD = "session";

    public final static String TYPE_FIELD = "type";

    @Override
    public void addFunction(Map<String, Object> params) {
        //do nothing
    }

    @Override
    public void removeFunction(Session session) {
        //do nothing
    }

    @Override
    public boolean hasSession(Session session) {
        return false;
    }

    @Override
    public Set<Session> getSession(Integer vehicleId) {
        return null;
    }

    @Override
    public void removeSession(Session session) {
        //do nothing
    }

    @Override
    public void sendMessage(Session session, String message) {
        //do nothing
    }

    @Override
    public boolean isNeed(Object... params) {
        return false;
    }

    /**
     * 组合返回数据
     */
    public String getResult(String message, String name) {
        log.debug("websocket推送:{}", name);
        Map<String, String> map = new HashMap<>();
        map.put("funcName", name);
        map.put("data", message);
        return GmsUtil.toJson(map);
    }

    public void afterAdd(Session session) {
        //do nothing
    }

    /**
     * 获取登录session的用户信息
     */
    public User getLoginUser(Session session) {
        Object obj = session.getUserProperties().get("key");
        if (null != obj) {
            UserService userService = SpringContextUtil.getBean(UserService.class);
            return userService.findUserById(GmsUtil.typeTransform(obj, Integer.class));
        }
        return null;
    }

    /**
     * 判断连接是否是指定角色列表
     * */
    public boolean isRole(Session session, Role.RoleSign... signs) {
        User user = getLoginUser(session);
        String roleSign = user.getRoleSign();
        if (GmsUtil.allObjNotNull(signs)) {
            Set<String> names = new HashSet<>();
            for (Role.RoleSign sign : signs) {
                names.add(sign.getValue());
            }
            return names.contains(roleSign);
        }
        return false;
    }

    /**
     * 发送异常信息
     */
    public void sendError(Session session, String message) {
        String result = getResult(message, FunctionEnum.linkError.name());
        synchronized (session) {
            if (session.isOpen()) {
                try {
                    log.error("发送ws-linkError信息");
                    session.getBasicRemote().sendText(result);
                } catch (IOException e) {
                    log.error("发送ws-linkError异常信息失败", e);
                }
            }
        }
    }
}
