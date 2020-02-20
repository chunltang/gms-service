package com.zs.gms.entity.mineralmanager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 矿物类型
 * */
@Data
@TableName(value = "t_mineral")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mineral implements Serializable {

    private static final long serialVersionUID = -4723327999762715396L;

    /**
     * 矿物id
     * */
    @TableId(value = "MINERALID",type = IdType.AUTO)
    private Long mineralId;

    /**
     * 矿物名称
     * */
    @TableField(value = "MINERALNAME")
    @NotBlank(message = "矿物名称不能为空")
    private String mineralName;

    /**
     * 备注
     * */
    @TableField(value = "REMARK")
    private String remark;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    private Date addTime;



}
