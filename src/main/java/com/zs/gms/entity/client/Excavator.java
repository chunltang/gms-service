package com.zs.gms.entity.client;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_excavator")
public class Excavator implements Serializable {

    /**
     * id
     * */
    @TableId(value = "EXCAVATORID",type = IdType.AUTO)
    private Integer excavatorId;

    /**
     * 电铲编号
     * */
    @TableField(value = "EXCAVATORNO")
    @NotNull(message = "电铲编号不能为空")
    private Integer excavatorNo;

    /**
     * ip1
     * */
    @TableField(value = "IP1")
    @NotBlank(message = "ip设置不能为空")
    private String ip1;

    /**
     * ip2
     * */
    @TableField(value = "IP2")
    @NotBlank(message = "ip设置不能为空")
    private String ip2;

    /**
     * 容量
     * */
    @TableField(value = "CAPACITY")
    @NotNull(message = "容量不能为空")
    private float capacity;

    /**
     * GPS安装位置
     * */
    @TableField(value = "X1")
    private float x1;

    @TableField(value = "Y1")
    private float y1;

    @TableField(value = "X2")
    private float x2;

    @TableField(value = "Y2")
    private float y2;

    /**
     * 臂长
     * */
    @TableField(value = "BRANCHLENGTH")
    private float branchLength;

    @TableField(value = "CREATETIME")
    private Date createTime;

    @TableLogic
    @TableField(value = "ISDEL",select = false)
    @JsonIgnore
    private Integer isDel;

    /**
     * 挖掘机用户
     * */
    @TableField(exist = false)
    private Integer userId;

    /**
     * 装载区id
     * */
    @TableField(exist = false)
    private Integer loadId;

}
