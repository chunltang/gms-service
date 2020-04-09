package com.zs.gms.entity.mapmanager.area;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.zs.gms.entity.mapmanager.BaseArea;
import com.zs.gms.entity.mapmanager.Point;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 不可通行区域
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImpassableArea extends BaseArea implements Serializable {

    private Point[] points;
}
