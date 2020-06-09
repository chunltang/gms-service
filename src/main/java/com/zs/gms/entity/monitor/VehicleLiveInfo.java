package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import com.zs.gms.enums.monitor.ModeStateEnum;
import com.zs.gms.enums.monitor.TaskStateEnum;
import com.zs.gms.enums.monitor.TaskTypeEnum;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "t_liveInfo")
public class VehicleLiveInfo extends LiveInfo implements Serializable {

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    /**
     *  车辆状态
     * */
    @TableField(value = "MODESTATE")
    private ModeStateEnum modeState;

    /**
     * 调度状态
     * */
    @TableField(value = "DISPSTATE")
    private DispatchStateEnum dispState;

    /**
     * 任务状态
     * */
    @TableField(value = "TASKSTATE")
    //@JsonDeserialize(using = IEnumDescSerializer.class)
    private TaskStateEnum taskState;

    /**
     * 当前车辆所在调度单元
     * */
    @TableField(value = "UNITID")
    private Integer unitId=0;

    /**
     * 当前任务的目标区域
     * */
    @TableField(value = "TASKAREAID")
    private Integer taskAreaId=0;

    /**
     * 当前任务类型
     * */
    @TableField(value = "TASKTYPE")
    private TaskTypeEnum taskType;


    /**
     * 当前任务的任务点
     * */
    @TableField(value = "TASKSPOTID")
    private Integer  taskSpotId=0;

    /**
     * 是否运行
     * */
    @TableField(value = "RUNFLAG")
    private String runFlag;

    /**
     * 是否遇到障碍物
     * */
    @TableField(value = "OBSFLAG")
    private Boolean obsFlag;


    //路径最近点id
    private Integer nowPathId;
    //当前点到起点距离
    private Double nowDistance;
    //路径终点到起点距离
    private Double endDistance;

    /**
     * 调度员id
     * */
    private Integer userId;

    /**
     * 调度员姓名
     * */
    private String userName;//12354566778_0  map000001_0

    /**
     * 产生时间
     * */
    @TableField(value = "UPDATETIME")
    private Date updateTime;

    @JsonFormat(pattern = "yyyyMMddHHmmss",timezone = "GMT+8")
    public void setUpdateTime(Date time){
        this.updateTime=time;
    }


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    public Date getUpdateTime(){
       return updateTime;
    }

    /**
     * 监控数据
     * */
    @TableField(value = "MONITOR",exist = false)
    private Monitor monitor;


    @Override
    @JsonIgnore
    public Type getType() {
        return Type.VEHICLE;
    }
}
