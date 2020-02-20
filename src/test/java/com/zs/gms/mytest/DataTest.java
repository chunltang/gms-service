package com.zs.gms.mytest;

import com.zs.gms.common.utils.DateUtil;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

public class DataTest {

    public static void main(String[] args) throws ParseException {
        String yyyy = DateUtil.getDateFormat(new Date(), "yyyy");
        System.out.println(yyyy);

        String instant = DateUtil.formatInstant(Instant.now(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(instant);

        String fullTime = DateUtil.formatFullTime(LocalDateTime.of(2019, 8, 28, 19, 19));
        System.out.println(fullTime);

        String time = DateUtil.formatFullTime(LocalDateTime.now(),"mm");
        System.out.println(time);

        String longTime = DateUtil.formatLongTime(1574401275000l);
        System.out.println(longTime);
    }
}
