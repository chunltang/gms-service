package com.zs.gms.entity.mapmanager.other;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 动作组
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Actions implements Serializable {
    private static final long serialVersionUID = 4025884702754173249L;

    /**
     * 地图id
     * */
    private Long mapId;

    /**
     *参考路径编号
     * */
    @NotNull(message = "参考路径编号不能为空")
    private Long referencePathId;

    /**
     * 路径点编号
     * */
    @NotNull(message = "路径点编号不能为空")
    private Long vertexId;

    /**
     * 动作组
     * */
    @NotNull(message = "动作组不能为空")
    private Map<String,Object>[] actions;

}
