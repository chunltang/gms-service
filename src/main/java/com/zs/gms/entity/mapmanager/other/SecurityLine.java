package com.zs.gms.entity.mapmanager.other;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 安全线
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityLine implements Serializable {
    private static final long serialVersionUID = 2140532045637102871L;

    /**
     * 有序的局部坐标点数组
     * */
    private Point[] points;

    /**
     * 属性类型,安全线”securityLine”
     * */
    private String attributeType="securityLine";

    /**
     * 地图id
     * */
    public Long mapId;

    /**
     * 区域编号
     * */
    @NotNull(message = "区域编号不能为空")
    public Long areaId;

    /**
     * 文件路径
     * */
    public String filePath;
}
