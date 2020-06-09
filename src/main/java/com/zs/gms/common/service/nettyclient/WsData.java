package com.zs.gms.common.service.nettyclient;

import com.zs.gms.common.service.websocket.FunctionEnum;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WsData {

    private Integer userId;

    private FunctionEnum funcName;

    private int type;

    private Map<String,Object> params;

    private String data;

    public static Build builder() {
        return new Build();
    }

    public static class Build {

        private WsData wsData;

        private Build() {
            this.wsData = new WsData();
        }

        public WsData build(){
            return wsData;
        }

        public Build withUserId(Integer userId){
            wsData.setUserId(userId);
            return this;
        }

        public Build withParam(String key,Object value){
            if(null==wsData.getParams()){
                wsData.setParams(new HashMap<>());
            }
            wsData.getParams().put(key,value);
            return this;
        }

        public Build withType(int type){
            wsData.setType(type);
            return this;
        }

        public Build withFuncName(FunctionEnum funcName){
            wsData.setFuncName(funcName);
            return this;
        }

        public Build withData(String data){
            wsData.setData(data);
            return this;
        }
    }
}
