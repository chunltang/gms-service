package com.zs.gms.service.init;

import com.zs.gms.entity.client.UserExcavatorLoadArea;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GmsCache {

    public final static Map<Integer, UserExcavatorLoadArea> bindData = new ConcurrentHashMap<>();
}
