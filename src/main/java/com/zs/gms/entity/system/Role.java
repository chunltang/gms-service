package com.zs.gms.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@TableName(value = "sys_role")
@ApiModel
@JsonIgnoreProperties(ignoreUnknown = true)
public class Role implements Serializable {

    private static final long serialVersionUID = -9081252068080362839L;

    public static final String NORMAL_ROLE="normal";


    /**
     * 角色ID
     * */
    //@ApiModelProperty(hidden = true)
    @TableId(value = "ROLEID",type = IdType.AUTO)
    private Integer roleId;

    /**
     * 角色名称
     * */
    @TableField(value = "ROLENAME",exist = true)
    @Size(max = 10,message = "{noMoreThan}")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色英文标识
     * */
    @TableField(value = "ROLESIGN",exist = true)
    private String roleSign;

    /**
     * 角色描述
     * */
    @TableField(value = "ROLEDESC")
    @Size(max = 500,message = "角色描述长度不能超过500")
    private String roleDesc;

    @TableField(exist =false)
    private String menuIds;

    @TableField(exist =false)
    private String menuNames;

    @JsonIgnore
    public static RoleSign getEnum(String value){
        for (RoleSign sign : RoleSign.values()) {
            if(sign.getValue().equals(value)){
                return sign;
            }
        }
        return null;
    }

    /**
     * 系统角色标识
     * */
    public enum RoleSign{

        ADMIN_ROLE("admin","系统调试员","X"),
        NORMAL_ROLE("normal","普通用户","N"),
        CHIEFDESPATCHER_ROLE("chiefDespatcher","调度长","B"),
        DESPATCHER_ROLE("despatcher","调度员","C"),
        MAPMAKER_ROLE("mapmaker","地图编辑员","D"),
        EXCAVATORPERSON_ROLE("excavatorPerson","挖掘机操作员","E"),
        DBULLDOZERPERSON_ROLE("bulldozerPerson","推土机操作员","U"),
        MANAGER_ROLE("manager","系统管理员","A");

        private String value;

        private String desc;

        /**
         * 角色表示简写
         * */
        private String sign;

        RoleSign(String value, String desc,String sign){
            this.value=value;
            this.desc=desc;
            this.sign=sign;
        }

        public String getValue(){
            return value;
        }

        public String getSign(){
            return this.sign;
        }

        public String getDesc(){
            return this.desc;
        }
    }
}
