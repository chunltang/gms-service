package com.zs.gms.jdk;

import com.zs.gms.common.utils.ThreadLocalUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalTest {

    static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        for(int i=0;i<2;i++){
            int k=i;
            executorService.execute(new TaskThread(k));
        }
    }

    static class TaskThread implements Runnable{

        private Integer pId;
        private Integer mId;

        public TaskThread(Integer pId) {
            mId=pId+10;
            ThreadLocalUtil.set(mId);
            this.pId = pId;

        }

        @Override
        public void run() {
            if(pId>200)
                return;
            ThreadLocalUtil.setInLocal(mId);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(ThreadLocalUtil.getInLocal());
                    executorService.execute(new TaskThread(mId));
                }
            });
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
