package com.zs.gms.controller.client;

import com.zs.gms.common.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/clients/park")
@Slf4j
@Api(tags = {"客户端管理"},description = "client Controller")
public class ParkController extends BaseController {

}
