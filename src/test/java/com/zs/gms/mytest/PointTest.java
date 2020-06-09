package com.zs.gms.mytest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointTest {

    public static void main(String[] args) throws JsonProcessingException {
        List<Map<String, Double>> points = new ArrayList<>(3000000);
        Map<String, Double> point;
        for (int i = 0; i < 3000000; i++) {
            point = new HashMap<>();
            point.put("x", 1111312.1321);
            point.put("y", 1111312.1321);
            point.put("z", 0d);
            points.add(point);
        }
        long t1 = System.currentTimeMillis();
        ObjectMapper mapper = new ObjectMapper();
        String string = mapper.writeValueAsString(points);
        System.out.println(string);
        System.out.println("time="+(System.currentTimeMillis()-t1));
        System.out.println("length="+string.length());
    }
}
