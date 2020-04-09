package com.zs.gms.entity.monitor;

public abstract class LiveInfo {

    public abstract Type getType();

    public enum Type{
        GPS,
        VEHICLE
    }
}
