package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 卸矿区
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnLoadMineralArea extends BaseArea  implements Serializable {

    private static final long serialVersionUID = -4193548219955831229L;
    /**
     * 卸矿类型
     * */
    @NotBlank(message = "卸矿类型不能为空")
    private String unloadType;
}
