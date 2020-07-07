select ifnull(max(userId), 0) maxId
from sys_user;

select *
from (select *
      from t_map_info
      where STATUS = 1
        and ISDEL = false
      union
      select *
      from t_map_info
      where ISDEL = false
      order by ADDTIME desc) t
order by STATUS = 1 desc, ADDTIME desc


select *
from t_map_info
where ISDEL = false
order by STATUS = 1 desc, ADDTIME desc

select tmi.USERNAME = su.NAME, tmi.*
from t_map_info tmi
         left join sys_user su on tmi.USERID = su.USERID
where tmi.ISDEL = false

SELECT CONCAT(' select ', GROUP_CONCAT(COLUMN_NAME), ' from ', TABLE_NAME, ' ;')
FROM information_schema.COLUMNS
WHERE TABLE_NAME = 't_map_info'
  AND TABLE_SCHEMA = 'ems'
  and COLUMN_NAME != 'USERNAME';
select (
           select GROUP_CONCAT(COLUMN_NAME)
           from information_schema.COLUMNS
           WHERE table_name = 't_map_info'
             and COLUMN_NAME not in ('USERNAME', 'ID')) t
from t_map_info

SELECT concat('drop table ', 'mysql.', table_name, ';') sql拼接结果
FROM information_schema.TABLES
WHERE table_schema = 'myqsql'


select *
FROM information_schema.COLUMNS
WHERE TABLE_NAME = 't_map_info'

SELECT CONCAT(' select ', GROUP_CONCAT(COLUMN_NAME), ' from ', table_name, ' ;')
FROM information_schema.COLUMNS
WHERE table_name = 't_map_info'
  and COLUMN_NAME != 'ID';

set @sql = (SELECT CONCAT(' select ', GROUP_CONCAT(COLUMN_NAME), ' from ', table_name, ' ;')
            FROM information_schema.COLUMNS
            WHERE table_name = 't_map_info'
              and COLUMN_NAME not in ('ID', 'USERNAME'));

PREPARE stmt FROM @sql;
EXECUTE stmt;

select MAPID,
       tmi.USERID,
       su.NAME USERNAME,
       tmi.NAME,
       VERSION,
       STATUS,
       COORDINATEORIGIN,
       BASEMAPPATH,
       SPEED,
       LEFTDRING LEFTDRIVING,
       REMARK,
       ADDTIME,
       UPDATETIME,
       APPROVEID,
       IMAGEPATH
from t_map_info tmi
         left join sys_user su on tmi.USERID = su.USERID
where tmi.ISDEL = false
