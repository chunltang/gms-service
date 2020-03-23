package com.zs.gms.entity.terminalmanager;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_gps")
public class Gps implements Serializable {

    @TableId(value = "GPSID",type = IdType.AUTO)
    private Integer gpsId;

    /**
     * 编号
     * */
    @TableField(value = "GPSNO")
    @NotNull(message = "GPS编号不能为空")
    private Integer gpsNo;

    /**
     * ip
     * */
    @TableField(value = "IP")
    @NotNull(message = "IP不能为空")
    private String ip;

    /**
     * 创建时间
     * */
    @TableField(value = "CREATETIME")
    private Date createTime;

    /**
     * 添加用户
     * */
    @TableField(value = "USERID")
    private Integer userId;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;
}
