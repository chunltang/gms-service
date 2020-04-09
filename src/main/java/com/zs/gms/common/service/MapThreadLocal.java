package com.zs.gms.common.service;

import java.util.HashMap;
import java.util.Map;

public  class MapThreadLocal<K,V> extends ThreadLocal<Map<K, V>>{
    @Override
    protected Map<K, V> initialValue() {
        return new HashMap<>();
    }

    /**
     * 获取值，没有返回defaultVal
     * */
    public V getValue(K key,V defaultVal) {
        Map<K,V> map = this.get();
        boolean b = null != map && map.containsKey(key);
        return (V)(b?map.get(key):defaultVal);
    }

    public void setValue(K clazz, V val) {
        Map<K, V> value = this.get();
        value.put(clazz, val);
        this.set(value);
    }
}