package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 车辆配置信息
 * */
@Data
@TableName(value = "t_vehicle_type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BarneyType implements Serializable {
    private static final long serialVersionUID = 8661218069023856454L;

    @TableId(value = "VEHICLETYPEID",type=IdType.AUTO)
    private Long vehicleTypeId;

    /**
     * 车辆类型，0矿车，1挖掘机，2推土机，3加油车，4加水车，5其他
     * */
    @TableField(value = "VEHICLETYPE")
    @NotBlank(message = "车辆类型不能为空")
    private String vehicleType;

    /**
     * 车辆规格，小型矿车，中型矿车等
     * */
    @TableField(value = "VEHICLESPECIFICATION")
    @NotBlank(message = "车辆规格不能为空")
    private String vehicleSpecification;

    /**
     * 车辆图标
     * */
    @TableField(value = "VEHICLEICON")
    private String vehicleIcon;

    /**
     * 车辆尺寸
     * */
    @TableField(value = "VEHICLEWIDTH")
    private String vehicleWidth;

    @TableField(value = "VEHICLEHEIGHT")
    private String vehicleHeight;

    @TableField(value = "VEHICLELENGHT")
    private String vehicleLenght;

    /**
     * 吨位
     * */
    @TableField(value = "VEHICLETON")
    private  String vehicleTon;

    /**
     * 轴距
     * */
    @TableField(value = "VEHICLEWHEEL")
    private  String vehicleWheel;

    /**
     * 车轮转角
     * */
    @TableField(value = "VEHICLEANGLE")
    private  String vehicleAngle;

    /**
     * 限速
     * */
    @TableField(value = "LIMITSPEED")
    private String limitSpeed;

    /**
     * 最小转弯半径
     * */
    @TableField(value = "TURNRADIAL")
    private String turnRadial;

    /**
     * 计算中心width
     * */
    @TableField(value = "CENTERWIDTH")
    private String centerWidth;

    /**
     *计算中心length
     * */
    @TableField(value = "CENTERLENGHT")
    private String centerLenght;

    /**
     *计算中心height
     * */
    @TableField(value = "CENTERHEIGHT")
    private String centerHeight;
}
