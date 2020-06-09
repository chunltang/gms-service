package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.common.entity.WhetherEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_excavator_type")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExcavatorType implements Serializable {

    /**
     * 挖掘机类型id
     * */
    @TableId(value = "EXCAVATORTYPEID",type = IdType.AUTO)
    private Integer excavatorTypeId;

    /**
     * 类型名称2-50
     * */
    @TableField(value = "EXCAVATORTYPENAME")
    @NotNull(message = "类型名称不能为空")
    private String excavatorTypeName;

    /**
     * 厂家2-50
     * */
    @TableField(value = "MANUFACTURERS")
    private String manufacturers;

    /**
     * 额定斗容2-100立方米
     * */
    @TableField(value = "RATEDBUCKETVOLUME")
    @NotNull(message = "斗容不能为空")
    private Float ratedBucketVolume;

    /**
     * 最大卸载半径2-30
     * */
    @TableField(value = "MAXDUMPINGRADIU")
    private Float maxDumpingRadius;

    /**
     * 最大半径时的卸载高度2-15
     * */
    @TableField(value = "UNLOADINGHEIGHTATMAXRADIUS")
    private Float unloadingHeightAtMaxRadius;

    /**
     * 最大卸载高度2-15
     * */
    @TableField(value = "MAXIMUMUNLOADINGHEIGHT")
    private Float maximumUnloadingHeight;

    /**
     * 最大高度时的卸载半径2-30
     * */
    @TableField(value = "UNLOADINGRADIUSATMAXHEIGHT")
    private Float unloadingRadiusAtMaxHeight;

    /**
     * 尾部回旋半径2-10
     * */
    @TableField(value = "TRAILINGCYCLOTRONRADIUS")
    private Float trailingCyclotronRadius;

    /**
     * 斗杆有效长度2-30
     * */
    @TableField(value = "ARMEFFECTIVELYLENGTH")
    private Float armEffectivelyLength;

    /**
     * 大臂长度2-30
     * */
    @TableField(value = "ARMLENGTH")
    private Float armLength;

    /**
     * 大臂倾角30-75
     * */
    @TableField(value = "LARGEARMANGLE")
    private Float largeArmAngle;

    /**
     * 大臂天轮回转半径2-30
     * */
    @TableField(value = "TURNINGRADIUSWHEEL")
    private Float turningRadiusWheel;

    @TableField(value = "CREATETIME")
    private Date createTime;

    /**
     * 是否激活，0停用，1启用
     */
    @TableField(value = "ACTIVE")
    private WhetherEnum active;

    @TableLogic
    @TableField(value = "ISDEL",select = false)
    @JsonIgnore
    private Integer isDel;
}
