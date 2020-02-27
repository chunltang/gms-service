package com.zs.gms.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.enums.monitor.Desc;
import lombok.Data;
import sun.security.krb5.internal.crypto.Des;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class IEnumDescSerializer extends JsonSerializer<Enum> {

    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String descStr = "";
        Serializable valueStr =null;
        if (null != value) {
            if(value instanceof Desc){
                Desc desc = (Desc) value;
                if(desc.isPrint()){
                    descStr = desc.getDesc();
                }
            }
            if(value instanceof IEnum){
                IEnum iEnum = (IEnum) value;
                valueStr = iEnum.getValue();
            }
        }
        if(GmsUtil.StringNotNull(descStr)&&null!=valueStr){
            JSONObject json = new JSONObject();
            json.put("desc", descStr);
            json.put("value", valueStr);
            gen.writeObject(json);
        }else if(null!=valueStr){
            gen.writeObject(valueStr);
        }else if(GmsUtil.StringNotNull(descStr)){
            gen.writeObject(descStr);
        }
    }
}
