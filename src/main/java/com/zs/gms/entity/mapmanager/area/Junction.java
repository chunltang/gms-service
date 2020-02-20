package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 路口
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Junction extends BaseArea implements Serializable {
    private static final long serialVersionUID = 133814149414179116L;

    /**
     * 类型,十字路口“CorssRoad”和丁字路口“T-Junction”
     * */
    @NotBlank(message = "类型不能为空")
    private String type;
}
