package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 道路
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Road extends BaseArea implements Serializable {
    private static final long serialVersionUID = -9212842381025741147L;

    /**
     * 类型,主线（major）、支线（branch）和连接线（connector）
     * */
    private  String type;

    /**
     * 容量，“Single”或“Double”
     * */
    @NotBlank(message = "容量不能为空")
    private String capacity;

    /**
     * 道路长度
     * */
    private Double length;

}
