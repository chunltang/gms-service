package com.zs.gms.entity.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GlobalPath {

    private int no;
    private int vehicleId;
    private int status;
    @JsonProperty("vertex_num")
    private int vertexNum;
    List<Vertex> data;
    private String messageId;
}
