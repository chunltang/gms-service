package com.zs.gms.mytest;

import java.math.BigDecimal;

public class JiSuan {

    public static void main(String[] args) {
        double x=701234.2213f;
        double y=701234.2313f;
        BigDecimal bcx = BigDecimal.valueOf(x);
        BigDecimal bcy = BigDecimal.valueOf(y);
        BigDecimal bc1 = BigDecimal.valueOf(20037508.34f);
        BigDecimal bc2 = BigDecimal.valueOf(180);
        BigDecimal bc3 = BigDecimal.valueOf(Math.PI);
        BigDecimal bc4 = BigDecimal.valueOf(2);
        //x=bcx.divide(bc1,15,BigDecimal.ROUND_HALF_UP).multiply(bc2).doubleValue();
        //y=bcy.divide(bc1,15,BigDecimal.ROUND_HALF_UP).multiply(bc2).doubleValue();
        x = (x / 20037508.34f) * 180;
        y = (y / 20037508.34f) * 180;
        double value = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2);
        System.out.println(x);
        System.out.println(y);
        System.out.println(value);
    }
}
