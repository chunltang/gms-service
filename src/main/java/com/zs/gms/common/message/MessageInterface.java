package com.zs.gms.common.message;

public interface MessageInterface {

    /**
     * 内部生成消息id，无回调
     * */
     void sendMessageNoResNoID(String routKry, String sendMessage);

    /**
     * 从外部转入消息id,有回调，非http请求
     * */
     void sendMessageNoResWithID(String messageId,String routKry, String sendMessage);


    /**
     * 从外部转入消息id，http请求，需要等待响应，有回调
     * @param messageId     消息id
     * @param routKry       路由建
     * @param  sendMessage   发送消息
     * @param resultMessage  返回结果
     * */
    void sendMessageWithID(String messageId, String routKry, String sendMessage, String resultMessage);

    /**
     * 内部生成消息id，http请求，需要等待响应，无回调
     * */
    void sendMessageNoID(String routKry, String sendMessage, String resultMessage);
}
