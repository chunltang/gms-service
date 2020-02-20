package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.enums.system.MenuTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@TableName(value = "sys_menu")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Menu implements Serializable {

    private static final long serialVersionUID = -2870406723784032391L;

    /**
     * 菜单ID
     * */
    @TableId(value = "MENUID",type = IdType.AUTO)
    private Integer menuId;

    /**
     * 父菜单ID
     * */
    @TableField(value = "PARENTID")
    private Long parentId;//上级菜单id

    /**
     * 菜单名称
     * */
    @TableField(value = "POWERLABEL")
    @NotBlank(message = "权限名称不能为空")
    private String powerLabel;

    /**
     * 菜单url
     * */
    @TableField(value = "URL")
    private String url;//菜单url

    /**
     * 权限标识
     * */
    @TableField(value = "POWERNAME")
    @NotBlank(message = "权限id不能为空")
    private String powerName;

    /**
     * 图标
     * */
    @TableField(value = "ICON")
    private String icon;

    /**
     * 类型：0 页面，1 功能
     * */
    @TableField(value = "POWERTYPE")
    @NotNull(message = "权限类型不能为空")
    private MenuTypeEnum powerType;

    /**
     * 排序ID
     * */
    @TableField(value = "ORDERNUM")
    private Integer orderNum=1;
}
