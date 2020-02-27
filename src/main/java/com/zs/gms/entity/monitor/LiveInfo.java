package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import com.zs.gms.enums.monitor.ModeStateEnum;
import com.zs.gms.enums.monitor.TaskStateEnum;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "t_liveInfo")
public class LiveInfo implements Serializable {

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
     * 是否运行
     * */
    @TableField(value = "RUNFLAG")
    private String runFlag;

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

}
