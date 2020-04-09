package com.zs.gms.entity.messagebox;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.common.handler.ListTypeHandler;
import com.zs.gms.common.handler.MapTypeHandler;
import com.zs.gms.enums.messagebox.ApproveType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@TableName(value = "t_approve",autoResultMap = true)
public class Approve implements Serializable {

    @TableId(value = "APPROVEID",type = IdType.AUTO)
    private Integer approveId;

    /**
     * 提交对象
     * */
    @TableField(value ="SUBMITUSERID")
    @NotNull
    private Integer submitUserId;

    /**
     * 提交对象名称
     * */
    @TableField(value ="SUBMITUSERNAME")
    @NotNull
    private String submitUserName;

    /**
     * 审批对象
     * */
    @TableField(value ="APPROVEUSERIDS")
    @NotNull
    private String approveUserIds;

    /**
     * 处理结果标记,true为提交对象查看了数据，false为未处理，需要下次再推送
     * */
    @TableField(value ="APPROVEMARK")
    @NotNull
    private boolean approveMark;

    /**
     * 审批类型
     * */
    @TableField(value ="APPROVETYPE")
    @NotNull
    private ApproveType approveType;

    /**
     * 提交时间
     * */
    @TableField(value ="CREATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 审批完成时间
     * */
    @TableField(value ="APPROVETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /**
     * 审批状态
     * */
    @TableField(value ="STATUS")
    @NotNull
    private Status status;

    /**
     * 审批异常描述
     * */
    @TableField(value ="APPROVEERRORDESC")
    private String approveErrorDesc;

    /**
     * 审批提交的参数,json格式
     * */
    @TableField(value ="PARAMS",typeHandler = MapTypeHandler.class)
    private Map<String,Object> params;

    /**
     * 审批描述
     * */
    @TableField(value ="APPROVEDESC")
    @NotNull
    private String approveDesc;

    /**
     * 审批过程
     * */
    @TableField(value ="APPROVEPROCESS",typeHandler = ListTypeHandler.class)
    private List<ApproveProcess> approveProcess;

    /**
     * 审批规则：false只需要一个通过，true需要全部通过
     * */
    @TableField(value ="RULE")
    @NotNull
    private boolean rule;


    public  enum Status implements IEnum{

        DELETE("0","删除申请"),
        WAIT("1","等待审批"),
        APPROVEPASS("2","申请通过"),
        APPROVEREJECT("3","驳回申请"),
        APPROVEERROR("4","流程处理异常");

        private String value;

        private String desc;

        Status(String value, String desc){
            this.value=value;
            this.desc=desc;
        };

        @JsonValue
        public String getValue(){
            return value;
        }


        public String getDesc(){
            return desc;
        }
    }
}
