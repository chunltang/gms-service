package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseSpot;
import lombok.Data;

import java.io.Serializable;

/**
 * 停车位
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingSpot extends BaseSpot implements Serializable {
    private static final long serialVersionUID = -1752673752550319630L;

    /**
     * 属性类型,停车位”parkingSpot”
     * */
    private String attributeType="parkingSpot";
}
