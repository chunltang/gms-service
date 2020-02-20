package com.zs.gms.controller.terminalmanager;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = {"终端管理"},description = "Terminal Controller")
@RequestMapping(value = "/terminals")
public class terminalManager {

}
