package com.zs.gms.common.service;

import com.zs.gms.common.utils.GmsUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class DelayedService extends Thread {

    private final ConcurrentSkipListMap<Long, List<Task>> taskMap = new ConcurrentSkipListMap();

    private  static DelayedService service;

    private final static long DEFAULT_DELAY = 600 * 1000;

    private static volatile boolean isAwait = false;//是添加任务唤醒true，还是延时任务唤醒

    private static AtomicInteger generateId = new AtomicInteger(0);

    @Resource(name = "gmsAsyncThreadPool")
    private Executor pool;

    @PostConstruct
    public void init() {
        service = this;
        service.pool=pool;
        service.setName("delay-task-thread");
    }

    /**
     * 添加任务
     */
    public static Task addTask(Runnable task, long delay) {
        return addTask(Task.build(task), delay);
    }

    public static Task addTask(Task task, long delay) {
        if (!service.isAlive()) {
            service.start();
        }
        if (null == task || null == task.getTask()) {
            log.error("参数异常");
            return null;
        }
        service.initTask(task, delay);
        awakeTask();
        return task;
    }

    private void initTask(Task task, long delay) {//初始任务信息
        long curTime = GmsUtil.getCurTime();
        long next = curTime + delay;
        task.setAddTime(curTime);
        task.setDelay(delay);
        if (task.isAtOnce()) {
            task.setNextTime(curTime);
            service.put(curTime, task);
        } else {
            task.setNextTime(next);
            service.put(next, task);
        }

    }

    private void put(long key, Task task) {
        taskMap.computeIfAbsent(key, list -> Collections.synchronizedList(new ArrayList<Task>())).add(task);
    }

    private void putAll(long key, List<Task> tasks) {
        taskMap.computeIfAbsent(key, list -> Collections.synchronizedList(new ArrayList<Task>())).addAll(tasks);
    }

    /**
     * 唤醒线程
     */
    private static void awakeTask() {
        synchronized (service.taskMap) {
            isAwait = true;
            service.taskMap.notifyAll();
        }
    }

    /**
     * 更新已有任务
     * */
    public static  void updateTask(Task task){
        Long key=0l;
        for (Map.Entry<Long, List<Task>> entry : service.taskMap.entrySet()) {
            key=entry.getKey();
            List<Task> value = entry.getValue();
            value.removeIf(t -> t.getTaskId().equals(task.getTaskId()));
        }
        if(service.taskMap.containsKey(key) && service.taskMap.get(key).isEmpty()){
            service.removeTask(key);
        }
        addTask(task,task.getDelay());
    }

    /**
     * 删除任务
     */
    private void removeTask(Long key) {
        taskMap.remove(key);
    }

    /**
     * 任务等待
     */
    private void waitTask(long delay) {
        synchronized (taskMap) {
            try {
                isAwait = false;
                taskMap.wait(delay);
            } catch (InterruptedException e) {
                log.error("延时线程执行失败", e);
            }
        }
    }

    private void execute(long key, List<Task> tasks) {//执行任务检查，是否执行任务
        Iterator<Task> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            Task task = iterator.next();
            if (task.isNeedExec() && (task.getNum() > 0 || task.getNum() == -1)) {
                pool.execute(()->{
                    log.debug("定时延时任务:thread={},desc={},taskId={},delay={},num={},step={}",
                            Thread.currentThread().getName(),task.getDesc(), task.getTaskId(), task.getDelay(), task.getNum(), task.getStep());
                    task.getTask().run();
                });
                task.update();
            }
            if (!task.isNeedExec() || task.getNum() == 0) {
                task.setNeedExec(false);
                iterator.remove();
            }
        }
        updateKey(key, tasks);
    }


    private void updateKey(long key, List<Task> tasks) {//更新下次任务执行时间,删除原有任务，添加新任务
        if (!tasks.isEmpty()) {
            Task task = tasks.get(0);
            putAll(task.getNextTime(), tasks);
        }
        removeTask(key);
    }

    @Override
    public void run() {
        while (true) {
            Map.Entry<Long, List<Task>> entry = taskMap.firstEntry();
            if (null != entry) {
                Long key = entry.getKey();
                List<Task> tasks = entry.getValue();
                long val = key - GmsUtil.getCurTime();
                if (val <= 0) {//任务过期时无条件执行
                    execute(key, tasks);
                    continue;
                }
                waitTask(val);//等待最执行时间的任务
                //如果不是外部添加任务唤醒的线程，则为第一个任务执行时间到了,如果是外部任务唤醒则重新获取第一个任务
                if (!isAwait) {
                    execute(key, tasks);
                }
            } else {
                waitTask(DEFAULT_DELAY);
            }
        }
    }

    @Data
    public static class Task {

        private Integer taskId;

        private Runnable task;//执行任务

        private List<Entry<Integer, Object>> result;//执行结果

        private long delay;//延时时间

        private long nextTime;//下次执行时间

        private long addTime;//任务添加时间

        private boolean atOnce = false;//是否立即运行,该参数要起作用，需将该参数在添加任务之前设置

        private int num = 1;//执行次数，默认一次，-1为无次数限制

        private int step = 0;//当前执行次数

        private String desc;//任务描述

        private boolean needExec = true;//执行标志


        public static Task build(Runnable task) {
            return build().withTask(task);
        }

        public static Task build() {
            return new Task().withTaskId(generateId.incrementAndGet());
        }

        public void update() {//跟新下次任务执行时间
            this.step++;
            if (this.num != -1) {
                this.num--;
            }
            this.nextTime = this.addTime + (step + (this.atOnce ? 0 : 1)) * this.delay;
        }

        public Task withTaskId(int taskId) {//设置任务号
            this.taskId = taskId;
            return this;
        }

        public Task withAtOnce(boolean atOnce) {//设置立即运行
            this.atOnce = atOnce;
            return this;
        }

        public Task withNum(int num) {//设置执行次数
            this.num = num;
            return this;
        }

        public Task withTask(Runnable task) {//设置任务
            this.task = task;
            return this;
        }

        public Task withDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public void putResult(Object result) {//添加任务执行结果
            Entry<Integer, Object> entry = new Entry<>();
            entry.put(this.step, result);
            this.result = GmsUtil.CollectionNotNull(this.result) ? this.result : new ArrayList<>();
            this.result.add(entry);
        }

        @Data
        private static class Entry<K, V> {

            private K key;

            private V value;

            public void put(K key, V value) {
                this.key = key;
                this.value = value;
            }
        }
    }

    /**************test**************/
    /*private static int step=0;

    @PostConstruct
    public void test() {
        DelayedService.Task task = DelayedService.Task.build();//有返回值的样例
        task.withTask(() -> {
            int exec = exec();
            task.putResult(exec);
        }).withNum(10000);
        DelayedService.addTask(task,20);
    }

    private int exec(){
        System.out.println(++step);
        return step;
    }*/
}
