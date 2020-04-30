package com.zs.gms.entity.mineralmanager;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.*;
import com.zs.gms.common.interfaces.Desc;
import com.zs.gms.enums.vehiclemanager.ActivateStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 矿物类型
 */
@Data
@TableName(value = "t_mineral")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Mineral implements Serializable {

    private static final long serialVersionUID = -4723327999762715396L;

    /**
     * 矿物id
     */
    @TableId(value = "MINERALID", type = IdType.AUTO)
    private Integer mineralId;

    /**
     * 矿物名称
     */
    @TableField(value = "MINERALNAME")
    @NotBlank(message = "矿物名称不能为空")
    private String mineralName;

    /**
     * 品位
     */
    @TableField(value = "LEVEL")
    private Level level;

    /**
     * 是否激活
     */
    @TableField(value = "ACTIVATE")
    private ActivateStatusEnum activate;

    /**
     * 备注
     */
    @TableField(value = "REMARK")
    private String remark;

    /**
     * 添加时间
     */
    @TableField(value = "ADDTIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GTM+8")
    private Date addTime;

    @TableLogic
    @JsonIgnore
    @TableField(value = "ISDEL", select = false)
    private Integer isDel;

    /**
     * 装载区
     * */
    @TableField(exist = false)
    private Integer loadId;

    /**
     * 地图id
     * */
    @TableField(exist = false)
    private Integer mapId;

    /**
     * 装载区
     * */
    @TableField(exist = false)
    private String loadIdName;

    /**
     * 挖掘机编号
     * */
    @TableField(exist = false)
    private Integer excavatorNo;

    public enum Level implements IEnum, Desc {

        HIGH("2", "高"),
        MIDDLE("1", "中"),
        LOW("0", "低");

        private String value;
        private String desc;

        Level(String value, String desc) {
            this.value = value;
            this.desc = desc;
        }

        @JsonValue
        public String getValue(){
            return value;
        }

        public String getDesc(){
            return desc;
        }
    }
}
