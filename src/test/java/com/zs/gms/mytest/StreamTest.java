package com.zs.gms.mytest;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {

    public static void main(String[] args) {
        //数值流的构造
        IntStream.of(new int[]{1, 2, 3}).forEach(x->System.out.println(x));
        IntStream.range(1, 3).forEach(System.out::println);
        IntStream.rangeClosed(1, 3).forEach(System.out::println);

        //流转换为其它数据结构
        Stream<String> stream = Stream.of(new String[]{"aa", "bb", "cc"});
        // 1. Array
        //String[] strArray1 = stream.toArray(String[]::new);
        // 2. Collection
        //List<String> list1 = stream.collect(Collectors.toList());
        //List<String> list2 = stream.collect(Collectors.toCollection(ArrayList::new));
        Set set1 = stream.collect(Collectors.toSet());
        //Stack stack1 = stream.collect(Collectors.toCollection(Stack::new));
        // 3. String
        //String str = stream.collect(Collectors.joining()).toString();

        /*
        * Intermediate:
          map (mapToInt, flatMap 等)、 filter、 distinct、 sorted、 peek、 limit、 skip、 parallel、 sequential、 unordered
          Terminal:
          forEach、 forEachOrdered、 toArray、 reduce、 collect、 min、 max、 count、 anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 iterator
          Short-circuiting:
          anyMatch、 allMatch、 noneMatch、 findFirst、 findAny、 limit*/

        String format = String.format("删除地图失败，mapId=%s,approveId=%s", 1, "3242sds");
        System.out.println(format);
    }
}
