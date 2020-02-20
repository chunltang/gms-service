package com.zs.gms.mytest;

public class ThreadTest {

    private  static String lock="lock";

    public static void main(String[] args) throws InterruptedException {
        ThreadTest test=new ThreadTest();
        test.execThread();
        Thread.sleep(2000);
        synchronized (lock){
            lock.notifyAll();
        }
    }

    public  void execThread(){
        for (int i = 0; i < 10; i++) {
            int k = i;
            new Thread(()->{
                synchronized (lock){
                    try {
                        lock.wait();
                        System.out.println("end:"+k);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }
}
