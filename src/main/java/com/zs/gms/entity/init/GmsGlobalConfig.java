package com.zs.gms.entity.init;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@TableName("t_config")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GmsGlobalConfig {

    @TableId(value = "CONFIGID",type = IdType.AUTO)
    private Integer configId;

    @TableField("CONFIGKEY")
    private String configKey;

    @TableField("CONFIGVALUE")
    private String configValue;
}
