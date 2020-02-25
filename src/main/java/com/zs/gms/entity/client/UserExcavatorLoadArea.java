package com.zs.gms.entity.client;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 挖掘机，用户，装载区的关系
 * */
@Data
@TableName(value = "t_bind_excavator")
public class UserExcavatorLoadArea  implements Serializable {

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "MAPID")
    @NotNull(message = "地图不能为空")
    private Integer mapId;

    @TableField(value = "USERID")
    @NotNull(message = "用户不能为空")
    private Integer userId;

    @TableField(value = "LOADAREA")
    @NotNull(message = "装载区不能为空")
    private Integer loadArea;

    @TableField(value = "EXCAVATORID")
    @NotNull(message = "挖掘机不能为空")
    private Integer excavatorId;

    @TableField(value = "CREATETIME")
    private Date createTime;
}
