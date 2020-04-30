package com.zs.gms.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapConfig {
    /**
     * 坐标原点，utm坐标{x,y,z}
     */
    @NotNull(message = "坐标原点不能为空")
    private Point coordinateOrigin;

    /**
     * 地图限速
     */
    @NotNull(message = "地图限速不能为空")
    private Float speed;

    /**
     * 靠左/右行驶
     */
    @NotNull(message = "靠左/右行驶不能为空")
    private boolean leftDring;
}
