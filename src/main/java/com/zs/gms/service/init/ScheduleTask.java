package com.zs.gms.service.init;

import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.entity.StaticConfig;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.common.service.nettyclient.WsUtil;
import com.zs.gms.common.service.websocket.FunctionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class ScheduleTask {

    private static double x = 0L;

    private static double y = 0L;

    private static int step=0;

    /**
     * 检测消息无响应过期,大于过期时间
     * {@link GmsConstant#WAIT_TIME}
     * */
    //@Scheduled(cron = "* * * * * *")
    public void checkOver(){
        for (int i = 0; i < 100; i++) {
            WsUtil.sendMessage(""+(step++)+"这是一条很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的消息", FunctionEnum.console);
        }
    }


    //@Scheduled(cron = "*/1 * * * * *")
    public void putData() throws InterruptedException {
        for (int k = 0; k < 5; k++) {
            Thread.sleep(10);
            for (int j = 0; j < 6; j++) {
                Random random = new Random();
                int i = random.nextInt(10) + 10010;
                i = 10003;
                y += 1;
                x += 5;
                if (x > 300) {
                    x = 0;
                    y = 0;
                }
                String jsonStr = "{\"vehicleId\":" + i + ",\"modeState\":4,\"dispState\":1,\"taskState\":6,\"runFlag\":0,\"updateTime\":\"20191211150837\",\"monitor\":{\"msgProdDevCode\":10001,\"fromVakCode\":10001," +
                        "\"year\":119,\"month\":11,\"day\":11,\"hour\":15,\"minute\":8,\"second\":37.000000," +
                        "\"lockedDeviceCode\":0,\"monitorDataType\":0,\"vakMode\":0,\"currentTaskCode\":0," +
                        "\"trackCode\":0,\"vakRequestCode\":0,\"currentGear\":0,\"gnssState\":0,\"longitude\":0.000000," +
                        "\"latitude\":0.000000,\"xWorld\":" + (708622.775 + y) + ",\"yWorld\":" + (y + 3088246.569) + ",\"xLocality\":0,\"yLocality\":0,\"yawAngle\":3130,\"navAngle\":3117,\"wheelAngle\":699,\"curSpeed\":0,\"addSpeed\":0,\"countofObstacle\":0,\"realSteerAngle\":0,\"realSteerRotSpeed\":0,\"realAcceleratorRate\":0,\"realHydBrakeRate\":0,\"realElectricFlowBrakeRate\":0,\"realMotorState\":0,\"realForwardBrakeState\":0,\"realElectricBrakeState\":0,\"realParkingBrakeState\":0,\"realLoadBrakeState\":0,\"realMotorRotSpeed\":0,\"realHouseLiftRate\":0,\"realTurnLeftlightState\":0,\"realTurnRightlightState\":0,\"realNearLightState\":0,\"realContourLightState\":0,\"realBrakeLightState\":0,\"realEmergencyLightState\":111111111,\"vecObstacle\":[]}}";
                String baseKey = "vap_base_" + i;
                RedisService.set(StaticConfig.MONITOR_DB, baseKey, jsonStr);
            }
        }


        String pathStr = "0,10001,6,708622.674,3088246.531,51.972,0,349.250,0.000,0.000,0.000,0.000,0.000,0.020,0.000,0,708622.775,3088246.548,51.967,0,0.582,0.000,1.923,0.000,0.000,0.000,0.202,0.103,0,708622.884,3088246.569,51.963,0,4.681,0.000,0.650,0.000,0.000,0.000,0.308,0.213,0,708622.992,3088246.590,51.963,0,6.241,0.000,0.247,0.000,0.000,0.000,0.387,0.323,0,708623.103,3088246.614,51.961,0,8.059,0.000,0.279,0.000,0.000,0.000,0.451,0.437,0,708623.212,3088246.642,51.961,0,9.810,0.000,0.272,0.000,0.000,0.000,0.506,0.549,0";
        String pathKey = "vap_path_" + 10001;
        RedisService.set(StaticConfig.MONITOR_DB, pathKey, pathStr);
    }
}

