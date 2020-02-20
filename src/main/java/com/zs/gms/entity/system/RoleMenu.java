package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "sys_role_menu")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = -2625741875502426968L;

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "ROLEID")
    private Integer roleId;

    @TableField(value = "MENUID")
    private Integer menuId;
}
