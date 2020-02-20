package com.zs.gms.mytest;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class MapTest {

    public static Map<String,String> messageResult=new ConcurrentHashMap<String,String>(){

        @Override
        public String put(String key, String value) {
            System.out.println(key+"="+value);
            return super.put(key,value);
        }
    };

    public static void main(String[] args) {
        Map<String,Integer> map=new HashMap<>();
        map.computeIfAbsent("name",key-> 1);
        BiFunction<String,Integer,Integer> bif=new BiFunction<String, Integer, Integer>() {
            @Override
            public Integer apply(String s, Integer integer) {
                System.out.println(s);
                return integer+1;
            }
        };
        map.computeIfPresent("name",bif);

        map.merge("name",3,(x,y)->x+y);
        System.out.println(map);
    }
}
