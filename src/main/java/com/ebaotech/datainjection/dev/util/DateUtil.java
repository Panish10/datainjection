package com.ebaotech.datainjection.dev.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /*
     * Get String in "EEE MMM dd HH:mm:ss zzz yyyy" format and convert to Java util and return
     * */
    public static Date getDate(String dateString) {
        //String dateString = "Fri Oct 14 00:00:00 IST 2022";
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = null;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
