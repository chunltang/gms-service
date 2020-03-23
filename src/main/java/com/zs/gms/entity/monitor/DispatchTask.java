package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;
import com.zs.gms.enums.monitor.TaskTypeEnum;
import com.zs.gms.enums.monitor.UnitTypeEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度单元,交互式调度单元由系统初始化时根据调度员id创建
 * */
@Data
@TableName(value = "t_dispatch_task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DispatchTask implements Serializable {
    private static final long serialVersionUID = -7810281330146044388L;

    /**
     * 调度单元id
     * */
    @TableId(value = "UNITID",type = IdType.AUTO)
    private Integer unitId;

    /**
     * 地图id
     * */
    @TableField(value = "MAPID")
    private Integer mapId;

    /**
     * 调度单元名称
     * */
    @TableField(value = "NAME")
    private String name;

    /**
     * 调度任务类型
     * */
    @TableField(value = "DISPATCHTASKTYPE")
    private UnitTypeEnum dispatchTaskType;

    /**
     * 调度员id
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 单元状态:0移除，1使用中，2未使用，3停止
     * */
    @TableField(value = "STATUS")
    private Status status;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    private Date addTime;

    /*****************************************/

    /**
     * 特殊调度单元的类型
     * */
    @TableField(value = "TASKTYPE")
    private TaskTypeEnum taskType;

    /**
     * 特殊调度单元的任务区id
     * */
    @TableField(value = "TASKAREAID")
    private Integer taskAreaId;

    /*****************************************/
    /**
     * 装卸调度单元的装载点id
     * */
    @TableField(value = "LOADAREAID")
    private  Integer loadAreaId;

    /**
     * 装卸调度单元的卸载点id
     * */
    @TableField(value = "UNLOADAREAID")
    private Integer unLoadAreaId;

    public enum Status implements IEnum, Desc {

        //与调度统一枚举，不能单方面修改
        DELETE("0","调度单元已删除"),
        RUNING("1","调度单元正在运行..."),
        UNUSED("2","已创建"),
        STOP("3","调度单元已停止运行"),
        STOPING("4","正在停止调度单元...");

        private String value;

        private String desc;

        Status(String value, String desc){
            this.value=value;
            this.desc=desc;
        }

        public String getValue(){
            return value;
        }

        @JsonValue
        public String getDesc(){
            return desc;
        }

        public static DispatchTask.Status getEnumTypeByValue(String value){
            for (Status status : DispatchTask.Status.values()) {
                if(status.getValue().equals(value)){
                    return status;
                }
            }
            return null;
        }
    }
}
