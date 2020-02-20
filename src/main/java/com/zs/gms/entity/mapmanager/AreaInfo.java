package com.zs.gms.entity.mapmanager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.enums.mapmanager.AreaTypeEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AreaInfo implements Serializable {
    private static final long serialVersionUID = 4511347147990596988L;

    /**
     * 地图id
     * */
    //@NotNull(message = "地图id不能为空")
    private Long mapId;

    /**
     * 区域类型
     * */
    @NotNull(message = "区域类型不能为空")
    private AreaTypeEnum areaType;

    /**
     * 区域名称
     * */
    private String name;

    /**
     * 点集
     * */
    @NotNull
    private Point[] points;

    /**
     * 区域限速
     * */
    private Float speed;

}
