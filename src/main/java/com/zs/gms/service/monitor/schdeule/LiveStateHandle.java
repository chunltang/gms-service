package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.entity.monitor.LiveInfo;
import com.zs.gms.entity.monitor.TaskAreaState;

public class LiveStateHandle implements RedisListener {

    private static LiveStateHandle instance=new LiveStateHandle();

    public static LiveStateHandle getInstance(){
        return instance;
    }

    @Override
    public void listener(String key) {
        TaskAreaState taskState = GmsUtil.getMessage(key, TaskAreaState.class);
        WsUtil.sendMessage(GmsUtil.toJsonIEnum(taskState), FunctionEnum.taskAreaState);
    }
}
