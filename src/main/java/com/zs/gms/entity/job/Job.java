package com.zs.gms.entity.job;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_job")
public class Job implements Serializable {

    private static final long serialVersionUID = -7339745795611155965L;
    /**
     * 任务调度参数 key
     */
    public static final String JOB_PARAM_KEY = "JOB_PARAM_KEY";

    @TableId(value = "JOBID", type = IdType.AUTO)
    private Long jobId;

    @TableField("BEANNAME")
    @NotBlank(message = "{required}")
    @Size(max = 50, message = "{noMoreThan}")
    private String beanName;

    @TableField("METHODNAME")
    @NotBlank(message = "{required}")
    @Size(max = 50, message = "{noMoreThan}")
    private String methodName;

    @TableField("PARAMS")
    @Size(max = 50, message = "{noMoreThan}")
    private String params;

    @TableField("CRONEXPRESSION")
    @NotBlank(message = "{required}")
    private String cronExpression;

    @TableField("STATUS")
    private String status;

    @TableField("REMARK")
    @Size(max = 100, message = "{noMoreThan}")
    private String remark;

    @TableField("CREATETIME")
    private Date createTime;

    private transient String createTimeFrom;
    private transient String createTimeTo;

}
