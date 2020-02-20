package com.zs.gms.redistest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zs.gms.common.entity.GmsConstant;
import com.zs.gms.common.service.RedisService;
import com.zs.gms.service.monitor.schdeule.LiveVapHandle;
import com.zs.gms.entity.system.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private LiveVapHandle liveVapHandle;

    @Autowired
    private RedisTemplate<String,Object> template;

    @Test
    public void testRedis() throws JsonProcessingException {
        Map<String,String> map=new HashMap<>();
        map.put("name","tcl");
        map.put("age","23");
        ObjectMapper mapper=new ObjectMapper();
        String s = mapper.writeValueAsString(map);
        template.opsForValue().set("www",s);
        Object name = template.opsForValue().get("www");
        System.out.println(name);
    }

    @Test
    public void redisForObj() throws IOException {
        User user=new User();
        user.setUserName("tcl");
        user.setPassword("123456");
        user.setRoleName("admin");
        user.setUserId(1111);

        RedisService.set(GmsConstant.KEEP_DB,"ee",user,20000L, TimeUnit.MILLISECONDS);
        User aa = RedisService.get(GmsConstant.KEEP_DB,"ee",User.class);
        System.out.println(aa);
    }

    @Test
    public void redisLeftPop() throws IOException {
        ListOperations<String, Object> listOperations = RedisService.listOperations(RedisService.getTemplate(GmsConstant.KEEP_DB));
        listOperations.leftPushAll("vehicleNo1",1,2,3,4,5,6);
        for (int i = 0; i < 6; i++) {
            Object pop = listOperations.rightPop("vehicleNo1");
            System.out.println(pop);
        }
    }

    @Test
    public void redisRange() throws JsonProcessingException {
      /*  VehicleLive live = new VehicleLive();
        live.setAngle(11.0f);
        live.setPoint(new Point(1f, 2f, 3f));
        live.setSpeed(12f);
        live.setAddTime(System.currentTimeMillis());
        live.setVapMode(1123);
        live.setVehState(123);*/
        Map<String, Object> map = new HashMap<>();
        Map<String,Object> point=new HashMap<>();
        //map.put("live", live);
        map.put("name", 111);
        point.put("x", 111);
        point.put("y", 111);
        point.put("z","111");
        map.put("point", point);
        map.put("ch", 'x');
        map.put("time", "2019-11-11 11:11:11");
        map.put("arr", new ArrayList(Arrays.asList(new Integer[]{1, 2, 3, 4})));
        ObjectMapper mapper=new ObjectMapper();

        RedisService.listOperations(RedisService.getTemplate(GmsConstant.KEEP_DB)).leftPush("Vap_Trail_VehNo1", mapper.writeValueAsString(map));
    }
}
