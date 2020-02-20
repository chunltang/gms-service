package com.zs.gms.AsyncTest;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class TestAsync {

    @Async("gmsAsyncThreadPool")
    public  void test1()   {
        for (int i = 0; i < 100; i++) {
            System.out.println("test1>>"+i);
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Async("gmsAsyncThreadPool")
    public  void test2()   {
        for (int i = 0; i < 100; i++) {
            System.out.println("test2>>"+i);
            System.out.println(Thread.currentThread().getName());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
