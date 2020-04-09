package com.zs.gms.entity.monitor;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zs.gms.enums.messagebox.HandleStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 车辆上报异常
 * */
@Data
@TableName("t_vehicle_warn")
public class VehicleWarn implements Serializable {

    @TableId(value = "WARNID",type = IdType.AUTO)
    private Integer warnId;

    /**
     * 部件编号
     * */
    @TableField(value = "PARTNO")
    private Integer partNo;

    /**
     * 故障码
     * */
    @TableField(value = "WARNCODE")
    private Integer warnCode;

    /**
     * 上报车辆编号
     * */
    @TableField(value = "VEHICLEID")
    private Integer vehicleId;

    /**
     * 处理进度
     * */
    @TableField(value = "STATUS")
    private HandleStatus status;

    /**
     * 产生时间
     * */
    @TableField(value = "CREATETIME")
    private Date createTime;
}
