package com.zs.gms.controller.system;

import com.zs.gms.common.authentication.ShiroHelper;
import com.zs.gms.common.controller.BaseController;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.system.UserService;
import org.apache.shiro.authz.AuthorizationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;

@Controller("systemView")
@ApiIgnore
public class ViewController extends BaseController {

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    @Lazy
    private ShiroHelper shiroHelper;

    @GetMapping("/login")
    @ResponseBody
    public Object login(HttpServletRequest request){
        userService.findByName("tcl");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("system/login");
        return mav;
    }

    @RequestMapping("/webSocket")
    public String webSocket(){
        return "testWebSocket";
    }


    @GetMapping("/")
    public String redirectIndex() {
        return "redirect:/index";
    }


    @GetMapping("index")
    public String index(Model model) {
        AuthorizationInfo authorizationInfo = shiroHelper.getCurrentUserAuthorizationInfo();
        User user = super.getCurrentUser();
        user.setPassword("It's a secret");
        model.addAttribute("user", userService.findByName(user.getUserName())); // 获取实时的用户信息
        model.addAttribute("permissions", authorizationInfo.getStringPermissions());
        model.addAttribute("roles",authorizationInfo.getRoles());
        return "index";
    }
}
