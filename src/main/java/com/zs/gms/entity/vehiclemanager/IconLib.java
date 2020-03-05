package com.zs.gms.entity.vehiclemanager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "t_iconlib")
public class IconLib implements Serializable {

    @TableId(value = "LIBID", type = IdType.AUTO)
    private Integer libId;

    @TableField("NAME")
    @NotBlank(message = "图标库名称不能为空")
    private String name;

    @TableField("REMARK")
    private String remark;

    /**
     * 相对路径
     */
    @TableField("PATH")
    private String path;

    @TableField("CREATETIME")
    private Date createTime;

    @TableField("USERNAME")
    private String userName;

    @TableField("USERID")
    private Integer userId;

    /**
     * 第一个图标的地址
     * */
    @TableField("FIRSTICONPATH")
    private String firstIconPath;

    @JsonIgnore
    @TableField("STATUS")
    private Status status=Status.ENABLE;


    public enum Status implements IEnum {

        ENABLE("1"),
        DISABLE("0");

        public String value;

        Status(String value) {
            this.value = value;
        }

        @Override
        public Serializable getValue() {
            return value;
        }
    }
}
