package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.interfaces.Desc;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_unit")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Unit implements Serializable {

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
    @NotNull(message = "调度单元名称不能为空")
    @TableField(value = "UNITNAME")
    private String unitName;

    /**
     * 创建人id
     * */
    @TableField(value = "CREATEUSERID")
    private Integer createUserId;

    /**
     * 调度员id
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 装卸调度单元的装载区id
     * */
    @NotNull(message = "装载区不能为空")
    @TableField(value = "LOADAREAID")
    private  Integer loadAreaId;

    /**
     * 装卸调度单元的卸载区id
     * */
    @NotNull(message = "卸载区不能为空")
    @TableField(value = "UNLOADAREAID")
    private Integer unLoadAreaId;

    /**
     * 循环次数
     * */
    @TableField(value = "CYCLETIMES")
    private Integer cycleTimes;

    /**
     * 设置装卸任务结束时间
     * */
    @TableField(value = "ENDTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    /**
     * 单元状态:0移除，1使用中，2未使用，3停止
     * */
    @TableField(value = "STATUS")
    private Status status;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date addTime;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;

    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private String name;

    @TableField(exist = false)
    private Integer excavatorId;



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

        public static Unit.Status getEnumTypeByValue(String value){
            for (Status status : Unit.Status.values()) {
                if(status.getValue().equals(value)){
                    return status;
                }
            }
            return null;
        }
    }
}
