package com.zs.gms.common.service.websocket;

import com.zs.gms.common.service.websocket.impl.HandleCenter;

public class WsUtil {

    public static void sendMessage(String key, String message, FunctionEnum nEnum){
        HandleCenter.getInstance().sendMessage(key,message,nEnum);
    }

    public static void sendMessage(String key, String message, FunctionEnum nEnum, Integer vehicleId){
        HandleCenter.getInstance().sendMessage(key,message,nEnum,vehicleId);
    }

    public static void sendMessage(String message, FunctionEnum nEnum){
        HandleCenter.getInstance().sendMessage(message,nEnum);
    }

    public static boolean isNeed(FunctionEnum nEnum,Object ...params){
        return HandleCenter.getInstance().isNeed(nEnum,params);
    }
}
