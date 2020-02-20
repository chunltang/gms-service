package com.zs.gms.entity.monitor;

import lombok.Data;

@Data
public class Vertex {
    //8字节
    private double x;
    //8字节
    private double y;
    //8字节
    private double z;

    private int type;
    //横摆角
    private float direction;
    //坡度
    private float slope;
    //曲率
    private float curvature;
    //8字节
    private double leftDistance;
    //8字节
    private double rightDistance;
    //8字节
    private double maxSpeed;
    //8字节
    private double speed;
    //8字节
    //当前点与轨迹头的距离
    private double s;
    //一个字节
    //是否倒车
    private boolean reverse;
}
