package com.zs.gms.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.zs.gms.enums.monitor.Desc;
import lombok.Data;
import sun.security.krb5.internal.crypto.Des;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class IEnumSerializer extends JsonSerializer<Enum> {

    @Override
    public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String descStr = "";
        Serializable valueStr = "";
        if (null != value) {
            Desc desc = (Desc) value;
            IEnum iEnum = (IEnum) value;
            descStr = desc.getDesc();
            valueStr = iEnum.getValue();
        }
        JSONObject json = new JSONObject();
        json.put("desc", descStr);
        json.put("value", valueStr);
        gen.writeObject(json);
    }
}
