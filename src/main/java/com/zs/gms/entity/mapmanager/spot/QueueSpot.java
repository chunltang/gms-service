package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseSpot;
import lombok.Data;

import java.io.Serializable;

/**
 * 排队点
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueSpot  extends BaseSpot implements Serializable {
    private static final long serialVersionUID = 4082602296388252730L;

    /**
     * 属性类型,排队点”queueSpot”
     * */
    private String attributeType="queueSpot";
}
