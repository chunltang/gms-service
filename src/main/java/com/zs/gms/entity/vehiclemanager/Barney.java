package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.common.entity.WhetherEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 车辆
 */
@Data
@TableName(value = "t_vehicle")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Barney implements Serializable {

    private static final long serialVersionUID = 1500977883215031608L;

    public static WhetherEnum DEFAULT_STATUS = WhetherEnum.NO;

    @TableId(value = "VEHICLEID", type = IdType.AUTO)
    private Integer vehicleId;
    /**
     * 车辆编号，唯一，如车牌
     */
    @TableField(value = "VEHICLENO")
    @NotNull(message = "车辆编号不能为空")
    private Integer vehicleNo;

    /**
     * 是否自动驾驶 1自动，0手动
     */
    @TableField(value = "SELFMOTION")
    private WhetherEnum selfMotion = WhetherEnum.YES;

    /**
     * 添加时间
     */
    @TableField(value = "ADDTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date addTime;

    /**
     * 车载系统ip
     */
    @TableField(value = "IP")
    private String ip;

    /**
     * 是否装载VAP
     */
    @TableField(value = "VAP")
    private WhetherEnum vap;

    /**
     * 车载系统port
     */
    @TableField(value = "PORT")
    private String port;

    /**
     * 入网时间
     */
    @TableField(value = "NETINTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date netInTime;

    /**
     * 车辆激活状态
     */
    @TableField(value = "VEHICLESTATUS")
    private WhetherEnum vehicleStatus;

    /**
     * 备注信息
     */
    @TableField(value = "REMARK")
    @Size(max = 200, message = "备注信息长度不能超过200")
    private String remark;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL", select = false)
    private Integer isDel;

    ////////////////////////////////以下为查询参数/////////////////////////////////////
    /**
     * 调度员id
     */
    @TableField(exist = false)
    private Integer userId;

    /**
     * 调度员名称
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 车辆类型参数id
     */
    @TableField(exist = false)
    @NotNull(message = "车辆类型不能为空")
    private Integer vehicleTypeId;

    /**
     * 车辆类型名称
     */
    @TableField(exist = false)
    private String vehicleTypeName;

    /**
     * 吨位
     */
    @TableField(exist = false)
    private String loadDignified;

    /**
     * 图标
     */
    @TableField(exist = false)
    private String vehicleIcon;
}
