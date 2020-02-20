package com.zs.gms.common.handler;

import com.baomidou.mybatisplus.core.enums.IEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
public class EnumConvertHandler implements ConverterFactory<String, IEnum> {

    @Override
    public <T extends IEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToIEum<>(targetType);
    }


    private static class StringToIEum<T extends IEnum> implements Converter<String, T> {
        private Class<T> targerType;
        public StringToIEum(Class<T> targerType) {
            this.targerType = targerType;
        }

        @Override
        public T convert(String source) {
            if (StringUtils.isEmpty(source)) {
                return null;
            }
            try {
                return (T) EnumConvertHandler.getIEnum(this.targerType, source);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public static <T extends IEnum> Object getIEnum(Class<T> targerType, String source) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method= targerType.getMethod("name",null);
        for (T enumObj : targerType.getEnumConstants()) {
            String name=(String)method.invoke(enumObj);
            if (source.equals(String.valueOf(enumObj.getValue()))||source.equals(name)) {
                return enumObj;
            }
        }
        return null;
    }
}
