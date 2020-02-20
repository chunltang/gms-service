package com.zs.gms.jdk;

import java.beans.EventHandler;

public class EventHandlerTest {

    public static void main(String[] args) {
        EventHandlerTest obj=new EventHandlerTest();
        obj.addAction(EventHandler.create(Action.class,obj, "exec"));//使用Acton接口代理执行exec
    }

    public void addAction(Action action){
        System.out.println("begin");
        action.sysName("send");//实际执行exec方法
        System.out.println("end");
    }
    public void exec(){
        System.out.println("exec");
    }
}




