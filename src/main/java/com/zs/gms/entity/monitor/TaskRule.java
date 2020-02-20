package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 调度任务规则
 * */
@Data
@TableName(value = "t_task_rule")
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskRule implements Serializable {
    private static final long serialVersionUID = -5812073764325353859L;

    /**
     * 任务规则id
     * */
    @TableId(value = "RULEID",type = IdType.AUTO)
    private Integer ruleId;

    /**
     * 调度单元id
     * */
    @TableField(value = "UNITID")
    private Integer unitId;

    /**
     * 调度员id
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 车辆编号
     * */
    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    /**
     * 循环次数
     * */
    @TableField(value = "CYCLETIMES")
    private Integer cycleTimes;

    /**
     * 设置装卸任务结束时间
     * */
    @TableField(value = "ENDTIME")
    private String endTime;

    /**
     * 调度装卸任务下发时间
     * */
    @TableField(value = "ADDTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;

    /**
     * 交互式任务的点集
     * */
    @TableField(value = "POINTS")
    private String points;

    /**
     * 完成状态，0进行中，1完成，2停止，3移除,4未执行
     * */
    @TableField(value = "STATUS")
    private Status status;

    public enum Status implements IEnum {

        RUNING("0","进行中"),
        FINISH("1","完成"),
        STOP("2","停止"),
        DELETE("3","移除"),
        UNEXECUTED("4","未执行");

        private String value;

        private String desc;

        Status(String value, String desc){
            this.value=value;
            this.desc=desc;
        }

        @Override
        public String getValue(){
            return value;
        }

        @JsonValue
        public String getDesc(){
            return desc;
        }
    }
}
