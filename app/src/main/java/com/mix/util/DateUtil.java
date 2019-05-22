package com.mix.util;

import android.text.TextUtils;

import com.mix.constant.DataType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * Return date in specified format.
     *
     * @param dateString Date in String
     * @param pattern    Date format
     * @return Date that has been generated from json
     */
    public static Date convertToDate(String dateString, String pattern) {
        if (TextUtils.isEmpty(dateString)) {
            return new Date();
        }

        DateFormat formatter = null;
        formatter = new SimpleDateFormat(pattern);

        Date convertedDate = null;
        try {
            convertedDate = formatter.parse(dateString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new Date();
        }
        return convertedDate;
    }

    /**
     * Return date in specified format.
     *
     * @param milliSeconds Date in milliseconds
     * @param dateFormat   Date format
     * @return String representing date in specified format
     */
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    /**
     * Return milliseconds of given date
     *
     * @param date Date that should be converted
     * @return long generated milliseconds
     */
    public static long dateToMil(Date date) {
        return date.getTime();
    }
}
