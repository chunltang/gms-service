package com.zs.gms.entity.client;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
     * ip
     * */
    @TableField(value = "IP")
    @NotBlank(message = "ip设置不能为空")
    private String ip;

    /**
     * 容量
     * */
    @TableField(value = "CAPACITY")
    @NotNull(message = "容量不能为空")
    private float capacity;

    /**
     * GPS安装位置
     * */
    @TableField(value = "X")
    private float x;

    @TableField(value = "Y")
    private float y;

    /**
     * 臂长
     * */
    @TableField(value = "BRANCHLENGTH")
    private float branchLength;

    @TableField(value = "CREATETIME")
    private Date createTime;
}
