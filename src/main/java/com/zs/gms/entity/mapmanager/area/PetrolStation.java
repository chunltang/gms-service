package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import java.io.Serializable;

/**
 * 加油站
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PetrolStation extends BaseArea implements Serializable {

    private static final long serialVersionUID = 5069471367727195084L;
}
