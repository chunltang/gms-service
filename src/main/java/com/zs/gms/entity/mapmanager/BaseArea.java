package com.zs.gms.entity.mapmanager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseArea implements Serializable {

    private static final long serialVersionUID = 2559772554275304440L;
    /**
     * 区域id
     * */
    @NotNull(message = "区域id不能为空")
    public  Long areaId;


    /**
     * 地图id
     * */
    public Long mapId;

    /**
     * 区域名称
     * */
    public String name;

    /**
     * 区域限速
     * */
    @NotNull(message = "区域限速不能为空")
    public Float speed;
}
