package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import java.io.Serializable;

/**
 *加水站
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefillStation extends BaseArea implements Serializable {

    private static final long serialVersionUID = 6913647502652294694L;
}

