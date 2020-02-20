package com.zs.gms.mytest;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.commons.lang3.StringUtils;

public class PatternTest {

    public static void main(String[] args) {
        String rex="[0-9]{13}";
        String str="map.txt.1574645935000";
        String[] split = StringUtils.split(str, StringPool.DOT);
        if(split.length>1&&split[2].matches(rex)){
            System.out.println("321");
        }else{
            System.out.println("rename");
        }
    }
}
