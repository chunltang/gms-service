package com.zs.gms.entity.messagebox;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;
import com.zs.gms.entity.mapmanager.Point;
import com.zs.gms.enums.messagebox.HandleStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_fault")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fault implements Serializable {

    private static final long serialVersionUID = -8521144826137309877L;

    /**
     * 故障id
     * */
    @TableId(value = "FAULTID",type = IdType.AUTO)
    private Integer faultId;

    /**
     * 故障等级
     * */
    @TableField(value = "LEVEL")
    private String level;

    /**
     * 车辆故障码
     * */
    @TableField(value = "FAULTCODE")
    private String faultCode;

    /**
     * 故障描述
     * */
    @TableField(value = "FAULTDESC")
    private String faultDesc;

    /**
     * 故障来源:0人为，1车辆上报
     * */
    @TableField(value = "SOURCETYPE")
    private String sourceType;

    /**
     * 故障状态:0未处理，1处理中，2已处理
     * */
    @TableField(value = "STATUS")
    private HandleStatus status;

    /**
     * 受理故障人员
     * */
    @TableField(value = "HANDLENAME")
    private String handleName;

    /**
     * 故障上报用户名或者是上报的车辆编号
     * */
    @TableField(value = "SOURCE")
    private String  source;

    /**
     * 故障产生时间
     * */
    @TableField(value = "CREATETIME")
    private Date createTime;

    /**
     * 故障处理时间
     * */
    @TableField(value = "HANDLETIME")
    private Date handleTime;

    /**
     * 故障位置
     * */
    @TableField(value = "POSITION")
    private Point position;

    /**
     * 故障半径
     * */
    @TableField(value = "RADIUS")
    private Float radius;

    /**
     * 故障备注信息
     * */
    @TableField(value = "REMARK")
    private String remark;
}
