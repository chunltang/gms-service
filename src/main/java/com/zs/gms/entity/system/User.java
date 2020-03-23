package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


@Data
@TableName(value = "sys_user")
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private static  long serialVersionUID = -8627815897867748563L;

    public static  String THEAM_WHITE="white";

    public static  String DEFAULT_ICON="";

    public static String DEFAULT_PWD="123456";

    /**
     * 加盐字符串
     * */
    public static final String DEAFULT_PASSWORD="1234qwer";

    /**
     * 用户id
     * */
    //@ApiModelProperty(hidden = true)
    @TableId(value = "USERID",type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名称
     * */
    @TableField(value = "USERNAME")
    @Size(min = 6,max = 16,message = "用户名称长度在6-16之间")
    private String userName;

    /**
     * 密码
     * */
    @TableField(value = "PASSWORD")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 主题
     * */
    @TableField("THEME")
    private String theme;

    /**
     * 头像
     * */
    @TableField("AVATAR")
    private String avatar;

    /**
     * 角色id
     * */
    @TableField(exist = false)
    private String roleId;

    /**
     * 角色名称
     * */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色英文标识
     * */
    @TableField(exist = false)
    private String roleSign;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL",select = false)
    private Integer isDel;
}
