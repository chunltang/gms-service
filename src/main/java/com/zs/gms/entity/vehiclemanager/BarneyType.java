package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.common.entity.WhetherEnum;
import lombok.Data;

import javax.validation.constraints.*;
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
     * 车辆类型名称,2-50
     * */
    @TableField(value = "VEHICLETYPENAME")
    @NotBlank(message = "车辆类型名称不能为空")
    private String vehicleTypeName;

    /**
     * 厂家2-50
     * */
    @TableField(value = "MANUFACTURERS")
    @NotBlank(message = "生产厂家不能为空")
    private String manufacturers;

    /**
     * 自重20-500
     * */
    @TableField(value = "SELFDIGNIFIED")
    private  Double selfDignified;

    /**
     * 载重50-500
     * */
    @TableField(value = "LOADDIGNIFIED")
    @Max(value = 500,message = "载重不能超过500t")
    @Min(value = 50,message = "载重不能小于50t")
    private  Double loadDignified;

    /**
     * 总重70-1000
     * */
    @TableField(value = "TOTALDIGNIFIED")
    private  Double   totalDignified;

    /**
     * 装载高度5-15
     */
    @TableField(value = "LOADHEIGHT")
    @Max(value = 15,message = "装载高度不能超过15m")
    @Min(value = 5,message = "装载高度不能小于5m")
    private Double loadHeight;


    /**
     * 车辆尺寸,宽度4-10
     * */
    @TableField(value = "VEHICLEWIDTH")
    @NotNull(message = "车宽度不能为空")
    @Max(value = 10,message = "车辆宽度不能超过10m")
    @Min(value = 4,message = "车辆宽度不能小于4m")
    private Double vehicleWidth;

    /**
     * 车辆尺寸,高度4-10
     * */
    @TableField(value = "VEHICLEHEIGHT")
    private Double vehicleHeight;

    /**
     * 车辆尺寸,长度6-15
     * */
    @TableField(value = "VEHICLELENGHT")
    @NotNull(message = "车长度不能为空")
    @Max(value = 15,message = "车辆长度不能超过15m")
    @Min(value = 6,message = "车辆长度不能小于6m")
    private Double vehicleLenght;

    /**
     * 后轮边缘到车尾的距离0.5-3
     * */
    @TableField(value = "VEHICLETAILWHEEL")
    @NotNull(message = "后轮边缘到车尾的距离不能为空")
    @Max(value = 3,message = "后轮边缘到车尾的距离不能超过3m")
    @Min(value = 0,message = "后轮边缘到车尾的距离不能小于0m")
    private  Double vehicleTailWheel;

    /**
     * 后轴到车尾的距离1-3
     * */
    @TableField(value = "VEHICLETAILAXLE")
    @NotNull(message = "后轴到车尾的距离不能为空")
    @Max(value = 5,message = "后轴到车尾的距离不能超过5m")
    @Min(value = 1,message = "后轴到车尾的距离不能小于1m")
    private  Double vehicleTailAxle;

    /**
     * 轴距2-10
     * */
    @TableField(value = "VEHICLEWHEEL")
    private  Double vehicleWheel;

    /**
     * 前轮距3-9
     * */
    @TableField(value = "FRONTGAUGE")
    private  Double frontGauge;

    /**
     * 后轮距3-9
     * */
    @TableField(value = "TRACKREAR")
    private  Double trackRear;

    /**
     * 最小离地间隙0.4-1.5
     * */
    @TableField(value = "MINGAP")
    private  Double minGap;

    /**
     * 轮胎尺寸，直径2-8
     * */
    @TableField(value = "TIRESIZE")
    private  Double tireSize;

    /**
     * 最小转弯半径6-20
     * */
    @TableField(value = "MINTURNINGRADIUS")
    private  Double minTurningRadius;

    /**
     * 限速20-80
     * */
    @TableField(value = "LIMITSPEED")
    @Max(value = 80,message = "限速不能超过80km/h")
    @Min(value = 20,message = "限速不能小于20km/h")
    private Double limitSpeed;

    /**
     * 车厢举升时间10-60
     * */
    @TableField(value = "BUCKETTIM")
    private Integer bucketTim;

    /**
     * 车厢降落时间10-60
     * */
    @TableField(value = "FALLTIME")
    private Integer fallTime;

    /**
     * 机油容量5-1000
     * */
    @TableField(value = "OLICAPACITY")
    private Double oliCapacity;

    /**
     * 轮边减速器油箱容量5-1000
     * */
    @TableField(value = "REDUCERTANKCAPACITY")
    private Double reducerTankCapacity;

    /**
     * 燃油箱容量100-10000
     * */
    @TableField(value = "FUELCAPACITY")
    private Double fuelCapacity;

    /**
     * 液压油容量20-2000
     * */
    @TableField(value = "HYDRAULICTANCAPACITY")
    private Double hydraulicTanCapacity;

    /**
     * 冷却水箱容量10-1000
     * */
    @TableField(value = "COOLANTTANKCAPACITY")
    private Double coolantTankCapacity;

    /**
     * 车辆图标
     * */
    @TableField(value = "VEHICLEICON")
    private String vehicleIcon;

    /**
     * 是否激活，0停用，1启用
     */
    @TableField(value = "ACTIVE")
    private WhetherEnum active;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;
}
