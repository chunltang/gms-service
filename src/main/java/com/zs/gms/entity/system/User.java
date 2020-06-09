package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.common.handler.BoolConverterHandler;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;


@Data
@TableName(value = "sys_user", autoResultMap = true)
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    private static long serialVersionUID = -8627815897867748563L;

    public static String THEAM_WHITE = "white";

    public static String DEFAULT_ICON = "";

    public static String DEFAULT_PWD = "123456";

    /**
     * 加盐字符串
     */
    public static final String DEAFULT_PASSWORD = "1234qwer";

    /**
     * 密码重试次数
     */
    public static final int MAX_PWD_RETRY = 5;

    /**
     * 用户id
     */
    //@ApiModelProperty(hidden = true)
    @TableId(value = "USERID")
    private Integer userId;

    /**
     * 用户编号，自动生成
     */
    @TableField(value = "USERNAME")
    private String userName;

    /**
     * 用户名称
     */
    @TableField(value = "NAME")
    @Size(min = 1, max = 16, message = "用户姓名长度在1-16之间")
    private String name;

    /**
     * 密码
     */
    @TableField(value = "PASSWORD")
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 联系电话
     */
    @TableField("PHONE")
    private String phone;

    /**
     * 主题
     */
    @TableField("THEME")
    private String theme;

    /**
     * 头像
     */
    @TableField("AVATAR")
    private String avatar;

    /**
     * 是否被锁定
     */
    @TableField(value = "USERLOCK", typeHandler = BoolConverterHandler.class)
    private boolean userLock;

    /**
     * 密码已重试次数
     */
    @JsonIgnore
    @TableField("RETRY")
    private Integer retry = 0;

    /**
     * 创建时间
     */
    @TableField("CREATETIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 最后登录时间
     */
    @TableField("LASTLOGINTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastLoginTime;

    /**
     * 角色id
     */
    @TableField(exist = false)
    @NotNull(message = "角色不能为空")
    private Integer roleId;

    /**
     * 角色名称
     */
    @TableField(exist = false)
    private String roleName;

    /**
     * 角色英文标识
     */
    @TableField(exist = false)
    private String roleSign;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL", select = false)
    private Integer isDel;

    @TableField(exist = false)
    private boolean activate = false;

    @TableField(exist = false)
    private String sessionId;
}
