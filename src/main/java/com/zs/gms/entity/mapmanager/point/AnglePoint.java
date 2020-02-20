package com.zs.gms.entity.mapmanager.point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import java.io.Serializable;

/**
 * 坐标点及其横摆角
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AnglePoint extends Point implements Serializable {

    private static final long serialVersionUID = 7327603663764408422L;

    public AnglePoint(){}

    public AnglePoint(double x,double y,double z,double yawAngle){
        super(x,y,x);
        this.yawAngle=yawAngle;
    }
    /**
     * 横摆角
     * */
    public double yawAngle;
}
