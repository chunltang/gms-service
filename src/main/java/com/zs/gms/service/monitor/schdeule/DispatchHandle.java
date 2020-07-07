package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.interfaces.RedisListener;
import com.zs.gms.common.service.GmsService;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.websocket.FunctionEnum;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import com.zs.gms.entity.monitor.TaskAreaState;
import com.zs.gms.entity.monitor.Unit;
import com.zs.gms.service.init.DispatchInit;
import com.zs.gms.service.monitor.UnitService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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
                TaskAreaState taskState = GmsService.getMessage(key, TaskAreaState.class);
                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(taskState), FunctionEnum.taskAreaState);
                log.debug("任务点状态变更: {}",taskState.getTaskSpots()[0].getState().getDesc());
                break;
            case RedisKeyPool.DISPATCH_SERVER_INIT:
                DispatchInit dispatchInit = SpringContextUtil.getBean(DispatchInit.class);
                dispatchInit.init();
                break;
            case RedisKeyPool.DISPATCH_UNIT:
                UnitService unitService = SpringContextUtil.getBean(UnitService.class);
                String unitId = GmsUtil.subLastStr(key, "_");
                Object value = RedisService.get(StaticConfig.MONITOR_DB, key);
                if(null!=value) {
                    HashMap result = GmsUtil.toObj(value, HashMap.class);
                    if(GmsUtil.mapNotNull(result)){
                        Unit.Status status = Unit.Status.getEnumTypeByValue(result.getOrDefault("state","").toString());
                        if(null!=status){
                            log.debug("调度单元状态改变:{},{}",unitId,result);
                            //unitService.updateStatus(Integer.valueOf(unitId),status);
                            if(WsUtil.isNeed(FunctionEnum.unitStatus)) {
                                result.put("state",status);
                                WsUtil.sendMessage(GmsUtil.toJsonIEnumDesc(result),FunctionEnum.unitStatus);
                            }
                        }else{
                            log.error("没有对应的调度枚举状态类型!");
                        }
                    }else{
                        log.error("调度状态解析异常!");
                    }
                }
                break;
        }
    }
}
