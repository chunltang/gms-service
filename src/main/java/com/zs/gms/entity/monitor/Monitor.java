package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.enums.monitor.TaskCodeEnum;
import com.zs.gms.enums.monitor.VakModeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "t_monitor")
public class Monitor implements Serializable {

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    /**
     * 关联id
     * */
    @TableField(value = "RELEVANCEID")
    private Integer relevanceId;

    /**
     * 报文产生设备编号,代表了当前报文所属设备
     * */
    @TableField(value = "MSGPRODDEVCODE")
    private Integer msgProdDevCode;

    /**
     * 矿用自卸车编号(1-9999)
     * */
    @TableField(value = "FROMVAKCODE")
    private Integer fromVakCode;

    @TableField(value = "YEAR")
    private Integer year;
    @TableField(value = "MONTH")
    private Integer month;
    @TableField(value = "DAY")
    private Integer day;
    @TableField(value = "HOUR")
    private Integer hour;
    @TableField(value = "MINUTE")
    private Integer minute;
    @TableField(value = "SECOND")
    private Float second;

    /**
     * 锁定设备编号,VAK当前所执行任务的来源设备
     * */
    @TableField(value = "LOCKEDDEVICECODE")
    private Integer lockedDeviceCode;

    /**
     * 监控数据类型,1级还是2级
     * */
    @TableField(value = "MONITORDATATYPE")
    private Integer monitorDataType;

    /**
     * 车辆模式编号
     * */
    @TableField(value = "VAKMODE")
    private VakModeEnum vakMode;

    /**
     * 当前任务编号
     * */
    @TableField(value = "CURRENTTASKCODE")
    private TaskCodeEnum currentTaskCode;

    /**
     * 轨迹编号
     * */
    @TableField(value = "TRACKCODE")
    private Integer trackCode;

    /**
     * 车载请求编号
     * */
    @TableField(value = "VAKREQUESTCODE")
    private Integer vakRequestCode;

    /**
     * 车辆当前挡位
     * */
    @TableField(value = "CURRENTGEAR")
    private Integer currentGear;

    /**
     * GNSS状态
     * */
    @TableField(value = "GNSSSTATE")
    private Integer gnssState;

    /**
     * 经度
     * */
    @TableField(value = "LONGITUDE")
    private double longitude;

    /**
     * 纬度
     * */
    @TableField(value = "LATITUDE")
    private double latitude;

    /**
     * 大地坐标系x坐标 单位米
     * */
    @TableField(value = "XWORLD")
    private double xworld;

    /**
     * 大地坐标系y坐标 单位米
     * */
    @TableField(value = "YWORLD")
    private double yworld;

    /**
     * 局部坐标系x坐标 单位米
     * */
    @TableField(value = "XLOCALITY")
    private double xLocality;

    /**
     * 局部坐标系y坐标 单位米
     * */
    @TableField(value = "YLOCALITY")
    private double yLocality;

    /**
     * 横摆角  单位度
     * */
    @TableField(value = "YAWANGLE")
    private double yawAngle;

    /**
     * 航向角  单位度
     * */
    @TableField(value = "NAVANGLE")
    private double navAngle;

    /**
     * 前轮转向角  单位度
     * */
    @TableField(value = "WHEELANGLE")
    private double wheelAngle;

    /**
     * 车辆速度  单位度
     * */
    @TableField(value = "CURSPEED")
    private double curSpeed;

    /**
     * 车辆加速度  单位度
     * */
    @TableField(value = "ADDSPEED")
    private double addSpeed;

    /**
     * 故障数量
     * */
    @TableField(value = "COUNTOFOBSTACLE",exist = false)
    private Integer countofObstacle;

    /**
     * 故障结构体数组
     * */
    @TableField(value = "VECOBSTACLE",exist = false)
    private Obstacle[] vecObstacle={};

    /**
     * 实际方向盘转角  deg
     * */
    @TableField(value = "STEERANGLE")
    private double realSteerAngle;

    /**
     * 实际方向盘转速  deg/s
     * */
    @TableField(value = "STEERROTSPEED")
    private double realSteerRotSpeed;

    /**
     * 实际油门开度 %
     * */
    @TableField(value = "ACCELERATORRATE")
    private double realAcceleratorRate;

    /**
     * 液压制动器主缸实际制动压力比例	%
     * */
    @TableField(value = "HYDBRAKERATE")
    private double realHydBrakeRate;

    /**
     * 电磁涡流制动器实际激磁电流比例	%
     * */
    @TableField(value = "ELECTRICFLOWBRAKERATE")
    private double realElectricFlowBrakeRate;

    /**
     * 发动机状态
     * */
    @TableField(value = "MOTORSTATE")
    private Integer realMotorState;

    /**
     * 行车制动状态
     * */
    @TableField(value = "FORWARDBRAKESTATE")
    private Integer realForwardBrakeState;

    /**
     * 电缓制动状态
     * */
    @TableField(value = "ELECTRICBRAKESTATE")
    private Integer realElectricBrakeState;

    /**
     * 停车制动状态
     * */
    @TableField(value = "PARKINGBRAKESTATE")
    private Integer realParkingBrakeState;

    /**
     * 装载制动状态
     * */
    @TableField(value = "LOADBRAKESTATE")
    private Integer realLoadBrakeState;

    /**
     * 发动机转速
     * */
    @TableField(value = "ROTSPEED")
    private Integer realMotorRotSpeed;

    /**
     * 货舱状态
     * */
    @TableField(value = "REALHOUSELIFTRATE")
    private Integer realHouseLiftRate;

    /**
     * 左转向灯状态
     * */
    @TableField(value = "LEFTLIGHTSTATE")
    private Integer realTurnLeftlightState;

    /**
     * 右转向灯状态
     * */
    @TableField(value = "RIGHTLIGHTSTATE")
    private Integer realTurnRightlightState;

    /**
     * 近光灯状态
     * */
    @TableField(value = "LIGHTSTATE")
    private Integer realNearLightState;

    /**
     * 示廓灯状态
     * */
    @TableField(value = "RLIGHTSTATE")
    private Integer realContourLightState;

    /**
     * 刹车灯状态
     * */
    @TableField(value = "BRAKELIGHTSTATE")
    private Integer realBrakeLightState;

    /**
     * 紧急信号灯状态
     * */
    @TableField(value = "EMERGENCYLIGHTSTATE")
    private Integer realEmergencyLightState;
}
