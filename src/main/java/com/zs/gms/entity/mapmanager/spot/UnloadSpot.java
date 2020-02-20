package com.zs.gms.entity.mapmanager.spot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseSpot;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;



/**
 * 排土点
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UnloadSpot extends BaseSpot implements Serializable {
    private static final long serialVersionUID = 1822738824116539890L;

    /**
     * 排土块编号
     * */
    @NotNull(message = "排土块编号不能为空")
    private Long unloadBlockId;

    /**
     * 属性类型,排队点”unloadSpot"
     * */
    private String attributeType="unloadSpot";
}