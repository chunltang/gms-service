package com.zs.gms.service.monitor.schdeule;

import com.zs.gms.entity.monitor.VehicleStatus;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractVehicleStatusHandle implements VehicleStatusHandle {

    private  Map<Integer,Object[]> prevStatusMap;//车辆上一个状态

    private final  static long interval=1000*60;

    public AbstractVehicleStatusHandle(){
        prevStatusMap=new HashMap<>();
    }

    public void put(Integer key,Object obj){
        prevStatusMap.put(key,new Object[]{obj,System.currentTimeMillis()});
    }

    public Object[] get(Integer key){
        if(prevStatusMap.containsKey(key)){
            return prevStatusMap.get(key);
        }
        return new Object[2];
    }

    @Override
    public void handleStatus(VehicleStatus vehicleStatus) {
        Object status = vehicleStatus.getObj();
        if(status!=null){
            if(status.equals(get(vehicleStatus.getVehicleId())[0])){
                //状态没有改变
                noChanged(vehicleStatus);
            }else{
                changed(vehicleStatus);
            }
        }
    }

    @Override
    public void noChanged(VehicleStatus vehicleStatus){
        Object[] objects = get(vehicleStatus.getVehicleId());
        if(objects!=null && objects.length==2){
            long prevTime = Long.valueOf(objects[1].toString());
            long millis = System.currentTimeMillis();
            if(prevTime+interval<millis){
                //状态1分钟没改变
                objects[1]=millis;
                overtime(vehicleStatus);
            }
        }
    };

    /**
     * 超时操作
     * */
    public void overtime(VehicleStatus vehicleStatus){
        //do nothing
    }

    @Override
    public void changed(VehicleStatus vehicleStatus){
        //状态改变
        this.put(vehicleStatus.getVehicleId(),vehicleStatus.getObj());
        save(vehicleStatus);
    };

    @Override
    public void save(VehicleStatus vehicleStatus) {
        //do nothing
    }

    @Override
    public String push(Integer vehicleId) {
        return "";
    }
}
