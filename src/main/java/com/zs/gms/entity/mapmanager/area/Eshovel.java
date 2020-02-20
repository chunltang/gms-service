package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 电铲
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Eshovel implements Serializable {
    private static final long serialVersionUID = -5840284696492078859L;

    /**
     * 电铲位置坐标点
     * */
    private Point center;

    /**
     * 半径,电铲半径，从电铲信息数据库中获取
     * */
    @NotNull(message = "电铲半径不能为空")
    private Float radius;

    /**
     * 电铲位置文件绝对路径，此参数非空时将忽略center参数
     * */
    private String filePath;

    /**
     * 区域id
     * */
    @NotNull(message = "区域id不能为空")
    public  Long areaId;


    /**
     * 地图id
     * */
    //@NotNull(message = "地图id不能为空")
    public Long mapId;

    /**
     * 区域名称
     * */
    public String name;

    /**
     * 电铲id
     * */
    @NotNull(message = "电铲id不为空")
    public Integer eShovelId;
}
