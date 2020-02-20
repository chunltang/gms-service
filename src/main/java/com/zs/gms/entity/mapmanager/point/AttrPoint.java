package com.zs.gms.entity.mapmanager.point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AttrPoint extends Point implements Serializable {

    public AttrPoint(){}

    public AttrPoint(double x,double y,double z,Integer attr){
        super(x,y,x);
        this.attr=attr;
    }
    /**
     * 横摆角,1正向，0倒车
     * */
    public Integer attr;
}
