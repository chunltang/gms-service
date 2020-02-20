package com.zs.gms.entity.mapmanager.other;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.point.AttrPoint;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 参考路径
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferencePath implements Serializable {
    private static final long serialVersionUID = 3070920111442207940L;


    private Long mapId;

    @NotNull(message = "区域id不能为空")
    private Long areaId;

    private String attributeType="referencePath";

    /**
     * 一个方向的参考路径上的坐标点集
     * */
    private AttrPoint[] points;


    private Integer referencePathId;

    private Integer laneType;
}
