####用户管理

DROP TABLE IF EXISTS `sys_user_log`;
CREATE TABLE `sys_user_log`
(
    `LOGID`       int(10) NOT NULL AUTO_INCREMENT COMMENT '用户日志id',
    `USERID`      int(4)  NOT NULL COMMENT '操作用户id',
    `USERNAME`    varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '用户名',
    `IP`          varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '登录ip',
    `CODE`        varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '执行结果状态码',
    `RESULTDESC`  varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '执行结果描述',
    `ELAPSEDTIME` BIGINT(10) COMMENT '操作耗时',
    `OPERATETIME` DATETIME(0) COMMENT '操作时间',
    `OPERATEDESC` varchar(50) COMMENT '操作描述',
    primary key (`LOGID`) using BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户日志表'
  ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`
(
    `USERID`   int(5)                                                  NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `USERNAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '用户名',
    `PASSWORD` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
    `THEME`    varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci  NULL DEFAULT NULL COMMENT '主题',
    `AVATAR`   varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '头像',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`USERID`) USING BTREE,
    UNIQUE KEY username (USERNAME)
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户表'
  ROW_FORMAT = Dynamic;

-- -------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`
(
    `ROLEID`   int(5)                                                  NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `ROLENAME` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '角色名称',
    `ROLESIGN` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci  NOT NULL COMMENT '角色英文标识',
    `ROLEDESC` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '角色描述',
    PRIMARY KEY (`ROLEID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '角色表'
  ROW_FORMAT = Dynamic;

-- -------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`
(
    `ID`     int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `USERID` int(5) NOT NULL COMMENT '用户ID',
    `ROLEID` int(5) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户角色关联表'
  ROW_FORMAT = Dynamic;

-- -------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`
(
    `MENUID`     int(5)                                                 NOT NULL AUTO_INCREMENT COMMENT '菜单/按钮ID',
    `PARENTID`   int(5)                                                 DEFAULT NULL COMMENT '上级菜单ID',
    `POWERLABEL` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单/按钮名称',
    `URL`        varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '菜单URL',
    `POWERNAME`  text CHARACTER SET utf8 COLLATE utf8_general_ci        NOT NULL COMMENT '权限标识',
    `ICON`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '图标',
    `POWERTYPE`  char(2) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '类型 0页面 1功能',
    `ORDERNUM`   int(5)                                                 NOT NULL COMMENT '排序',
    PRIMARY KEY (`MENUID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '菜单表'
  ROW_FORMAT = Dynamic;

-- -------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`
(
    `ID`     int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `ROLEID` int(5) NOT NULL COMMENT '角色ID',
    `MENUID` int(5) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '角色菜单关联表'
  ROW_FORMAT = Dynamic;

-- -----------------------------------------------------------------------------------------------------------------------
####定时调度

DROP TABLE IF EXISTS `t_job`;
CREATE TABLE `t_job`
(
    `JOBID`          int(6)                                                 NOT NULL AUTO_INCREMENT COMMENT '任务id',
    `BEANNAME`       varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'spring bean名称',
    `METHODNAME`     varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',
    `PARAMS`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
    `CRONEXPRESSION` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'cron表达式',
    `STATUS`         char(2) CHARACTER SET utf8 COLLATE utf8_general_ci     NOT NULL COMMENT '任务状态  0：正常  1：暂停',
    `REMARK`         varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
    `CREATETIME`     datetime(0)                                            NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`JOBID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '定时任务表'
  ROW_FORMAT = Dynamic;

-- -----------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `t_job_log`;
CREATE TABLE `t_job_log`
(
    `LOGID`      int(8)                                                  NOT NULL AUTO_INCREMENT COMMENT '任务日志id',
    `JOBID`      int(6)                                                  NOT NULL COMMENT '任务id',
    `BEAN_NAME`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'spring bean名称',
    `METHODNAME` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '方法名',
    `PARAMS`     varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参数',
    `STATUS`     char(2) CHARACTER SET utf8 COLLATE utf8_general_ci      NOT NULL COMMENT '任务状态    0：成功    1：失败',
    `ERROR`      text CHARACTER SET utf8 COLLATE utf8_general_ci         NULL COMMENT '失败信息',
    `TIMES`      decimal(11, 0)                                          NULL DEFAULT NULL COMMENT '耗时(单位：毫秒)',
    `CREATETIME` datetime(0)                                             NULL DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`LOGID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '调度日志表'
  ROW_FORMAT = Dynamic;

-- -----------------------------------------------------------------------------------------------------------------------
####车辆管理

DROP TABLE IF EXISTS `t_vehicle_type`;
CREATE TABLE `t_vehicle_type`
(
    `VEHICLETYPEID`        int(5)                                                 NOT NULL AUTO_INCREMENT COMMENT '车辆类型id',
    `VEHICLETYPE`          varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '车辆类型',
    `VEHICLESPECIFICATION` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '车辆规格',
    `VEHICLEICON`          varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆图标',
    `VEHICLEWIDTH`         varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆宽度',
    `VEHICLEHEIGHT`        varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆高度',
    `VEHICLELENGHT`        varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆长度',
    `VEHICLETON`           varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆吨位',
    `VEHICLEWHEEL`         varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '轴距',
    `VEHICLEANGLE`         varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车轮转角',
    `LIMITSPEED`           varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆限速',
    `TURNRADIAL`           varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '最小转弯半径',
    `CENTERWIDTH`          varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '计算中心宽度',
    `CENTERLENGHT`         varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '计算中长度',
    `CENTERHEIGHT`         varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '计算中高度',
    primary key (`VEHICLETYPEID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '车辆类型表'
  ROW_FORMAT = Dynamic;

#alter table t_vehicle_type add column  CENTERHEIGHT varchar(10);
-- -----------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `t_vehicle`;
CREATE TABLE `t_vehicle`
(
    `VEHICLEID`     int(5)                                             NOT NULL AUTO_INCREMENT COMMENT '车辆id',
    `VEHICLENO`     int(5)                                             NOT NULL COMMENT '车辆编号',
    `IP`            varchar(15) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车载系统ip',
    `PORT`          varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车载系统port',
    `SELFMOTION`    char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '是否自动驾驶 0自动，1手动',
    `ADDTIME`       datetime(0)                                        NOT NULL COMMENT '车辆添加时间',
    `NETINTIME`     datetime(0) COMMENT '车辆入网时间',
    `VEHICLESTATUS` char(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '车辆状态，0停用，1启动',
    `REMARK`        varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '车辆备注',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    primary key (`VEHICLEID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '车辆表'
  ROW_FORMAT = Dynamic;

-- -----------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `t_user_vehicle`;
CREATE TABLE `t_user_vehicle`
(
    `ID`        int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `USERID`    int(5) NOT NULL COMMENT '用户ID',
    `VEHICLEID` int(5) NOT NULL COMMENT '车辆ID',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '用户车辆关联表'
  ROW_FORMAT = Dynamic;

-- -----------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `t_iconlib`;
CREATE TABLE `t_iconlib`
(
    `LIBID`        int(3) NOT NULL AUTO_INCREMENT COMMENT '库id',
    `USERID`       int(5) NOT NULL  COMMENT '创建用户id',
    `USERNAME`    VARCHAR(20) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '创建用户名称',
    `FIRSTICONPATH`    VARCHAR(50) CHARACTER SET UTF8 COLLATE utf8_general_ci  COMMENT '第一个图片路径',
    `NAME`    VARCHAR(20) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '库名称',
    `REMARK`    VARCHAR(100) CHARACTER SET UTF8 COLLATE utf8_general_ci  COMMENT '备注',
    `PATH`    VARCHAR(50) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '相对路径',
    `STATUS`    CHAR(1) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '删除状态',
    `CREATETIME` datetime(0) NOT NULL COMMENT '上传时间',
    PRIMARY KEY (`LIBID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '图标库信息表'
  ROW_FORMAT = Dynamic;

-- -----------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS `t_vehicle_vehicleType`;
CREATE TABLE `t_vehicle_vehicleType`
(
    `ID`            int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `VEHICLETYPEID` int(5) NOT NULL COMMENT '车辆类型ID',
    `VEHICLEID`     int(5) NOT NULL COMMENT '车辆ID',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '车辆类型车辆关联表'
  ROW_FORMAT = Dynamic;


DROP TABLE IF EXISTS `t_bind_excavator`;
CREATE TABLE `t_bind_excavator`
(
    `ID`            int(5) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `USERID` int(5) NOT NULL COMMENT '用户ID',
    `LOADAREAID`     int(5) NOT NULL COMMENT '装载区ID',
    `EXCAVATORID`     int(5) NOT NULL COMMENT '挖掘机ID',
    `MAPID`     int(5)  COMMENT '地图id',
    `CREATETIME`     DATETIME(0) NOT NULL COMMENT '创建时间',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = utf8
  COLLATE = utf8_general_ci COMMENT = '挖掘机，用户，装载区关联表'
  ROW_FORMAT = Dynamic;

#alter table t_bind_excavator change LOADAREA LOADAREAID int(5);
-- -----------------------------------------------------------------------------------------------------------------------
####矿物管理
DROP TABLE IF EXISTS `t_mineral`;
CREATE TABLE `t_mineral`
(
    `MINERALID`   int(4)                                                 NOT NULL AUTO_INCREMENT COMMENT '矿物id',
    `MINERALNAME` VARCHAR(20) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '矿物名称',
    `REMARK`      VARCHAR(200) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '备注',
    `ADDTIME`     DATETIME(0)                                            NOT NULL COMMENT '添加时间',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`MINERALID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '矿物种类表'
  ROW_FORMAT = DYNAMIC;



DROP TABLE IF EXISTS `t_area_mineral`;
CREATE TABLE `t_area_mineral`
(
    `ID`          int(4)                                                 NOT NULL AUTO_INCREMENT COMMENT '数据id',
    `AREAID`      int(5)                                                 NOT NULL COMMENT '卸载区id',
    `MINERALID`   int(5)                                                 NOT NULL COMMENT '矿物id',
    `USERID`      int(5) COMMENT '添加用户',
    `MINERALNAME` VARCHAR(20) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '矿物名称',
    `STATUS`      VARCHAR(2) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '使用状态：0正在使用，1已变更',
    `ADDTIME`     DATETIME(0)                                            NOT NULL COMMENT '添加时间',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '卸载区和矿物对应表'
  ROW_FORMAT = DYNAMIC;
-- -----------------------------------------------------------------------------------------------------------------------
####地图管理
DROP TABLE IF EXISTS `t_map_info`;
CREATE TABLE `t_map_info`
(
    `ID`               int(4)                                                  NOT NULL AUTO_INCREMENT COMMENT 'id',
    `MAPID`            int(4)                                                  NOT NULL COMMENT '地图id',
    `USERID`           int(4)                                                  NOT NULL COMMENT '用户id',
    `NAME`             varchar(20) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '地图名称',
    `USERNAME`         varchar(20) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '用户名',
    `VERSION`          varchar(20) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '地图版本号',
    `STATUS`           varchar(20) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '状态：0使用中，1未使用，2发布确认，3删除确认',
    `COORDINATEORIGIN` VARCHAR(200) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '坐标原点',
    `BASEMAPPATH`      VARCHAR(20) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '底图文件绝对路径',
    `SPEED`            FLOAT(10)                                               NOT NULL COMMENT '地图限速',
    `LEFTDRING`        BOOLEAN                                                 NOT NULL COMMENT '靠左/右行驶,true: left, false: right',
    `REMARK`           VARCHAR(200) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '备注',
    `APPROVEID`        int(6) COMMENT '审批id',
    `ADDTIME`          DATETIME(0) COMMENT '地图创建时间',
    `UPDATETIME`       DATETIME(0) COMMENT '地图修改时间',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '地图信息表'
  ROW_FORMAT = DYNAMIC;


-- -----------------------------------------------------------------------------------------------------------------------
####运营监控
DROP TABLE IF EXISTS `t_vehicleLive`;
CREATE TABLE `t_vehicleLive`
(
    `DATAID`        int(4)      NOT NULL AUTO_INCREMENT COMMENT '数据id',
    `VEHICLESIGN`   varchar(15) NOT NULL COMMENT '车辆标识',
    `X`             BIGINT(10) COMMENT 'x',
    `Y`             BIGINT(10) COMMENT 'y',
    `Z`             BIGINT(10) COMMENT 'z',
    `ANGLE`         FLOAT(5, 3) COMMENT '横摆角',
    `SPEED`         FLOAT(3, 3) COMMENT '速度',
    `ACCELERATION`  FLOAT(10) COMMENT '车辆加速度',
    `VAPMODE`       VARCHAR(2) COMMENT '车载模式',
    `VAPSTATE`      VARCHAR(2) COMMENT '车载状态',
    `EXPIRETIME`    int(5) COMMENT '有效时间',
    `VEHSTATE`      VARCHAR(2) COMMENT '车辆状态',
    `DISPATCHSTATE` VARCHAR(2) COMMENT '调度状态',
    `TASKSTATE`     VARCHAR(2) COMMENT '任务状态',
    `ADDTIME`       varchar(13) not null COMMENT '添加时间',
    primary key (`DATAID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '车辆实时数据表'
  ROW_FORMAT = DYNAMIC;
##添加约束
alter table `t_vehicleLive`
    add unique `time_unique` (ADDTIME);

DROP TABLE IF EXISTS `t_dispatch_task`;
CREATE TABLE `t_dispatch_task`
(
    `UNITID`           int(4)                                                 NOT NULL AUTO_INCREMENT COMMENT '调度单元id',
    `MAPID`            int(4)                                                 NOT NULL COMMENT '地图id',
    `dispatchTaskType` varchar(1) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '调度任务类型',
    `USERID`           int(4)                                                 NOT NULL COMMENT '调度员id',
    `NAME`             varchar(50) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '调度单元名称',
    `STATUS`           varchar(1) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '单元状态:0移除，1使用中，2未使用',
    `ADDTIME`          datetime(0)                                            not null COMMENT '添加时间',
    `TASKTYPE`         varchar(2) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '特殊调度单元的类型',
    `TASKAREAID`       int(5) COMMENT '特殊调度单元的任务区id',
    `LOADAREAID`       int(5) COMMENT '装卸调度单元的装载点id',
    `UNLOADAREAID`     int(5) COMMENT '装卸调度单元的卸载点id',
    primary key (`UNITID`) using btree
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '调度单元表'
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `t_task_rule`;
CREATE TABLE `t_task_rule`
(
    `RULEID`     int(4)      NOT NULL AUTO_INCREMENT COMMENT '任务规则id',
    `UNITID`     int(4)      NOT NULL COMMENT '调度单元id',
    `USERID`     int(4)      NOT NULL COMMENT '调度员id',
    `VEHICLEID`  int(6)      NOT NULL COMMENT '车辆编号',
    `CYCLETIMES` INT(3) COMMENT '循环次数',
    `ENDTIME`    varchar(20) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '设置装卸任务结束时间',
    `ADDTIME`    datetime(0) not null COMMENT '添加时间',
    `POINTS`     varchar(500) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '交互式任务的点集',
    `STATUS`     varchar(1) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '完成状态:0进行中，1完成，2取消，3未完成',
    primary key (`RULEID`) using btree
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '调度任务规则表'
  ROW_FORMAT = DYNAMIC;

DROP TABLE IF EXISTS `t_liveInfo`;
CREATE TABLE `t_liveInfo`
(
    `ID`         int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `VEHICLEID`  int(6)  NOT NULL COMMENT '车辆编号',
    `MODESTATE`  varchar(15) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '车辆状态',
    `DISPSTATE`  varchar(15) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '调度状态',
    `TASKSTATE`  varchar(15) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '任务状态',
    `RUNFLAG`    char(1) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '是否运行',
    `UPDATETIME` datetime(0) COMMENT '产生时间',
    primary key (`ID`) using btree
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '实时调度信息表'
  ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `t_dispatchStatus`;
CREATE TABLE `t_dispatchStatus`
(
    `ID`         int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `VEHICLEID`  int(6)  NOT NULL COMMENT '车辆编号',
    `USERID`     int(4) COMMENT '用户id',
    `STATUS`     varchar(35) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '调度状态',
    `CREATETIME` datetime(0) COMMENT '产生时间',
    `UNITID`  int(5)  NOT NULL COMMENT '调度单元id',
    `MAPID`  int(4)  NOT NULL COMMENT '地图id',
    `EXCAVATORID`  int(4)  NOT NULL COMMENT '电铲id',
    `EXCAVATORUSERID`  int(4)  NOT NULL COMMENT '电铲用户id',
    `MINERALID`  int(4)  NOT NULL COMMENT '矿物id',
    `UNLOADID`  int(4)  NOT NULL COMMENT '卸载区id',
    primary key (`ID`) using btree
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '实时调度状态变更表'
  ROW_FORMAT = DYNAMIC;
-- ---------------------------------------------------------------------------------
##消息盒子
DROP TABLE IF EXISTS `t_fault`;
CREATE TABLE `t_fault`
(
    `FAULTID`    int(8)                                                 NOT NULL AUTO_INCREMENT COMMENT '故障id',
    `LEVEL`      varchar(20) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '故障等级',
    `DESC`       varchar(200) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '故障描述',
    `SOURCETYPE` varchar(1) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '故障来源:0人为，1车辆上报',
    `STATUS`     varchar(1) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '故障状态:0未处理，1处理中，2已处理',
    `SOURCE`     varchar(10) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '故障上报用户名或者是上报的车辆编号',
    `CREATETIME` datetime(0)                                            NOT NULL COMMENT '故障产生时间',
    `HANDLETIME` datetime(0) COMMENT '故障处理时间',
    `POSITION`   varchar(100) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '故障位置',
    `RADIUS`     float(4, 3) COMMENT '故障半径',
    `REMARK`     varchar(100) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '故障备注信息',
    primary key (`FAULTID`) using btree
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '故障表'
  ROW_FORMAT = DYNAMIC;


DROP TABLE IF EXISTS `t_approve`;
CREATE TABLE `t_approve`
(
    `APPROVEID`        int(6)                                                  NOT NULL AUTO_INCREMENT COMMENT '审批id',
    `SUBMITUSERID`     int(4)                                                  NOT NULL COMMENT '提交对象',
    `SUBMITUSERNAME`   varchar(16) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '提交对象名称',
    `APPROVETYPE`      char(1) CHARACTER SET UTF8 COLLATE utf8_general_ci      NOT NULL COMMENT '审批类型',
    `APPROVEUSERIDS`   varchar(30) CHARACTER SET UTF8 COLLATE utf8_general_ci  NOT NULL COMMENT '审批对象',
    `APPROVEDESC`      varchar(500) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT '审批描述',
    `CREATETIME`       datetime(0) COMMENT '提交时间',
    `APPROVETIME`      datetime(0) COMMENT '审批完成时间',
    `STATUS`           char(1) CHARACTER SET UTF8 COLLATE utf8_general_ci      NOT NULL COMMENT '审批状态',
    `APPROVEPROCESS`   varchar(500) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '审批过程',
    `PARAMS`           varchar(200) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '审批提交的参数,json格式',
    `APPROVEERRORDESC` varchar(100) CHARACTER SET UTF8 COLLATE utf8_general_ci COMMENT '审批异常描述',
    `RULE`             boolean COMMENT '审批规则：false只需要一个通过，true需要全部通过',
    `APPROVEMARK`      boolean COMMENT '处理结果标记,true为提交对象查看了数据，false为未处理，需要下次再推送',
    primary key (`APPROVEID`) using btree
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '审批表'
  ROW_FORMAT = DYNAMIC;

##excavator
DROP TABLE IF EXISTS `t_excavator`;
CREATE TABLE `t_excavator`
(
    `EXCAVATORID`  int(3)                                                 NOT NULL AUTO_INCREMENT COMMENT '挖掘机id',
    `EXCAVATORNO`  int(6)                                                 NOT NULL COMMENT '电铲编号' unique,
    `IP1`           varchar(15) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT 'GPS1的ip1',
    `IP2`           varchar(15) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT 'GPS2的ip2',
    `CAPACITY`     float(5, 2)                                            NOT NULL COMMENT '容量',
    `X1`            float(4, 2) COMMENT 'GPS安装位置x1',
    `X2`            float(4, 2) COMMENT 'GPS安装位置x2',
    `Y1`            float(4, 2) COMMENT 'GPS安装位置y1',
    `Y2`            float(4, 2) COMMENT 'GPS安装位置y2',
    `BRANCHLENGTH` float(4, 2) COMMENT '臂长',
    `CREATETIME`   datetime(0) COMMENT '创建时间',
    `ISDEL`    boolean default false COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`EXCAVATORID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 100
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT '挖掘机参数表'
  ROW_FORMAT = DYNAMIC;


##gps
DROP TABLE IF EXISTS `t_gps`;
CREATE TABLE `t_gps`
(
    `GPSID`  int(4)                                                 NOT NULL AUTO_INCREMENT COMMENT 'id',
    `GPSNO`  int(6)                                                 NOT NULL COMMENT 'gps编号' unique,
    `USERID`  int(5)                                                 NOT NULL COMMENT '用户id',
    `IP`           varchar(15) CHARACTER SET UTF8 COLLATE utf8_general_ci NOT NULL COMMENT 'GPS的ip',
    `CREATETIME`   datetime(0) COMMENT '创建时间',
    `ISDEL`    boolean  COMMENT '逻辑删除：1删除，0保留',
    PRIMARY KEY (`GPSID`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 0
  CHARACTER SET = UTF8
  COLLATE = utf8_general_ci COMMENT 'gps参数表'
  ROW_FORMAT = DYNAMIC;


/*DROP TABLE  IF EXISTS `t_monitor`;
CREATE TABLE `t_monitor`
(
    `ID` int(10) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `RELEVANCEID` int(10) NOT NULL  COMMENT '关联id',
    `MSGPRODDEVCODE`  int(6)    COMMENT '报文产生设备编号',
    `FROMVAKCODE`  int(4)    COMMENT '矿用自卸车编号(1-9999)',
    `YEAR`  int(4)    COMMENT '年',
    `MONTH`  int(2)    COMMENT '月',
    `DAY`  int(2)    COMMENT '日',
    `HOUR`  int(2)    COMMENT '时',
    `MINUTE`  int(2)    COMMENT '分',
    `SECOND`  float(8,6)    COMMENT '秒',
    `LOCKEDDEVICECODE`  int(6)    COMMENT '锁定设备编号,VAK当前所执行任务的来源设备',
    `MONITORDATATYPE`  int(6)    COMMENT '监控数据类型,1级还是2级',
    `VAKMODE`  varchar(30) CHARACTER SET UTF8 COLLATE utf8_general_ci    COMMENT '车辆模式编号',
    `CURRENTTASKCODE`  varchar(21) CHARACTER SET UTF8 COLLATE utf8_general_ci    COMMENT '当前任务编号',
    `TRACKCODE`  int(6)    COMMENT '轨迹编号',
    `VAKREQUESTCODE`  int(6)    COMMENT '车载请求编号',
    `CURRENTGEAR`  int(1)    COMMENT '车辆当前挡位',
    `GNSSSTATE`  int(1)    COMMENT 'GNSS状态',
    `LONGITUDE`  double(10,6)    COMMENT '经度',
    `LATITUDE`  double(10,6)    COMMENT '纬度',
    `XWORLD`  double(12,2)    COMMENT '大地坐标系x坐标 单位米',
    `YWORLD`  double(12,2)    COMMENT '大地坐标系y坐标 单位米',
    `XLOCALITY`  double(10,2)    COMMENT '局部坐标系x坐标 单位米',
    `YLOCALITY`  double(10,2)    COMMENT '局部坐标系y坐标 单位米',
    `YAWANGLE`  double(10,2)    COMMENT '横摆角  单位度',
    `NAVANGLE`  double(6,2)    COMMENT '航向角  单位度',
    `WHEELANGLE`  double(6,2)    COMMENT '前轮转向角  单位度',
    `CURSPEED`  double(6,2)    COMMENT '车辆速度  单位度',
    `ADDSPEED`  double(6,2)    COMMENT '车辆加速度  单位度',

    `STEERANGLE`  double(4,2)    COMMENT '实际方向盘转角  deg',
    `STEERROTSPEED`  double(4,2)    COMMENT '实际方向盘转速  deg/s',
    `ACCELERATORRATE`  double(4,2)    COMMENT '实际油门开度 %',
    `HYDBRAKERATE`  double(4,2)    COMMENT '液压制动器主缸实际制动压力比例 %',
    `ELECTRICFLOWBRAKERATE`  double(4,2)    COMMENT '电磁涡流制动器实际激磁电流比例 %',

    `MOTORSTATE`  int(1)             COMMENT '发动机状态',
    `FORWARDBRAKESTATE`  int(1)      COMMENT '行车制动状态',
    `ELECTRICBRAKESTATE`  int(1)     COMMENT '电缓制动状态',
    `PARKINGBRAKESTATE`  int(1)      COMMENT '停车制动状态',
    `LOADBRAKESTATE`  int(1)         COMMENT '装载制动状态',
    `ROTSPEED`  int(1)               COMMENT '发动机转速',
    `REALHOUSELIFTRATE`  int(1)      COMMENT '货舱状态',
    `LEFTLIGHTSTATE`  int(1)         COMMENT '左转向灯状态',
    `RIGHTLIGHTSTATE`  int(1)        COMMENT '右转向灯状态',
    `LIGHTSTATE`  int(1)             COMMENT '近光灯状态',
    `RLIGHTSTATE`  int(1)            COMMENT '示廓灯状态',
    `BRAKELIGHTSTATE`  int(1)        COMMENT '刹车灯状态',
    `EMERGENCYLIGHTSTATE`  int(1)    COMMENT '紧急信号灯状态',

    primary key (`ID`) using btree
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET =UTF8 COLLATE = utf8_general_ci COMMENT '车辆监控数据表' ROW_FORMAT = DYNAMIC;
*/

/*alter table t_gps  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_excavator  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_map_info  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_area_mineral  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_mineral  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_bind_excavator  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_vehicle  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_user_vehicle  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table sys_user  add column  `ISDEL`   boolean default 0  COMMENT '逻辑删除：1删除，0保留';
alter table t_bind_excavator  add column  `MAPID`  int(5)  COMMENT '地图id';*/


