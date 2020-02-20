package com.zs.gms.jdk;

import sun.reflect.Reflection;

import java.lang.reflect.Method;

public class RefectionTest {

    public static void main(String[] args) {

        System.out.println("before");
        MyInvoke myInvoke = new MyInvoke();
        myInvoke.sys();
    }

    public static class MyInvoke{

        {
            System.out.println("非静态代码块");
        }

        public MyInvoke(){
            System.out.println("MyInvoke构造");
        }

        static{
            System.out.println("MyInvoke初始化");
        }

        public void sys(){
            System.out.println("your are dog!");
            Class<?> callerClass = Reflection.getCallerClass(0);
            System.out.println(callerClass.getName());
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement element=stackTrace[i];
                if(getClass().getName().equals(element.getClassName())){
                    System.out.println("my:"+element.getClassName());
                    System.out.println("invoke:"+stackTrace[i+1].getClassName()+":"+stackTrace[i+1].getMethodName()+":"+stackTrace[i+1].getFileName());
                    Class<?> aClass = null;
                    try {
                        aClass = Class.forName(stackTrace[i + 1].getClassName());
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println(aClass.getName());
                }
            }
        }

        public static class TestInit{

            static{
                System.out.println("TestInit初始化");
            }
        }
    }
}
