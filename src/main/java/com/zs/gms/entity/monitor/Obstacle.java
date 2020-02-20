package com.zs.gms.entity.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Obstacle implements Serializable {

    private Float length;
}
