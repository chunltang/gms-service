package com.zs.gms.enums.monitor;

public interface Desc {

    String getDesc();

    default boolean isPrint(){
        return true;
    };
}
