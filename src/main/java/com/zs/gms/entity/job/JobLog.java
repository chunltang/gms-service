package com.zs.gms.entity.job;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("t_job_log")
public class JobLog implements Serializable {

    private static final long serialVersionUID = -7114915445674333148L;
    // 任务执行成功
    public static final String JOB_SUCCESS = "0";
    // 任务执行失败
    public static final String JOB_FAIL = "1";

    @TableId(value = "LOGID", type = IdType.AUTO)
    private Long logId;

    @TableField("JOBID")
    private Long jobId;

    @TableField("BEANNAME")
    private String beanName;

    @TableField("METHODNAME")
    private String methodName;

    @TableField("PARAMS")
    private String params;

    @TableField("STATUS")
    private String status;

    @TableField("ERROR")
    private String error;

    @TableField("TIMES")
    private Long times;

    @TableField("CREATETIME")
    private Date createTime;

    private transient String createTimeFrom;
    private transient String createTimeTo;

}
