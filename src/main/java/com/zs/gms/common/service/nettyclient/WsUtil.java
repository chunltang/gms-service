package com.zs.gms.common.service.nettyclient;

import com.zs.gms.common.service.websocket.FunctionEnum;

public class WsUtil {

    public static void sendMessage(String userId, String message, FunctionEnum nEnum) {
        NettyClient.sendMessage(WsData.builder()
                .withData(message)
                .withUserId(Integer.valueOf(userId))
                .withFuncName(nEnum)
                .withType(Protocol.WITH_USERID)
                .build()
        );
    }

    public static void sendMessage(String key, String message, FunctionEnum nEnum, Integer vehicleId) {
        NettyClient.sendMessage(WsData.builder()
                .withData(message)
                .withParam("vehicleId",vehicleId)
                .withFuncName(nEnum)
                .withType(Protocol.WITH_VEHICLEID)
                .build()
        );
    }

    public static void sendMessage(String message, FunctionEnum nEnum, Integer vehicleId) {
        NettyClient.sendMessage(WsData.builder()
                .withData(message)
                .withParam("vehicleId",vehicleId)
                .withFuncName(nEnum)
                .withType(Protocol.WITH_VEHICLEID)
                .build()
        );
    }

    public static void sendMessage(String message, FunctionEnum nEnum) {
        NettyClient.sendMessage(WsData.builder()
                .withData(message)
                .withFuncName(nEnum)
                .withType(Protocol.WITH_NO)
                .build()
        );
    }

    public static boolean isNeed(FunctionEnum nEnum, Object... params) {
        return true;
    }
}
