package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseSpot;
import lombok.Data;

import java.io.Serializable;

/**
 * 卸矿点
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnloadMineralSpot extends BaseSpot implements Serializable {
    private static final long serialVersionUID = -1042360457901361150L;

    /**
      *  属性类型
      */
    private String attributeType = "unloadMineralSpot";
}
