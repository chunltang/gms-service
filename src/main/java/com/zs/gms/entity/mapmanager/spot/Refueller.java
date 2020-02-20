package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.point.AnglePoint;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 加油车
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Refueller  implements Serializable {
    private static final long serialVersionUID = 5955752477609077028L;

    /**
     * 属性类型,加油车”refueller”
     * */
    private String attributeType="refueller";

    /**
     * 长度
     * */
    private Float length;

    /**
     * 宽度
     * */
    private Float width;

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
}
