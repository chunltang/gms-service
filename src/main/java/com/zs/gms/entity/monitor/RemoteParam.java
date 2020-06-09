package com.zs.gms.entity.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 远程控制参数
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RemoteParam {

    private int vehicleId;

    private double dvalue;

    private int ivalue;
}
