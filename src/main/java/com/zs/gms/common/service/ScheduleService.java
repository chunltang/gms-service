package com.zs.gms.common.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * 定时任务服务
 * */
@Data
@Slf4j
public class ScheduleService {

    private ScheduledThreadPoolExecutor executor;

    private static ScheduleService instance;

    private static int curCount=0;

    private static int tid=0;

    private ScheduleService() {
        executor=new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("gms-schedule-thread-"+tid);
                tid++;
                return thread;
            }
        },new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 添加定时任务
     * */
    public  static ScheduledFuture addTask(Runnable task,long delayed,TimeUnit timeUnit){
        if(instance==null || instance.getExecutor().isShutdown()){
            instance=new ScheduleService();
        }
        if(task!=null && delayed>0 && timeUnit!=null){
            log.debug("添加定时任务:delayed={},timeUnit={}",delayed,timeUnit.name());
            curCount++;
            return  instance.getExecutor().scheduleWithFixedDelay(()->{
                try {
                    task.run();
                }catch (Exception e){
                    log.error("定时任务执行失败",e);
                }
            }, 0L, delayed, timeUnit);
        }
        return null;
    }

    /**
     * 关闭定时任务线程池
     * */
    public static void shutdown(){
        if(instance.getExecutor()!=null && !instance.getExecutor().isShutdown()){
            instance.getExecutor().shutdown();
            curCount=0;
        }
    }

    /**
     * 当前定时任务个数
     * */
    public static int getCurCount(){
        return curCount;
    }

    /**
     * 获取队列任务个数
     * */
    public static int getTaskCount(){
        if(instance.getExecutor()!=null && !instance.getExecutor().isShutdown()){
            return instance.getExecutor().getQueue().size();
        }
        return 0;
    }

    /**
     * 取消定时任务
     * */
    public static void cancel(ScheduledFuture future,boolean interruptRun){
        if(future!=null && !future.isCancelled()){
            log.debug("取消定时任务");
            future.cancel(interruptRun);
            curCount--;
        }
    }

    /**
     * 执行延迟任务且只执行一次,delayed毫秒
     * */
    public static void addSingleTask(Runnable task,long delayed){
        if(null==task || delayed<=0){
            return;
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                task.run();
                this.cancel();
            }
            }, delayed);
    }
}
