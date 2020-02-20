package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 调度员和车辆对应关系
 * */
@Data
@TableName(value = "t_user_vehicle")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVehicle implements Serializable {

    private static final long serialVersionUID = -6564958081055481504L;

    @TableField(value = "USERID")
    private Integer userId;

    @TableField(value = "VEHICLEID")
    private Integer vehicleId;
}
