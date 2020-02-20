package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 车辆类型和车辆对应关系
 * */
@Data
@TableName(value = "t_vehicle_vehicleType")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleVehicleType implements Serializable {
    private static final long serialVersionUID = 5743232175560929930L;

    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    @TableField(value = "VEHICLETYPEID")
    private Integer vehicleTypeId;
}
