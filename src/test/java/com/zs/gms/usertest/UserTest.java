package com.zs.gms.usertest;

import com.zs.gms.common.annotation.Log;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.RoleService;
import com.zs.gms.service.system.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.matchers.JUnitMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    /**
     * .perform() : 执行一个MockMvcRequestBuilders的请求；MockMvcRequestBuilders有.get()、.post()、.put()、.delete()等请求。
     * .andDo() : 添加一个MockMvcResultHandlers结果处理器,可以用于打印结果输出(MockMvcResultHandlers.print())。
     * .andExpect : 添加MockMvcResultMatchers验证规则，验证执行结果是否正确。
     */
    @Test
    public void helloWorld() throws Exception {
        this.mockMvc.perform(get("/user/hello"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello World")));
    }

    @Test
    public void getUserList() throws Exception {
        this.mockMvc.perform(get("/user/getUserList"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void testAnnotation(){
        Map<String, Object> maps= applicationContext.getBeansWithAnnotation(Log.class);
        for (Map.Entry<String, Object> entry : maps.entrySet()) {
            Log ann =  entry.getValue().getClass().getAnnotation(Log.class);
            System.out.println(ann.value());
        }
    }

    @Test
    public void testUserService(){
        User user = userService.findByName("tcl");
        System.out.println(user);
    }
}
