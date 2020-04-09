package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.VehicleLiveInfo;
import com.zs.gms.entity.monitor.VehicleStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 状态监控中心
 */
@Slf4j
public class StatusMonitor extends AbstractVehicleStatusHandle {

    private static Map<Class<? extends VehicleStatusHandle>, VehicleStatusHandle> handleMap;

    private static TaskService taskService;


    static{
        taskService=new TaskService();
        handleMap = new HashMap<>();
        handleMap.put(VehicleErrorStatusHandle.class, new VehicleErrorStatusHandle());
        handleMap.put(VehicleDispatchStatusHandle.class, new VehicleDispatchStatusHandle());
        handleMap.put(VehicleObstacleStatusHandle.class, new VehicleObstacleStatusHandle());
        handleMap.put(VehicleTaskStatusHandle.class, new VehicleTaskStatusHandle());
    }


    /**
     * 获取处理类
     */
    public static <T> T getHandle(Class<? extends VehicleStatusHandle> clazz) {
        if (handleMap.containsKey(clazz)) {
            return (T) handleMap.get(clazz);
        }
        return null;
    }


    /**
     * 任务委派
     */
    public static void delegateStatus(VehicleLiveInfo vehicleLiveInfo) {
        LivePosition.handleDelegate(vehicleLiveInfo,vehicleLiveInfo.getVehicleId());
        Integer vehicleId = vehicleLiveInfo.getVehicleId();
        delegateStatus(vehicleLiveInfo.getTaskState(), vehicleId, VehicleTaskStatusHandle.class, vehicleLiveInfo.getUpdateTime());
        //delegateStatus(vehicleLiveInfo.getTaskState(),vehicleId,VehicleErrorStatusHandle.class);
        delegateStatus(vehicleLiveInfo.getMonitor().getVecObstacle(), vehicleId, VehicleObstacleStatusHandle.class, vehicleLiveInfo.getUpdateTime());
        delegateStatus(vehicleLiveInfo.getDispState(), vehicleId, VehicleDispatchStatusHandle.class, vehicleLiveInfo.getUpdateTime());
    }

    /**
     * 添加任务
     */
    private static void delegateStatus(Object obj, Integer vehicleId, Class<? extends VehicleStatusHandle> clazz, Date time) {
        if (handleMap.containsKey(clazz)) {
            taskService.addTask(() -> {
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

    private static class TaskService {

        private ExecutorService executorService;

        private ArrayBlockingQueue<Runnable> queue;

        private int tCount = 0;

        public TaskService() {
            int processors = Runtime.getRuntime().availableProcessors();
            queue = new ArrayBlockingQueue<>(processors * 10, true);
            this.executorService = new ThreadPoolExecutor(2,
                    processors * 5,
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
        }

        /**
         * 获取队列任务数
         */
        public  int getTaskCount() {
            return queue.size();
        }

        /**
         * 执行添加任务
         */
        private void addTask(Runnable task) {
            executorService.execute(task);
        }
    }
}
