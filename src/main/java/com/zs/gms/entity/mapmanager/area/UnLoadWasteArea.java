package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 排土场
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnLoadWasteArea extends BaseArea  implements Serializable {

    private static final long serialVersionUID = 973946043653629085L;
    /**
     * 排土类型，边缘式”cliff”和定点式“point”
     * */
    @NotBlank(message = "排土类型不能为空")
    private String type;

    /**
     * 排土点排土次数上限
     * */
    @NotNull(message = "排土点排土次数上限不能为空")
    private Integer unloadMax;
}
