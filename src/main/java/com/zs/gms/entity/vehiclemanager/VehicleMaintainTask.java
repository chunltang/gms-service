package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zs.gms.common.interfaces.Desc;
import com.zs.gms.enums.vehiclemanager.DateEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * 车辆维护任务
 * */
@Data
@TableName("t_maintain_task")
public class VehicleMaintainTask implements Serializable {

    public final static String TASK_PREFIX="maintain_task_";

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    /**
     * 矿车编号
     * */
    @TableField(value = "VEHICLEID")
    @NotNull
    private Integer vehicleId;

    /**
     * 维护任务名称
     * */
    @TableField(value = "MAINTAINTASKNAME")
    private String maintainTaskName;


    /**
     * 数量，几个小时，几天
     * */
    @TableField(value = "NUM")
    @NotNull
    private Integer num;

    /**
     * 数量单位
     * */
    @TableField(value = "UNITS")
    @NotNull
    private DateEnum units;

    /**
     * 下次执行时间
     * */
    @TableField(value = "NEXTTIME")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date nextTime;

    @TableField(value = "STATUS")
    private Status status;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    private Date addTime;

    /**
     * 添加用户id
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 添加用户名
     * */
    @TableField(value = "USERNAME")
    private String userName;

    @TableLogic
    @TableField(value = "ISDEL",select = false)
    @JsonIgnore
    private Integer isDel;

    public enum Status implements IEnum, Desc {

        UNDISPOSED("0","未处理"),
        PROCESSING("1","处理中"),
        PROCESSED("2","已处理");

        private String value;

        private String desc;

        Status(String value,String desc){
            this.value=value;
            this.desc=desc;
        }

        @Override
        public String getValue(){
            return this.value;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }
    }
}
