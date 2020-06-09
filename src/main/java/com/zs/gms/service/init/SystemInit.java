package com.zs.gms.service.init;

import com.zs.gms.common.properties.RoleProperties;
import com.zs.gms.entity.system.Role;
import com.zs.gms.entity.system.User;
import com.zs.gms.service.mineralmanager.AreaMineralService;
import com.zs.gms.service.monitor.UnitVehicleService;
import com.zs.gms.service.system.RoleService;
import com.zs.gms.service.system.UserRoleService;
import com.zs.gms.service.system.UserService;
import com.zs.gms.service.vehiclemanager.BarneyService;
import com.zs.gms.service.vehiclemanager.ExcavatorService;
import com.zs.gms.service.vehiclemanager.UserExcavatorLoadAreaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * 初始化角色
 * */
@Component
@Slf4j
public class SystemInit{

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleProperties roleProperties;

    @Autowired
    private AreaMineralService areaMineralService;

    @Autowired
    private BarneyService barneyService;

    @Autowired
    private ExcavatorService excavatorService;

    @Autowired
    private UnitVehicleService unitVehicleService;


    /**
     * 初始化角色
     * */
    public void initRoleData(){
        Map<String, String[]> roles = roleProperties.getRoles();
        if(!MapUtils.isEmpty(roles)){
            Set<Map.Entry<String, String[]>> entries = roles.entrySet();
            for (Map.Entry<String, String[]> entry : entries) {
                getRole(entry);
            }
        }
        log.info("初始化系统角色完成");
    }

    public void getRole(Map.Entry<String, String[]> entry){
        String key = entry.getKey();
        if(!roleService.roleExistsByRoleSign(key)){
            String[] value = entry.getValue();
            Role role=new Role();
            role.setRoleSign(key);
            role.setRoleName(value[0]);
            role.setRoleDesc(value[1]);
            roleService.addRole(role);
        }
    }

    /**
     * 初始化系统超级用户
     * */
    public void initAdminUser(){
        String adminRoleValue = Role.RoleSign.ADMIN_ROLE.getValue();
        boolean exists = userRoleService.userExistsByRoleSign(adminRoleValue);
        if(!exists){
            User user=new User();
            Role role = roleService.getRoleIdByRoleSign(adminRoleValue);
            if(null!=role){
                Integer maxId = userService.getMaxId();
                String str = String.format("%03d", maxId);
                Role.RoleSign roleSign = Role.getEnum(role.getRoleSign());
                user.setUserId(maxId);
                user.setUserName(roleSign.getSign()+str);
                user.setName(adminRoleValue);
                user.setRoleId(role.getRoleId());
                user.setTheme(User.THEAM_WHITE);
                user.setAvatar(User.DEFAULT_ICON);
                user.setPassword(User.DEFAULT_PWD);
                userService.addUser(user);
            }
        }
        log.info("初始化admin用户完成");
    }

    /**
     * 初始化数据激活状态
     * */
    public void updateActiveStatus(){
        areaMineralService.updateMineralActive();
        barneyService.updateTypeActive();
        excavatorService.updateExcavatorActive();
        excavatorService.updateExcavatorTypeActive();
        unitVehicleService.updateVehicleActive();
    }

    public void init() {
        this.initRoleData();
        this.initAdminUser();
        this.updateActiveStatus();
    }
}
