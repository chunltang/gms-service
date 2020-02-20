package com.zs.gms.mytest;

import java.util.Random;

public class RandomTest {

    public static void main(String[] args) {
        Random random=new Random();
        for (int i = 0; i < 100; i++) {
            int k = random.nextInt(3);
            System.out.println(k);
        }

    }
}
