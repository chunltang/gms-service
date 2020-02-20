package com.zs.gms.propertytest;

import com.zs.gms.entity.monitor.VehicleLive;
import com.zs.gms.service.monitor.VehicleLiveService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BarneyProTest {

    @Autowired
    private VehicleLiveService vehicleLiveService;
    @Test
    public void test(){
        VehicleLive vehicleLive=new VehicleLive();
        vehicleLive.setVehState(1);
        vehicleLive.setSpeed(11f);
        vehicleLive.setVapMode(1);
        vehicleLive.setAddTime(System.currentTimeMillis());
        vehicleLive.setAcceleration(1f);
        vehicleLive.setDispatchState(1);
        vehicleLive.setTaskState(1);
        vehicleLive.setX(11l);
        vehicleLive.setY(11l);
        vehicleLive.setZ(12l);
        vehicleLive.setAngle(11f);
        vehicleLive.setVapState(1);
        vehicleLive.setVehicleSign("vheNo1");
        vehicleLiveService.addVehicleLive(vehicleLive);
    }
}
