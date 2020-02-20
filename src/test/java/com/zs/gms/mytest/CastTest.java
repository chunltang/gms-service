package com.zs.gms.mytest;

import com.zs.gms.common.utils.SpringContextUtil;

public class CastTest {

    public static void main(String[] args) {
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
    }
}
