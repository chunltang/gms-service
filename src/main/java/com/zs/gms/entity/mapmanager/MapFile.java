package com.zs.gms.entity.mapmanager;

import lombok.Data;

@Data
public class MapFile {

    /**
     * 文件编号
     * */
    private Long id;
    /**
     * 地图id
     * */
    private Integer mapId;

    /**
     * 文件名称
     * */
    private String name;

    /**
     * 文件创建时间
     * */
    private String creatTime;

    /**
     * 文件最后修改时间
     * */
    private String lastUpdateTime;

    /**
     * 文件大小
     * */
    private Long size;

    /**
     * 文件类型
     * */
    private String fileType;
}
