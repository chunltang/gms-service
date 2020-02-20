package com.zs.gms.entity.mapmanager.other;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 道路连接
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoadLink implements Serializable {
    private static final long serialVersionUID = 5721134368675964754L;

    /**
     * 地图id
     * */
    private Long mapId;

    /**
     * 区域id
     * */
    @NotNull(message = "区域id不能为空")
    private Long areaId;

    /**
     *前驱区域编号
     * */
    @NotNull(message = "前驱区域编号不能为空")
    private Long predecessorId;

    /**
     * 后继区域编号
     * */
    @NotNull(message = "后继区域编号不能为空")
    private Long successororId;
}
