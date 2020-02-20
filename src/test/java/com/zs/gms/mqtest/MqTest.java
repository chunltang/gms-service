package com.zs.gms.mqtest;

import com.alibaba.fastjson.JSON;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.service.RabbitMqService;
import com.zs.gms.common.utils.GmsUtil;
import com.zs.gms.common.utils.HttpUtil;
import com.zs.gms.entity.mapmanager.Point;
import com.zs.gms.service.mapmanager.MapDataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MqTest {

    @Test
    public void test(){
        RabbitMqService.sendMessage("testAutoExChange","testKey","test auto create!","");
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRest(){
        Map<String,Object> params=new HashMap<>();
        params.put("userName","admin");
        params.put("password","123456");
        HttpUtil.restRequest("http://192.168.2.100:8080/login", HttpMethod.POST, JSON.toJSONString(params));
    }
}
