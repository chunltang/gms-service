package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.annotation.NotNull;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "t_dispatch_status")
public class DispatchStatus implements Serializable {

    @TableId(value = "ID",type = IdType.AUTO)
    public Integer id;
    /**
     * 创建时间
     * */
    @TableField(value = "CREATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createTime;

    /**
     * 车辆用户
     * */
    @NotNull
    @TableField(value = "USERID")
    public Integer userId;

    /**
     * 车辆编号
     * */
    @NotNull
    @TableField(value = "VEHICLEID")
    public Integer vehicleId;

    /**
     * 矿车容量
     * */
    @NotNull
    @TableField(value = "VEHICLECAPACITY")
    public float vehicleCapacity;

    @TableField(value = "STATUS")
    public DispatchStateEnum status;

    /**
     * 调度单元id
     * */
    @NotNull
    @TableField(value = "UNITID")
    public Integer unitId;

    /**
     * 地图id
     * */
    @NotNull
    @TableField(value = "MAPID")
    public Integer mapId;

    /**
     * 电铲id
     * */
    @NotNull
    @TableField(value = "EXCAVATORID")
    public Integer excavatorId;


    /**
     * 电铲用户id
     * */
    @NotNull
    @TableField(value = "EXCAVATORUSERID")
    public Integer excavatorUserId;

    /**
     * 矿物id
     * */
    @NotNull
    @TableField(value = "MINERALID")
    public Integer mineralId;

    /**
     * 卸载区id
     * */
    @NotNull
    @TableField(value = "UNLOADID")
    public Integer unLoadId;
}
