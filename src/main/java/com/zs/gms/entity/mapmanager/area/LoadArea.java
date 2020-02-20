package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 装载区
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoadArea extends BaseArea  implements Serializable {

    private static final long serialVersionUID = 6699712196074330045L;
    /**
     *装载类型
     * */
    @NotBlank(message = "装载类型不能为空")
    private String loadType;
}
