package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "sys_user_role")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRole implements Serializable {

    private static final long serialVersionUID = -7083728788506094286L;

    @TableId(value = "ID",type = IdType.AUTO)
    private Integer id;

    @TableField(value = "USERID")
    private Integer userId;

    @TableField(value = "ROLEID")
    private Integer roleId;
}
