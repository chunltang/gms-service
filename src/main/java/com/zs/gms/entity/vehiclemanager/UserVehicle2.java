package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 调度员和车辆对应关系
 * */
@Data
//@TableName(value = "t_user_vehicle1")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserVehicle2 implements Serializable {

    private static final long serialVersionUID = -6564958081055481504L;

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "USERID")
    private Integer userId;

    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;
}
