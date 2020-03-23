package com.zs.gms.entity.mineralmanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 卸载区和矿物对应表
 * */
@Data
@TableName(value = "t_area_mineral")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaMineral implements Serializable {
    private static final long serialVersionUID = -5552226581756462278L;

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    /**
     * 卸载区id
     * */
    @TableField(value = "AREAID")
    private Integer areaId;

    /**
     * 矿物id
     * */
    @TableField(value = "MINERALID")
    private Integer mineralId;

    /**
     * 矿物名称
     * */
    @TableField(value = "MINERALNAME")
    private String mineralName;

    /**
     * 添加时间
     * */
    @TableField(value = "ADDTIME")
    private Date addTime;

    /**
     * 添加用户
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 使用状态：0正在使用，1已变更
     * */
    @TableField(value = "STATUS")
    public String status;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;

    public enum Status{

        USING("0","正在使用"),
        CHANGED("1","已变更");

        private String value;

        private String desc;

        Status(String value, String desc){
            this.value=value;
            this.desc=desc;
        }

        public String getValue(){
            return value;
        }
    }
}
