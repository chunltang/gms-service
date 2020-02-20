package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 车辆
 * */
@Data
@TableName(value = "t_vehicle")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vehicle  implements Serializable {

    private static final long serialVersionUID = 1500977883215031608L;

    public  static  String DEFAULT_STATUS="1";

    @TableId(value = "VEHICLEID",type = IdType.AUTO)
    private Integer vehicleId;
    /**
     * 车辆编号，唯一，如车牌
     * */
    @TableField(value = "VEHICLENO")
    @Max(value = 5l,message = "车辆编号在10000~19999之间")
    private Integer vehicleNo;

    /**
     * 是否自动驾驶 0自动，1手动
     * */
    @TableField(value = "SELFMOTION")
    @NotBlank(message = "是否自动驾驶不能为空")
    private String selfMotion;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    private Date addTime;

    /**
     * 车载系统ip
     * */
    @TableField(value = "IP")
    private String ip;

    /**
     * 车载系统port
     * */
    @TableField(value = "PORT")
    private String port;

    /**
     * 入网时间
     * */
    @TableField(value = "NETINTIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date netInTime;

    /**
     * 车辆状态，0停用，1启用(默认)
     * */
    @TableField(value = "VEHICLESTATUS")
    private String vehicleStatus;

    /**
     * 备注信息
     * */
    @TableField(value = "REMARK")
    @Size(max = 200,message = "备注信息长度不能超过200")
    private String remark;

    ////////////////////////////////以下为查询参数/////////////////////////////////////
    /**
     * 调度员id
     * */
    @TableField(exist = false)
    private Integer userId;

    /**
     * 调度员名称
     * */
    @TableField(exist = false)
    private String userName;

    /**
     * 车辆类型参数id
     * */
    @TableField(exist = false)
    @NotNull(message = "车辆类型不能为空")
    private Integer vehicleTypeId;

    /**
     * 车辆类型，0矿车，1电铲，2推土机，3加油车，4加水车，5其他
     * */
    @TableField(exist = false)
    private String vehicleType;

    /**
     * 车辆规格，小型矿车，中型矿车，电铲等
     * */
    @TableField(exist = false)
    private String vehicleSpecification;

    /**
     * 吨位
     * */
    @TableField(exist = false)
    private  String vehicleTon;

    /**
     * 图标
     * */
    @TableField(exist = false)
    private String vehicleIcon;
}
