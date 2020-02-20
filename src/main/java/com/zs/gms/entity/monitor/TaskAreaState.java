package com.zs.gms.entity.monitor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zs.gms.enums.monitor.TaskAreaStateEnum;
import com.zs.gms.enums.monitor.TaskTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务区状态
 * */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskAreaState implements Serializable {

    /**
     * 区域id
     * */
    private Integer id;

    /**
     * 区域类型
     * */
    private TaskTypeEnum taskType;

    private TaskSpot[] taskSpots;

    @Data
    public static class TaskSpot implements Serializable {

        /**
         * 任务点状态
         * */
        private Integer id;

        /**
         * 任务类型
         * */
        private TaskAreaStateEnum state;
    }
}
