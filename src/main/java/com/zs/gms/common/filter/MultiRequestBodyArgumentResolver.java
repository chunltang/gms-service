package com.zs.gms.common.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.annotation.MultiRequestBody;
import com.zs.gms.common.exception.GmsException;
import com.zs.gms.common.utils.DateUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.core.MethodParameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MultiRequestBodyArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String JSONBODY_ATTRIBUTE = "JSON_REQUEST_BODY";

    /**
     * @param parameter 方法参数
     * @return 支持的类型
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持带@MultiRequestBody注解的参数
        return parameter.hasParameterAnnotation(MultiRequestBody.class);
    }

    /**
     * 注意：非基本类型返回null会报空指针异常，要通过反射或者JSON工具类创建一个空对象
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String jsonBody = getRequestBody(webRequest);
        if (StringUtils.isEmpty(jsonBody)) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(jsonBody);
        // 根据@MultiRequestBody注解value作为json解析的key
        MultiRequestBody parameterAnnotation = parameter.getParameterAnnotation(MultiRequestBody.class);
        //注解的value是JSON的key
        String key = parameterAnnotation.value();
        Object value;
        if (StringUtils.isNotEmpty(key)) {
            value = jsonObject.get(key);
            // 如果设置了value但是解析不到，报错
            if (value == null && parameterAnnotation.required()) {
                throw new GmsException(String.format("必要参数不存在:%s", key));
            }
        } else {
            // 注解没有设置value，则用参数名当做json的key
            key = parameter.getParameterName();
            value = jsonObject.get(key);
        }

        // 获取的注解后的类型
        Class<?> parameterType = parameter.getParameterType();
        // 通过注解的value或者参数名解析，能拿到value进行解析
        if (value != null) {

            if (parameterType.isPrimitive()) {//基本类型
                return parsePrimitive(parameterType.getName(), value);
            }

            if (isBasicDataTypes(parameterType)) {// 基本类型包装类
                return parseBasicTypeWrapper(parameterType, value);

            } else if (parameterType == String.class) {//字符串类型
                return value.toString();
            } else if (parameterType.isEnum()) {//枚举类型
                Method method = parameterType.getMethod("name", null);
                for (Object enumConstant : parameterType.getEnumConstants()) {
                    if (enumConstant instanceof IEnum) {//传的枚举value值
                        IEnum iEnum = (IEnum) enumConstant;
                        if (iEnum.getValue().equals(value)) {
                            return enumConstant;
                        }
                    }
                    if (method.invoke(enumConstant).equals(value)) {//传的枚举名称
                        return enumConstant;
                    }
                }
                return null;
            }else if(parameterType==Date.class){
                DateTimeFormat format = parameter.getParameterAnnotation(DateTimeFormat.class);
                if(null!=format){
                    return DateUtil.formatStringTime(String.valueOf(value),format.pattern());
                }
                return null;
            }
            // 其他复杂对象
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(value.toString(), parameterType);
        }

        // 解析不到则将整个json串解析为当前参数类型
        if (isBasicDataTypes(parameterType)) {
            if (parameterAnnotation.required()) {
                throw new IllegalArgumentException(String.format("必要参数不存在:%s", key));
            } else {
                return null;
            }
        }

        // 非基本类型，不允许解析所有字段，必备参数则报错，非必备参数则返回null
        if (!parameterAnnotation.parseAllFields()) {

            if (parameterAnnotation.required()) {// 如果是必传参数抛异常
                throw new IllegalArgumentException(String.format("必要参数不存在:%s", key));
            }
            return null;
        }

        Object result;// 非基本类型，允许解析，将外层属性解析
        try {
            ObjectMapper mapper = new ObjectMapper();
            result = mapper.readValue(jsonObject.toString(), parameterType);
        } catch (JSONException jsonException) {
            result = null;
        }

        if (!parameterAnnotation.required()) {//如果非必要参数直接返回，否则如果没有一个属性有值则报错
            return result;
        } else {
            boolean haveValue = false;
            Set<Field> fields = new HashSet<>();
            fields.addAll(Arrays.asList(parameterType.getDeclaredFields()));
            fields.addAll(Arrays.asList(parameterType.getFields()));
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.get(result) != null) {
                    haveValue = true;
                    break;
                }
            }
            if (!haveValue) {
                throw new IllegalArgumentException(String.format("必要参数不存在:%s", key));
            }
            return result;
        }
    }

    /**
     * 基本类型解析
     */
    private Object parsePrimitive(String parameterTypeName, Object value) {
        final String booleanTypeName = "boolean";
        if (booleanTypeName.equals(parameterTypeName)) {
            return Boolean.valueOf(value.toString());
        }
        final String intTypeName = "int";
        if (intTypeName.equals(parameterTypeName)) {
            return Integer.valueOf(value.toString());
        }
        final String charTypeName = "char";
        if (charTypeName.equals(parameterTypeName)) {
            return value.toString().charAt(0);
        }
        final String shortTypeName = "short";
        if (shortTypeName.equals(parameterTypeName)) {
            return Short.valueOf(value.toString());
        }
        final String longTypeName = "long";
        if (longTypeName.equals(parameterTypeName)) {
            return Long.valueOf(value.toString());
        }
        final String floatTypeName = "float";
        if (floatTypeName.equals(parameterTypeName)) {
            return Float.valueOf(value.toString());
        }
        final String doubleTypeName = "double";
        if (doubleTypeName.equals(parameterTypeName)) {
            return Double.valueOf(value.toString());
        }
        final String byteTypeName = "byte";
        if (byteTypeName.equals(parameterTypeName)) {
            return Byte.valueOf(value.toString());
        }
        return null;
    }

    /**
     * 基本类型包装类解析
     */
    private Object parseBasicTypeWrapper(Class<?> parameterType, Object value) {
        if (Number.class.isAssignableFrom(parameterType)) {
            Number number = (Number) value;
            if (parameterType == Integer.class) {
                return number.intValue();
            } else if (parameterType == Short.class) {
                return number.shortValue();
            } else if (parameterType == Long.class) {
                return number.longValue();
            } else if (parameterType == Float.class) {
                return number.floatValue();
            } else if (parameterType == Double.class) {
                return number.doubleValue();
            } else if (parameterType == Byte.class) {
                return number.byteValue();
            }
        } else if (parameterType == Boolean.class) {
            return value.toString();
        } else if (parameterType == Character.class) {
            return value.toString().charAt(0);
        }
        return null;
    }

    /**
     * 判断是否为基本数据类型包装类
     */
    private boolean isBasicDataTypes(Class clazz) {
        Set<Class> classSet = new HashSet<>();
        classSet.add(Integer.class);
        classSet.add(Long.class);
        classSet.add(Short.class);
        classSet.add(Float.class);
        classSet.add(Double.class);
        classSet.add(Boolean.class);
        classSet.add(Byte.class);
        classSet.add(Character.class);
        return classSet.contains(clazz);
    }

    /**
     * 获取请求体JSON字符串
     */
    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);

        // 有就直接获取
        String jsonBody = (String) webRequest.getAttribute(JSONBODY_ATTRIBUTE, NativeWebRequest.SCOPE_REQUEST);
        // 没有就从请求中读取
        if (StringUtils.isEmpty(jsonBody)) {
            try {
                jsonBody = IOUtils.toString(servletRequest.getReader());
                webRequest.setAttribute(JSONBODY_ATTRIBUTE, jsonBody, NativeWebRequest.SCOPE_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonBody;
    }
}

