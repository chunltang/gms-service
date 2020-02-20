package com.zs.gms.mqtest;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class SemaphoreTest {

    public static void main(String[] args) throws InterruptedException {

        //信号量控制并发数最多为3
        Semaphore semaphore = new Semaphore(3);

        //同时开启10个线程
        for(int i=1;i<=10;i++){
            new Thread(new ReaderThread(semaphore,i)).start();
        }

    }


    static class ReaderThread implements Runnable{

        Semaphore semaphore;

        //用户序号
        int userIndex;

        public ReaderThread(Semaphore semaphore, int userIndex) {
            this.semaphore = semaphore;
            this.userIndex = userIndex;
        }

        @Override
        public void run() {
            try {
                //获取许可
                semaphore.acquire(1);
                //模拟访问资源所用的时间
                TimeUnit.SECONDS.sleep(2);

                System.out.println("用户 "+userIndex+" 访问资源,时间:"+System.currentTimeMillis());

                //释放许可
                semaphore.release();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
