package com.zs.gms.entity.mapmanager.other;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 路口连接
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JunctionConnection  implements Serializable {
    private static final long serialVersionUID = -1254753550663153123L;

    /**
     * 地图id
     * */
    private Long mapId;

    /**
     * 区域编号
     * */
    @NotNull(message = "区域编号不能为空")
    private Long areaId;

    /**
     * 属性类型
     * */
    private String attributeType="connection";

    /**
     * 来路编号
     * */
    @NotNull(message = "来路编号不能为空")
    private Long incomingRoadId;

    /**
     * 去路编号
     * */
    @NotNull(message = "去路编号不能为空")
    private Long connectingRoadId;
}
