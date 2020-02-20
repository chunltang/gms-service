package com.zs.gms.entity.mapmanager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseSpot implements Serializable {
    private static final long serialVersionUID = -2320052621912287243L;

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
     * 坐标点及其横摆角
     * */
    public AnglePoint spot;

    /**
     * 文件路径
     * */
    public String filePath;

    @NotNull(message = "点id不能为空")
    public Integer spotId;

}
