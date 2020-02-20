package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

/**
 * 车辆实时数据
 * */
@Data
@TableName(value = "t_vehicleLive")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleLive implements Serializable {
    private static final long serialVersionUID = 3431465223271449999L;

    @TableId(value = "DATAID",type = IdType.AUTO)
    private Long dataId;

    /**
     * 车辆标识
     * */
    @TableField(value = "VEHICLESIGN")
    private String vehicleSign;
    /**
     * 局部坐标(x,y,z,yawAngle)
     * */
    @TableField(value = "X")
    private Long x;

    @TableField(value = "Y")
    private Long y;

    @TableField(value = "Z")
    private Long z;

    @TableField(value = "ANGLE")
    private Float angle;

    /**
     * 车辆速度
     * */
    @TableField(value = "SPEED")
    private Float speed;

    /**
     * 车辆加速度
     * */
    @TableField(value = "ACCELERATION")
    private Float acceleration;

    /**
     * 车载模式
     * */
    @TableField(value = "VAPMODE")
    private Integer vapMode;

    /**
     * 车载状态
     * */
    @TableField(value = "VAPSTATE")
    private Integer vapState;

    /**
     * 有效时间
     * */
    @TableField(value = "EXPIRETIME")
    private Integer expireTime;

    /**
     * 车辆状态
     * */
    @TableField(value = "VEHSTATE")
    private Integer vehState;

    /**
     * 调度状态
     * */
    @TableField(value = "DISPATCHSTATE")
    private Integer dispatchState;

    /**
     * 任务状态
     * */
    @TableField(value = "TASKSTATE")
    private Integer taskState;

    /**
     * 获取时间
     * */
    @TableField(value = "ADDTIME")
    private Long addTime;

    //////////////////////////////////////查询字段///////////////////////////////////////////

    /**
     * 开始时间
     * */
    @TableField(exist = false)
    private Long beginTime;

    /**
     * 结束时间
     * */
    @TableField(exist = false)
    private Long endTime;
}
