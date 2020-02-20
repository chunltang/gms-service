package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户操作日志
 * */
@Data
@TableName(value = "sys_user_log")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysLog implements Serializable {
    private static final long serialVersionUID = -7714742102425749788L;

    /**
     * 日志id
     * */
    @TableId(value = "LOGID",type = IdType.AUTO)
    private Integer logId;

    /**
     * 操作用户id
     * */
    @TableField(value = "USERID")
    private Integer userId;

    /**
     * 操作用户名称
     * */
    @TableField(value = "USERNAME")
    private String userName;

    /**
     * 登录ip
     * */
    @TableField(value = "IP")
    private String ip;

    /**
     * 执行结果状态码
     * */
    @TableField(value = "CODE")
    private String code;

    /**
     * 执行结果描述
     * */
    @TableField(value = "RESULTDESC")
    private String resultDesc;

    /**
     * 操作耗时
     * */
    @TableField(value = "ELAPSEDTIME")
    public Long elapsedTime;

    /**
     * 操作时间
     * */
    @TableField(value = "OPERATETIME")
    private Date operateTime;

    /**
     * 操作描述
     * */
    @TableField(value = "OPERATEDESC")
    private String operateDesc;

    //////////////////////////////////////以下查询参数//////////////////////////////////////

    /**
     * 开始时间
     * */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date beginTime;

    /**
     * 结束时间
     * */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date endTime;
}
