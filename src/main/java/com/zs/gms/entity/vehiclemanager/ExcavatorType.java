package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
     * 类型名称
     * */
    @TableField(value = "EXCAVATORTYPENAME")
    @NotNull(message = "类型名称不能为空")
    private String excavatorTypeName;
    /**
     * 容量
     * */
    @TableField(value = "CAPACITY")
    @NotNull(message = "容量不能为空")
    private Float capacity;

    /**
     * 臂长
     * */
    @TableField(value = "BRANCHLENGTH")
    @NotNull(message = "臂长不能为空")
    private Float branchLength;

    @TableField(value = "CREATETIME")
    private Date createTime;

    @TableLogic
    @TableField(value = "ISDEL",select = false)
    @JsonIgnore
    private Integer isDel;
}
