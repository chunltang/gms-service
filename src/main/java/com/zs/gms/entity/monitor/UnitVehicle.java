package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 矿车和调度单元关系
 * */
@Data
@TableName("t_unit_vehicle")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitVehicle implements Serializable {

    /**
     * 数据id
     * */
    @TableId(value = "UVID",type = IdType.AUTO)
    private Integer uvId;

    /**
     * 调度单元id
     * */
    @TableField(value = "UNITID")
    private Integer unitId;

    /**
     * 矿车编号
     * */
    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    private Date addTime;

    /**
     * 添加用户
     * */
    @TableField(value = "CREATEUSERID")
    private Integer createUserId;


    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;
}
