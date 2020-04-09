package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.websocket.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.monitor.DispatchTask;
import com.zs.gms.entity.monitor.TaskAreaState;
import com.zs.gms.service.init.DispatchInit;
import com.zs.gms.service.monitor.DispatchTaskService;

import java.util.HashMap;
import java.util.Map;

public class DispatchHandle implements RedisListener {

    private static DispatchHandle instance=new DispatchHandle();

    public static DispatchHandle getInstance(){
        return instance;
    }

    @Override
    public void listener(String key) {
        String prefix = key;
        if(key.matches(".*\\_[0-9]+$")){
            prefix=GmsUtil.subIndexStr(key, "_");
        }

        switch (prefix){
            case RedisKeyPool.DISPATCH_AREA_PREFIX:
                TaskAreaState taskState = GmsUtil.getMessage(key, TaskAreaState.class);
                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(taskState), FunctionEnum.taskAreaState);
                break;
            case RedisKeyPool.DISPATCH_SERVER_INIT:
                DispatchInit dispatchInit = SpringContextUtil.getBean(DispatchInit.class);
                dispatchInit.init();
                break;
            case RedisKeyPool.DISPATCH_UNIT:
                DispatchTaskService dispatchTaskService = SpringContextUtil.getBean(DispatchTaskService.class);
                String unitId = GmsUtil.subLastStr(key, "_");
                Object value = RedisService.get(StaticConfig.MONITOR_DB, key);
                if(null!=value) {
                    DispatchTask.Status status = DispatchTask.Status.getEnumTypeByValue((String) value);
                    dispatchTaskService.updateUnitStatusByUnitId(Integer.valueOf(unitId),status);
                    if(WsUtil.isNeed(FunctionEnum.unitStatus)) {
                        Map<String,Object> result=new HashMap<>();
                        result.put("unitId",unitId);
                        result.put("status",status);
                        WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(result),FunctionEnum.unitStatus);
                    }
                }
                break;
        }
    }
}
