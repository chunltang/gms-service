package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.common.entity.WhetherEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_excavator")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Excavator implements Serializable {

    /**
     * id
     * */
    @TableId(value = "EXCAVATORID",type = IdType.AUTO)
    private Integer excavatorId;

    /**
     * 电铲类型id
     * */
    @NotNull(message = "电铲类型不能为空")
    @TableField(value = "EXCAVATORTYPEID")
    private Integer excavatorTypeId;

    /**
     * 电铲编号
     * */
    @TableField(value = "EXCAVATORNO")
    @NotNull(message = "电铲编号不能为空")
    private Integer excavatorNo;

    /**
     * 车辆状态，0停用，1启用(默认)
     */
    @TableField(value = "VEHICLESTATUS")
    private WhetherEnum vehicleStatus;

    /**
     * ip1
     * */
    @TableField(value = "IP1")
    @NotBlank(message = "ip设置不能为空")
    private String ip1;

    /**
     * ip2
     * */
    /*@TableField(value = "IP2")
    @NotBlank(message = "ip设置不能为空")
    private String ip2;*/

    /**
     * 是否装载VAP
     */
    @TableField(value = "VAP")
    private WhetherEnum vap;


    /**
     * GPS安装位置
     * */
    @TableField(value = "X1")
    private Float x1;

    @TableField(value = "Y1")
    private Float y1;

    @TableField(value = "PORT")
    private String port;

    /*@TableField(value = "X2")
    private Float x2;

    @TableField(value = "Y2")
    private Float y2;*/

    @TableField(value = "CREATETIME")
    private Date createTime;

    @TableField(value = "REMARK")
    private String remark;

    @TableLogic
    @TableField(value = "ISDEL",select = false)
    @JsonIgnore
    private Integer isDel;

    /**
     * 挖掘机用户
     * */
    @TableField(exist = false)
    private Integer userId;

    @TableField(exist = false)
    private String userName;

    /**
     * 装载区id
     * */
    @TableField(exist = false)
    private Integer loadId;

    @TableField(exist = false)
    private String loadName;

    @TableField(exist = false)
    private Integer mapId;

    /**
     * 容量
     * */
    @TableField(exist = false)
    private Float ratedBucketVolume;

    /**
     * 类型名称
     * */
    @TableField(exist = false)
    private String excavatorTypeName;
}
