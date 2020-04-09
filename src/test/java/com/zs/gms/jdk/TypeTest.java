package com.zs.gms.jdk;

import com.zs.gms.common.annotation.NotNull;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.SpringContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TypeTest {

    public static void main(String[] args) {
        Object[] objects = {1, "",true,new TypeTest(),new ArrayList<String>(),new HashMap<String,Object>()};
        for (Object object : objects) {
            boolean from = int.class.isAssignableFrom(Integer.class);
            boolean from1 = Integer.class.isAssignableFrom(int.class);
            Class<?> aClass = object.getClass();
            String name = aClass.getName();
            Type superclass = aClass.getGenericSuperclass();
            String typeName = superclass.getTypeName();
            String typeName1 = aClass.getTypeName();
            Object[] signers = aClass.getSigners();
            Class<?> superclass1 = aClass.getSuperclass();
            Class<?> declaringClass = aClass.getDeclaringClass();
            Class<?>[] classes = aClass.getClasses();
            Class<?>[] declaredClasses = aClass.getDeclaredClasses();
            Class<?> componentType = aClass.getComponentType();
            Class<?> enclosingClass = aClass.getEnclosingClass();
            System.out.println(11);
        }
        ClassTask classTask = new ClassTask();
        classTask.setClassName("com.zs.gms.jdk.TypeTest");
        classTask.setMethodName("print");
        classTask.setParams(new Object[]{1,1,"a",false,new ArrayList(),new HashMap()});
        new TypeTest().withClassTask(classTask);
    }

    public void print(int a, Integer b, String c, boolean d, List<String> f, Map<String,Object> g){
        System.out.println(1);
    }

    public void withClassTask(ClassTask classTask){//使用该方法会替换task参数

        ClassLoader loader = this.getClass().getClassLoader();
        try {
            Class<?> aClass = loader.loadClass(classTask.getClassName());
            Method[] methods = aClass.getDeclaredMethods();
            Method target=null;
            Object[] params = classTask.getParams();
            String methodName = classTask.getMethodName();
            for (Method method : methods) {
                if(method.getName().equals(methodName)){
                    Class<?>[] types = method.getParameterTypes();
                    if(types.length!=classTask.getParams().length){
                        continue;
                    }
                    for (int i = 0; i < types.length; i++) {
                        Class typeClass=types[i];
                        if(types[i].isPrimitive()){
                             typeClass = GmsUtil.typeClass(types[i]);
                        }
                        if(!typeClass.isAssignableFrom(params[i].getClass())){
                           break;
                        }
                    }
                    target=method;
                    break;
                }
            }
            if(null!=target){
                Runnable task;
                final Method method=target;
                if(Modifier.isStatic(target.getModifiers())){
                    task= ()->{
                        try {
                            method.invoke(null,params);
                        } catch (Exception e) {
                            log.error("反射方法调用失败，method:{},args:{}",methodName,params);
                        }
                    };
                }else{
                    //Object bean = SpringContextUtil.getBean(aClass);
                    TypeTest test = new TypeTest();
                    task= ()->{
                        try {
                            method.invoke(test,params);
                        } catch (Exception e) {
                            log.error("反射方法调用失败，method:{},args:{}",methodName,params);
                        }
                    };
                }
                task.run();
            }
        } catch (ClassNotFoundException e) {
            log.error("根据classTask反射异常",e);
        }

    }

    @Data
    public static class ClassTask{

        @NotNull
        private String className;

        @NotNull
        private String methodName;

        @NotNull
        private Object[] params;
    }
}
