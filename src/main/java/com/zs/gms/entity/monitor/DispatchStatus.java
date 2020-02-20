package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "t_dispatchStatus")
public class DispatchStatus implements Serializable {

    @TableId(value = "ID",type = IdType.AUTO)
    public Integer id;
    /**
     * 创建时间
     * */
    @TableField(value = "CREATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date createTime;

    /**
     * 车辆用户
     * */
    @TableField(value = "USERID")
    public Integer userId;

    /**
     * 车辆编号
     * */
    @TableField(value = "VEHICLEID")
    public Integer vehicleId;

    @TableField(value = "STATUS")
    public DispatchStateEnum status;
}
