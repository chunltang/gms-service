package com.zs.gms.entity.mapmanager;

import lombok.Data;

import java.io.Serializable;

/**
 * 点对象
 * */
@Data
public class Point implements Serializable {
    private static final long serialVersionUID = 1448645547360774887L;

    public Point(){};

    public Point(double x,double y,double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    /**
     * x坐标
     * */
    public double x;

    /**
     * y坐标
     * */
    public double y;

    /**
     * z坐标
     * */
    public double z;

}
