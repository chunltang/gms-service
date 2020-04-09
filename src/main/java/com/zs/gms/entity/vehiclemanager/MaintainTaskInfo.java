package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Date;

/**
 * 维护信息
 * */
@TableName("t_maintain_task_info")
@Data
public class MaintainTaskInfo {

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    /**
     * 维护任务id
     * */
    @TableField(value = "MAINTAINTASKID")
    private Integer maintainTaskId;

    /**
     * 处理用户id
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 车辆id
     * */
    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    /**
     * 处理用户名
     * */
    @TableField(value = "USERNAME")
    private String userName;

    /**
     * 处理时间
     * */
    @TableField(value = "HANDLETIME")
    private Date handleTime;

    /**
     * 备注
     * */
    @TableField(value = "REMARK")
    private String remark;

    @TableLogic
    @TableField(value = "ISDEL",select = false)
    @JsonIgnore
    private Integer isDel;
}
