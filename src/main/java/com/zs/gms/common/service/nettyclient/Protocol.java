package com.zs.gms.common.service.nettyclient;

public class Protocol {

    /**
     * 协议类型类型
     */
    public static final int WITH_NO = 0X0001;//不指定用户和参数
    public static final int WITH_VEHICLEID = 0X0002;//指定车辆id为参数
    public static final int WITH_USERID = 0X0003;//指定用户
    public static final int WITH_LOGIN = 0X0004;//websocket用户连接通知
    public static final int WITH_FUNC_ADD = 0X0005;//功能订阅
}
