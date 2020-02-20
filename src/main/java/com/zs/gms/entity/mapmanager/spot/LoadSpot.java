package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseSpot;
import lombok.Data;

import java.io.Serializable;

/**
 * 装载点
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadSpot  extends BaseSpot implements Serializable {

    private static final long serialVersionUID = 6170447015002062790L;

    /**
     * 属性类型,排队点”loadSpot”
     * */
    private String attributeType="loadSpot";
}