package com.zs.gms.common.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.LinkedList;

/**
 * 固定数据大小队列
 * */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LimitQueue<T> extends LinkedList<T> {

    private Integer limit;

    @JsonCreator
    public LimitQueue(Integer limit){
        this.limit=limit;
    }

    @Override
    public boolean add(T element) {
        super.add( element);
        while (size()>limit){
            synchronized (this){
                super.remove();//删除第一个元素
            }
        }
        return true;
    }
}
