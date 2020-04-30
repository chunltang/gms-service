select *
from gms_base.sys_user;

SELECT date_format(CREATETIME, '%Y-%m')          time,
       count(*)                                  count,
       tds.VEHICLEID                             vehicleId,
       IFNULL(tds.VEHICLECAPACITY, 0) * count(*) volume
FROM t_dispatchStatus tds
where 1 = 1
  and tds.vehicleId = 10003
GROUP BY date_format(CREATETIME, '%Y-%m'), tds.VEHICLEID, tds.VEHICLECAPACITY
ORDER BY time DESC;


####################################################################################################
select tuv.USERID       USERID,
       tdt.MAPID        MAPID,
       tdt.LOADAREAID,
       tdt.UNLOADAREAID UNLOADID,
       tdt.UNITID       UNITID,
       tv.VEHICLENO     VEHICLEID,
       tbe.USERID       EXCAVATORUSERID,
       tbe.EXCAVATORID  EXCAVATORID,
       tam.MINERALID    MINERALID,
       tvt.VEHICLETON   VEHICLECAPACITY
from t_vehicle tv
         inner join t_user_vehicle tuv on tv.VEHICLEID = tuv.VEHICLEID
         inner join t_vehicle_vehicleType tvv on tvv.VEHICLEID = tv.VEHICLEID
         inner join t_vehicle_type tvt on tvt.VEHICLETYPEID = tvv.VEHICLETYPEID
         inner join t_task_rule ttr on ttr.VEHICLEID = tv.VEHICLENO
         inner join t_dispatch_task tdt on tdt.UNITID = ttr.UNITID
         inner join t_bind_excavator tbe on tbe.LOADAREAID = tdt.LOADAREAID
         inner join t_area_mineral tam on tam.AREAID = tdt.LOADAREAID
where 1 = 1
  and tv.VEHICLENO = 10003
  and tv.ISDEL = false
  and ttr.STATUS not in ('3', '5')
  and tdt.STATUS != '0'
  and tam.ISDEL = false
  and tbe.ISDEL = false
  and tdt.MAPID = tbe.MAPID;

##检查挖掘机绑定
select tuv.USERID       USERID,
       tdt.MAPID        MAPID,
       tdt.LOADAREAID,
       tdt.UNLOADAREAID UNLOADID,
       tdt.UNITID       UNITID,
       tv.VEHICLENO     VEHICLEID,
       tbe.USERID       EXCAVATORUSERID,
       tbe.EXCAVATORID  EXCAVATORID,
       tvt.VEHICLETON   VEHICLECAPACITY
from t_vehicle tv
         inner join t_user_vehicle tuv on tv.VEHICLEID = tuv.VEHICLEID
         inner join t_vehicle_vehicleType tvv on tvv.VEHICLEID = tv.VEHICLEID
         inner join t_vehicle_type tvt on tvt.VEHICLETYPEID = tvv.VEHICLETYPEID
         inner join t_task_rule ttr on ttr.VEHICLEID = tv.VEHICLENO
         inner join t_dispatch_task tdt on tdt.UNITID = ttr.UNITID
         inner join t_bind_excavator tbe on tbe.LOADAREAID = tdt.LOADAREAID
where 1 = 1
  and tv.VEHICLENO = 10003
  and tv.ISDEL = false
  and ttr.STATUS not in ('3', '5')
  and tdt.STATUS != '0'
  and tbe.ISDEL = false
  and tdt.MAPID = tbe.MAPID;

##检查调度单元
select tuv.USERID       USERID,
       tdt.MAPID        MAPID,
       tdt.LOADAREAID,
       tdt.UNLOADAREAID UNLOADID,
       tdt.UNITID       UNITID,
       tv.VEHICLENO     VEHICLEID,
       tvt.VEHICLETON   VEHICLECAPACITY
from t_vehicle tv
         inner join t_user_vehicle tuv on tv.VEHICLEID = tuv.VEHICLEID
         inner join t_vehicle_vehicleType tvv on tvv.VEHICLEID = tv.VEHICLEID
         inner join t_vehicle_type tvt on tvt.VEHICLETYPEID = tvv.VEHICLETYPEID
         inner join t_task_rule ttr on ttr.VEHICLEID = tv.VEHICLENO
         inner join t_dispatch_task tdt on tdt.UNITID = ttr.UNITID
where 1 = 1
  and tv.VEHICLENO = 10003
  and tv.ISDEL = false
  and ttr.STATUS not in ('3', '5')
  and tdt.STATUS != '0';


##检查车
select tuv.USERID     USERID,
       tv.VEHICLENO   VEHICLEID,
       tvt.VEHICLETON VEHICLECAPACITY
from t_vehicle tv
         inner join t_user_vehicle tuv on tv.VEHICLEID = tuv.VEHICLEID
         inner join t_vehicle_vehicleType tvv on tvv.VEHICLEID = tv.VEHICLEID
         inner join t_vehicle_type tvt on tvt.VEHICLETYPEID = tvv.VEHICLETYPEID
where 1 = 1
  and tv.VEHICLENO = 10003;

##检查调度规则
select *
from t_task_rule
where VEHICLEID = 10003
  and STATUS not in ('3', '5');
################################################################################################


SELECT tuv.USERID
FROM t_vehicle tv
         LEFT JOIN t_user_vehicle tuv ON (tv.VEHICLEID = tuv.VEHICLEID)
WHERE 1 = 1
  AND tv.ISDEL = false
  AND tv.VEHICLENO = 10001;

select tv.VEHICLENO, tuv.UNITID
from t_vehicle tv
         left join (select t.VEHICLEID, t.UNITID from t_unit_vehicle t where t.ISDEL = false) tuv
                   on tv.VEHICLENO = tuv.VEHICLEID
where tv.ISDEL = false
  and tv.VEHICLESTATUS = '1';

select *
from t_vehicle tv
where tv.ISDEL = false
  and tv.VEHICLESTATUS = '1';


select tm.*, tbe.EXCAVATORID, tam.AREAID
from t_mineral tm
         left join (select t.MINERALID, t.AREAID from t_area_mineral t where t.ISDEL = false) tam
                   on tm.MINERALID = tam.MINERALID
         left join (select LOADAREAID, EXCAVATORID from t_bind_excavator t where t.ISDEL = false) tbe
                   on tam.AREAID = tbe.LOADAREAID
where tm.ISDEL = false;


SELECT te.EXCAVATORID,
       te.EXCAVATORTYPEID,
       tet.CAPACITY,
       tet.BRANCHLENGTH,
       te.CREATETIME,
       te.EXCAVATORNO,
       te.IP1,
       te.IP2,
       te.X1,
       te.X2,
       te.Y1,
       te.Y2,
       tbe.USERID,
       tbe.LOADAREAID LOADID,
       tbe.MAPID,
       su.USERNAME
FROM t_excavator te
         LEFT JOIN t_excavator_type tet ON tet.EXCAVATORTYPEID = te.EXCAVATORTYPEID
         LEFT JOIN t_bind_excavator tbe ON tbe.EXCAVATORID = te.EXCAVATORID
         LEFT JOIN sys_user su ON su.USERID = tbe.USERID
WHERE 1 = 1
  AND te.ISDEL = false
ORDER BY EXCAVATORID DESC
LIMIT 0,10;


select tu.*,su.userName
from t_unit_vehicle tuv
         left join t_unit tu on tuv.UNITID = tu.UNITID
         left join sys_user su on su.USERID = tu.USERID
where tuv.ISDEL = false
  and tu.ISDEL = false
  and tuv.VEHICLEID = 10001