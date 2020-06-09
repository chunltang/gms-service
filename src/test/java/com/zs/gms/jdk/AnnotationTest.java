package com.zs.gms.jdk;

import com.zs.gms.common.annotation.NotNull;
import com.zs.gms.common.annotation.Parser;
import com.zs.gms.entity.monitor.DispatchStatus;
import com.zs.gms.enums.monitor.DispatchStateEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotationTest {

    public static void main(String[] args) {
        /*DispatchStatus dispatchStatus=new DispatchStatus();
        dispatchStatus.setUnitId(1);
        dispatchStatus.setUnLoadId(1);
        dispatchStatus.setExcavatorUserId(1);
        dispatchStatus.setExcavatorId(1);
        dispatchStatus.setMineralId(1);
        dispatchStatus.setMapId(1);
        dispatchStatus.setObj(null);
        dispatchStatus.setVehicleId(1);
        dispatchStatus.setUserId(1);
        Parser.notNull(dispatchStatus);*/

        Student student=new Student();
        student.setName("xiaoming");
        student.setAlis(new String[]{});
        ArrayList<String> objects = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("1","1");
        objects.add("aa");
        student.setList(objects);
        student.setMap(hashMap);
        Parser.notNull(student);
    }

    @Data
    public static class Student{

        @NotNull
        private String name;
        @NotNull
        private int age;
        @NotNull
        private String[] alis;
        @NotNull
        private List<String>  list;
        @NotNull
        private Map<String,Object> map;
    }
}
