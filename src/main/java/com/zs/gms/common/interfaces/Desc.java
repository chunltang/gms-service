package com.zs.gms.common.interfaces;

public interface Desc {

    String getDesc();

    default boolean isPrint(){
        return true;
    };
}
