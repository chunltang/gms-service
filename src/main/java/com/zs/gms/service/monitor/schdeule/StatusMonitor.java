package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.LiveInfo;
import com.zs.gms.entity.monitor.VehicleStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 状态监控中心
 */
@Slf4j
@Data
public class StatusMonitor extends AbstractVehicleStatusHandle {

    private static volatile StatusMonitor instance = null;

    private Map<Class<? extends VehicleStatusHandle>, VehicleStatusHandle> handleMap;

    private ExecutorService executorService;

    private ArrayBlockingQueue<Runnable> queue;

    private int tCount = 0;

    private static Object lock=new Object();

    private StatusMonitor() {
        int processors = Runtime.getRuntime().availableProcessors();
        queue = new ArrayBlockingQueue<>(processors*10, true);
        executorService = new ThreadPoolExecutor(2,
                processors*5,
                60,
                TimeUnit.SECONDS,
                queue,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("gms-statusMonitor-thread-" + tCount++);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
        handleMap = new HashMap<>();
        handleMap.put(VehicleErrorStatusHandle.class, new VehicleErrorStatusHandle());
        handleMap.put(VehicleDispatchStatusHandle.class, new VehicleDispatchStatusHandle());
        handleMap.put(VehicleObstacleStatusHandle.class, new VehicleObstacleStatusHandle());
        handleMap.put(VehicleTaskStatusHandle.class, new VehicleTaskStatusHandle());
    }

    /**
     * 获取队列任务数
     */
    public static int getTaskCount() {
        if (instance != null && instance.getQueue() != null) {
            return instance.getQueue().size();
        }
        return 0;
    }

    public static StatusMonitor getInstance() {
        if (instance == null)
            synchronized (lock) {
                if (instance == null)
                    instance = new StatusMonitor();
            }
        return instance;
    }

    /**
     * 获取处理类
     */
    public static <T> T getHandle(Class<? extends VehicleStatusHandle> clazz) {
        if (getInstance().getHandleMap().containsKey(clazz)) {
            return (T) instance.getHandleMap().get(clazz);
        }
        return null;
    }

    /**
     * 执行添加任务
     */
    private void addTask(Runnable task) {
        executorService.execute(task);
    }

    /**
     * 任务委派
     */
    public void delegateStatus(LiveInfo liveInfo) {
        Integer vehicleId = liveInfo.getVehicleId();
        delegateStatus(liveInfo.getTaskState(), vehicleId, VehicleTaskStatusHandle.class, liveInfo.getUpdateTime());
        //delegateStatus(liveInfo.getTaskState(),vehicleId,VehicleErrorStatusHandle.class);
        delegateStatus(liveInfo.getMonitor().getVecObstacle(), vehicleId, VehicleObstacleStatusHandle.class, liveInfo.getUpdateTime());
        delegateStatus(liveInfo.getDispState(), vehicleId, VehicleDispatchStatusHandle.class, liveInfo.getUpdateTime());
    }

    /**
     * 添加任务
     */
    private void delegateStatus(Object obj, Integer vehicleId, Class<? extends VehicleStatusHandle> clazz, Date time) {
        if (handleMap.containsKey(clazz)) {
            addTask(() -> {
                try {
                    VehicleStatus vehicleStatus = new VehicleStatus();
                    vehicleStatus.setCreateTime(time);
                    vehicleStatus.setVehicleId(vehicleId);
                    vehicleStatus.setStatus(obj);
                    handleMap.get(clazz).handleStatus(vehicleStatus);
                } catch (Exception e) {
                    log.error("状态监控任务执行失败", e);
                }
            });
        }
    }

}
