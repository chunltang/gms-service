package com.zs.gms.jdk;

interface Action{
    public String sysName(String aaa);

    default String send(){
        return "send";
    }
}