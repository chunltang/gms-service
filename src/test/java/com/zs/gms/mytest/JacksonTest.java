package com.zs.gms.mytest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zs.gms.entity.monitor.VehicleLive;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;

public class JacksonTest {

    /*public static void main(String[] args) throws IOException{

        User user = new User();
        user.setUserName("tcl");
        user.setPassword("123456");
        user.setRoleName("admin");
        user.setUserId(1111L);
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(user);

        ObjectMapper mapper1 = new ObjectMapper();
        User value = mapper1.readValue(s, User.class);
        System.out.println(s);
        System.out.println(value);

    }*/

   /* public static void main(String[] args) {
        VehicleLive live=new VehicleLive();
        live.setAngle(11.0f);
        //live.setPoint(new Point(1f,2f,3f));
        live.setSpeed(12f);
        live.setAddTime(System.currentTimeMillis());
        live.setVapMode(1123);
        live.setVehState(123);

        Map<String,Object> map=new HashMap<>();
        map.put("live",live);
        map.put("name",111);
        map.put("x",111);
        map.put("y",111);
        map.put("z",111);
        String s = JSON.toJSONString(map);
        System.out.println(diGuiParse(JSON.parseObject(s)));
    }

    public static Object diGuiParse(Map<String,Object> data){
        Set<Map.Entry<String, Object>> entries = data.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if(value instanceof JSONObject){
                JSONObject object = (JSONObject) value;
                data.put(entry.getKey(),diGuiParse((Map)object));
            }
        }
       return data;
    }*/

    /*public static void main(String[] args) throws IOException {
        Map<String,Object> map=new HashMap<>();
        map.put("x",1f);
        map.put("y",1f);
        map.put("z",1f);
        map.put("speed",2);
        map.put("vapMode",2);
        map.put("vehState",2);
        ObjectMapper mapper=new ObjectMapper();
        String value = mapper.writeValueAsString(map);
        System.out.println(value);


        VehicleLive live = mapper.readValue(value, VehicleLive.class);
        System.out.println(live);
    }*/

    public static void main(String[] args) {
        VehicleLive live = new VehicleLive();
        //live.setPoint(new Point(1f, 2f, 3f));
        live.setSpeed(12f);
        live.setAddTime(System.currentTimeMillis());
        live.setVapMode(1123);
        live.setVehState(123);
        Map<String, Object> map = new HashMap<>();
        map.put("live", live);
        map.put("name", 111);
        map.put("x", 111);
        map.put("logLevelEnum", "0");
        map.put("y", 111);
        map.put("z","111");
        map.put("ch", 'x');
        map.put("time", "2019-11-11 11:11:11");
        map.put("arr", new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4})));
        String s = JSON.toJSONString(map);
        long t1 = System.currentTimeMillis();
        VehicleLive live1 = jsonToBean(s, VehicleLive.class);
        //VehicleLive live1 = JSONObject.parseObject(s, VehicleLive.class);
        System.out.println(live1);
        System.out.println(System.currentTimeMillis()-t1);
    }


    /**
     * 按对象属性名称生层次转换json数据
     * */
    public static <T> T jsonToBean(String jsonStr, Class target) {
        if (Map.class.isAssignableFrom(target)) {
            return (T) JSONObject.parseObject(jsonStr, Map.class);
        } else if (List.class.isAssignableFrom(target)) {
            return (T) JSONArray.parseObject(jsonStr, List.class);
        } else {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            Object t = null;
            try {
                t = target.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Field[] fields = target.getDeclaredFields();
            handleField(fields,t,jsonObject);
            return (T)t;
        }
    }

    /**
     * 字段设值
     * */
    public static void handleField(Field[] fields,Object t,JSONObject jsonObject){
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())) {
                continue;
            }
            Class<?> type = field.getType();
            Object value = containKey(jsonObject, field.getName());
            if (null != value) {
                field.setAccessible(true);
                try {
                    if (value instanceof JSONObject) {//map,obj
                        field.set(t, JSONObject.parseObject(String.valueOf(value), type));
                        continue;
                    }
                    if (value instanceof JSONArray) {//数组,list
                        field.set(t, JSONArray.parseObject(String.valueOf(value), type));
                        continue;
                    }
                    if (Date.class.isAssignableFrom(type)) {//date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = dateFormat.parse(value.toString());
                        field.set(t, date);
                        continue;
                    }

                    if (type.isPrimitive()) { //基础数据类型
                        if (type.isAssignableFrom(float.class)) {
                            field.set(t, Float.parseFloat(value.toString()));
                        }else if (type.isAssignableFrom(int.class)) {
                            field.set(t, Integer.parseInt(value.toString()));
                        }else if (type.isAssignableFrom(boolean.class)) {
                            field.set(t, Boolean.parseBoolean(value.toString()));
                        }else if (type.isAssignableFrom(long.class)) {
                            field.set(t, Long.parseLong(value.toString()));
                        }else if (type.isAssignableFrom(short.class)) {
                            field.set(t, Short.parseShort(value.toString()));
                        }else if (type.isAssignableFrom(char.class)) {
                            field.set(t, value.toString().charAt(0));
                        }else if (type.isAssignableFrom(double.class)) {
                            field.set(t, Double.parseDouble(value.toString()));
                        }else if (type.isAssignableFrom(byte.class)) {
                            field.set(t, Byte.parseByte(value.toString()));
                        }
                        continue;
                    }

                    if(hasField(type,"TYPE")){//包装类获取基础数据类型
                        if(Character.class.isAssignableFrom(type)){
                            Method m = type.getDeclaredMethod("valueOf", Character.class);
                            Object invoke = m.invoke(null, value.toString().charAt(0));
                            field.set(t, invoke);
                        }else{
                            Method  m = type.getDeclaredMethod("valueOf", String.class);
                            Object invoke = m.invoke(null, value.toString());
                            field.set(t, invoke);
                        }
                        continue;
                    }

                    if(type.isEnum()){//枚举类型
                        Object[] enumConstants = type.getEnumConstants();
                        Method getValue = type.getMethod("getValue");//需要定义getValue方法
                        for (Object enumConstant : enumConstants) {
                            Object invoke = getValue.invoke(enumConstant);
                            if(null!=invoke&&String.valueOf(invoke).equals(value.toString())){
                                field.set(t, enumConstant);
                            }
                        }
                        continue;
                    }

                    if(String.class.isAssignableFrom(type)){
                        field.set(t, String.valueOf(value));
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("反射异常");
                    continue;
                }
            }else{//转换对象中的自定义对象属性
                Field[] declaredFields = type.getDeclaredFields();
                try {
                    Object o = type.newInstance();
                    field.setAccessible(true);
                    field.set(t,o);
                    handleField(declaredFields,o,jsonObject);
                }catch (Exception e){
                    System.out.println(type.getName()+"没有构造函数");
                    continue;
                }
            }
        }
    }

    /**
     * TYPE,判断是否是包装类，只有包装类才有TYPE字段
     * */
    public static boolean hasField(Class clazz,String name){
        try {
            clazz.getField(name);
        }catch (NoSuchFieldException e){
            return false;
        }
        return true;
    }


    /**
     * 判断json中是否有指定键
     * */
    public static Object containKey(JSONObject jsonObject, String containKey) {
        if (jsonObject.containsKey(containKey)) {
            return jsonObject.get(containKey);
        }

        Set<Map.Entry<String, Object>> entries = jsonObject.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object value = entry.getValue();
            if (value instanceof Map || value instanceof JSONObject) {
                return containKey((JSONObject) value, containKey);
            }
        }
        return null;
    }
}

