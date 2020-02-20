package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import java.io.Serializable;

/**
 * 停车场
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ParkingLot extends BaseArea  implements Serializable {

    private static final long serialVersionUID = -1722805943137549470L;
}
