package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseSpot;
import lombok.Data;

import java.io.Serializable;


/**
 * 加水点
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefillSpot extends BaseSpot implements Serializable {
    private static final long serialVersionUID = 5955752477609077028L;

    /**
     * 属性类型,加水点”refillSpot”
     * */
    private String attributeType="refillSpot";
}