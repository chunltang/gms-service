package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.mapmanager.Point;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 车辆实时位置
 * */
public class LivePosition {

    private static LivePosition instance=new LivePosition();

    /**
     * 车辆上一个位置
     * */
    private static Map<Integer, Point> prePointMap=new ConcurrentHashMap<>();

    /**
     * 车辆上一个位置所在区域
     * */
    private static Map<Integer, Point> preAreaMap=new ConcurrentHashMap<>();
}
