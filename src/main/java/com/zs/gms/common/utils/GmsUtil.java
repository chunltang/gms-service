package com.zs.gms.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.entity.RedisKeyPool;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.handler.IEnumDescSerializer;
import com.zs.gms.common.handler.IEnumDeserializer;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.shiro.SecurityUtils;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

@Slf4j
public class GmsUtil {

    /**
     * {}匹配替换
     */
    public static String format(String format, Object... objs) {
        Pattern pattern = Pattern.compile("\\{([^}])*\\}");
        Matcher matcher = pattern.matcher(format);
        int index = 0;
        int num = 0;
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            int start = matcher.start();
            sb.append(format.substring(index, start));
            if (null != objs && objs.length >= num + 1) {
                sb.append(objs[num]);
            }
            index = matcher.end();
            num++;
        }
        if (num == 0) {
            sb.append(format);
        }
        if (index < format.length()) {
            sb.append(format.substring(index));
        }
        return sb.toString();
    }

    /**
     * 对象序列化
     * */
    public static byte[] serializer(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream os = new ObjectOutputStream(bos)) {
            os.writeObject(obj);
            return bos.toByteArray();
        } catch (Exception e) {
            log.error("对象序列化失败",e);
        }
        return null;
    }

    /**
     * 对象反序列化
     * */
    public static Object deserializer(byte[] bytes) {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
             ObjectInputStream in = new ObjectInputStream(bin)){
            return in.readObject();
        } catch (Exception e) {
            log.error("对象反序列化失败",e);
        }
        return null;
    }


    /**
     * 获取当前时间，毫秒
     */
    public static long getCurTime() {
        return System.currentTimeMillis();
    }

    /**
     * 截取匹配最后字符串的后一段
     */
    public static String subLastStr(String key, String match) {
        if (key.contains(match)) {
            return key.substring(key.lastIndexOf(match) + 1);
        }
        return "";
    }

    /**
     * 截取匹配最后字符串的前一段
     */
    public static String subIndexStr(String key, String match) {
        if (key.contains(match)) {
            return key.substring(0, key.lastIndexOf(match) + 1);
        }
        return "";
    }

    /**
     * 字符串替换
     */
    public static String replaceAll(String origin, String oldStr, String newStr) {
        return origin.replaceAll(oldStr, newStr);
    }

    /**
     * 获取监听库中的消息，带有枚举转换
     */
    public static <T> T getMessage(String key, Class<T> clazz) {
        Object json = RedisService.get(StaticConfig.MONITOR_DB, key);
        if (ObjectUtils.isEmpty(json))
            return null;
        return GmsUtil.toObjIEnum(json, clazz);
    }

    /**
     * 包装类型转换
     */
    public static <T> T typeTransform(Object obj, Class<T> clazz) {
        try {
            String s = String.valueOf(obj);
            Method method = clazz.getDeclaredMethod("valueOf", String.class);
            if (method != null) {
                return (T) method.invoke(null, s);
            }
            return null;
        } catch (Exception e) {
            log.error("类型转换失败", e);
            return null;
        }
    }

    /**
     * 对象属性转map，空属性、final踢出
     */
    public static Map<String, Object> obj2Map(Object obj) {
        Map<String, Object> map = new HashMap<>();
        if (obj != null) {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isFinal(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                String name = field.getName();
                try {
                    Object value = field.get(obj);
                    if (value != null) {
                        map.put(name, value);
                    }
                } catch (IllegalAccessException e) {
                    log.error("反射获取属性值失败", e);
                }
            }
        }
        return map;
    }

    /**
     * 基础类型和包装类型转换
     */
    public static Class typeClass(Class clazz) {
        Class re = null;
        if (null != clazz && clazz.isPrimitive()) {
            if (clazz.equals(int.class)) {
                re = Integer.class;
            } else if (clazz.equals(short.class)) {
                re = Short.class;
            } else if (clazz.equals(long.class)) {
                re = Long.class;
            } else if (clazz.equals(byte.class)) {
                re = Byte.class;
            } else if (clazz.equals(char.class)) {
                re = Character.class;
            } else if (clazz.equals(boolean.class)) {
                re = Boolean.class;
            } else if (clazz.equals(float.class)) {
                re = Float.class;
            } else if (clazz.equals(double.class)) {
                re = Double.class;
            }
        }
        return re;
    }

    /**
     * 转为字符串
     */
    public static String getString(Object obj) {
        if (null == obj) {
            return "";
        }
        return String.valueOf(obj);
    }

    /**
     * put并返回value
     */
    public static <K, T> T mapPutAndGet(Map<K, T> map, K key, T value) {
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            map.put(key, value);
            return value;
        }
    }

    /**
     * map键转为大写
     */
    public static void toLowerCase(Map<String, Object> map) {
        if (MapUtils.isEmpty(map)) {
            return;
        }
        Set<String> keys = new HashSet<>(map.keySet());
        keys.stream().forEach(each -> {
            Object value = map.get(each);
            map.remove(each);
            map.put(each.toUpperCase(), value);
        });
    }

    public static boolean mapContains(Map map, String... keys) {
        if (map != null && !map.isEmpty() && keys != null) {
            int len = 0;
            for (String key : keys) {
                if (map.containsKey(key)) {
                    len++;
                }
            }
            if (len == keys.length) {
                return true;
            }
        }
        return false;
    }

    public static String toJson(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("jackson转换数据失败", e);
        }
        return "";
    }

    public static <T> T toObj(Object obj, Class<T> tClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            return mapper.readValue(String.valueOf(obj), tClass);
        } catch (IOException e) {
            log.error("jackson读取数据失败", e);
        }
        return null;
    }

    //转换list，map
    public static <T> T toCollectObj(String json, Class collect, Class target) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JavaType jt = mapper.getTypeFactory().constructParametricType(collect, target);
            return mapper.readValue(json, jt);
        } catch (IOException e) {
            log.error("jackson读取数据失败", e);
        }
        return null;
    }


    /**
     * 包含转换IEnum枚举
     */
    public static <T> T toObjIEnum(Object obj, Class<T> tClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addDeserializer(Enum.class, new IEnumDeserializer());
            mapper.registerModule(simpleModule);
            return mapper.readValue(String.valueOf(obj), tClass);
        } catch (IOException e) {
            log.error("jackson读取数据失败", e);
        }
        return null;
    }

    /**
     * 转实现IEnum,Desc接口的枚举
     */
    public static String toJsonIEnumDesc(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(Enum.class, new IEnumDescSerializer());
            mapper.registerModule(simpleModule);
            return mapper.writeValueAsString(obj);
        } catch (IOException e) {
            log.error("jackson读取数据失败", e);
        }
        return null;
    }

    /**
     * =========数据判空=========
     */
    public static boolean StringNotNull(String obj) {
        return null != obj && obj.length() > 0;
    }

    public static boolean objNotNull(Object obj) {
        return null != obj;
    }

    public static boolean mapNotNull(Map map) {
        return null != map && !map.isEmpty();
    }

    public static boolean CollectionNotNull(Collection collection) {
        return null != collection && collection.size() > 0;
    }

    public static boolean allObjNotNull(Object... objs) {
        for (Object obj : objs) {
            if (!objNotNull(obj)) {
                return false;
            }
        }
        return true;
    }

    public static boolean arrayNotNull(Object[] array) {
        return null != array && array.length > 0;
    }
}
