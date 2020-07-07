package com.zs.gms.entity.messagebox;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApproveProcess implements Serializable {

    /**
     * 审批用户
     */
    private String userId;

    private String userName;

    private String roleName;

    /**
     * 审批结果
     */
    private Approve.Status status;

    /**
     * 审批时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date approveTime;

    /**
     * 审批意见
     */
    private String suggestion;
}
