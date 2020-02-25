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
public class IdPoint extends AnglePoint implements Serializable {

    private static final long serialVersionUID = 7327603663764408422L;

    public IdPoint(){}

    public IdPoint(double x, double y, double z, int id,double yawAngle){
        super(x,y,x,yawAngle);
        this.id=id;
    }

    public int id;
}
