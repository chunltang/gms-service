package com.zs.gms.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryRequest  implements Serializable {

    private static final long serialVersionUID = -2540913312389240146L;
    /**
     * 排序类型
     * */
    private String sortType;

    /**
     * 排序字段
     * */
    private String field;

    /**
     * 当前页码
     * */
    private int pageNo=1;

    /**
     * 页容量
     * */
    private int pageSize=10;
}
