package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 保护区
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SafetyArea implements Serializable {
    private static final long serialVersionUID = -6599680314575896520L;

    /**
     * 地图id
     * */
    private Long mapId;

    /**
     * 圆心
     * */
    @NotNull(message = "圆心不能为空")
    private Point center;

    /**
     * 半径
     * */
    @NotNull(message = "半径不能为空")
    private Float radius;

}
