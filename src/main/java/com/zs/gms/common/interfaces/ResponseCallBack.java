package com.zs.gms.common.interfaces;

/**
 * mq发送给远程服务消息并得到成功处理的回应后的回调接口
 * */
public interface ResponseCallBack {

    public void afterHandler();
}
