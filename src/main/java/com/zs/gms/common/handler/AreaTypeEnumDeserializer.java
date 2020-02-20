package com.zs.gms.common.handler;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import lombok.Data;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Data
public class AreaTypeEnumDeserializer extends JsonDeserializer<AreaTypeEnum>  implements ContextualDeserializer {

    private Class clazz;

    @Override
    public  AreaTypeEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Class<?> rawClass = clazz;
        String source = p.getText();
        Method method= null;
        try {
            method = rawClass.getMethod("name",null);
            for (Object enumObj : rawClass.getEnumConstants()) {
                AreaTypeEnum e=(AreaTypeEnum)enumObj;
                if (source.equals(String.valueOf(e.getNumValue()))||source.equals(String.valueOf(e.getValue()))) {
                    return e;
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonDeserializer createContextual(DeserializationContext ctx, BeanProperty property) throws JsonMappingException {
        Class rawCls = ctx.getContextualType().getRawClass();
        AreaTypeEnumDeserializer clone = new AreaTypeEnumDeserializer();
        clone.setClazz(rawCls);
        return clone;
    }
}
