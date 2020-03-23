package com.zs.gms.mytest;

import com.alibaba.fastjson.JSON;
import com.zs.gms.common.utils.SpringContextUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CastTest {

    /*public static void main(String[] args) {
        Object obj=new CastTest();
        Class<?> aClass = obj.getClass();
        String typeName = aClass.getTypeName();
        Object[] signers = aClass.getSigners();
        String simpleName = aClass.getSimpleName();
        Class<?>[] interfaces = aClass.getInterfaces();
        Class<?> superclass = aClass.getSuperclass();
        System.out.println(typeName);
    }

    public void get(){
        System.out.println("aa");
    }*/

    /*public static void main(String[] args) {
         String a="wsada";
         Map<String,Object> map=new HashMap<>();
         map.put("k",a);
        System.out.println(JSON.toJSONString(a).equals(a));
        System.out.println(JSON.toJSONString(a));
        System.out.println(a);
        System.out.println(JSON.isValidObject(JSON.toJSONString(a)));
    }*/

    public static void main(String[] args) {
        long i=11;
        long b=11;
        System.out.println(Long.valueOf(i).equals(Long.valueOf(b)));
    }
}
