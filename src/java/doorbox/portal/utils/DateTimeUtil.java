/*
 * Copyright (C) 2016 TheDoorbox.com - All rights reserved.
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */
package doorbox.portal.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author torinw
 */
public class DateTimeUtil {
    public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static String DEFAULT_TIME_FORMAT = "HH:mm:ss zzz";
    public static String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss zzz";
    
    public static String getDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        return sdf.format(new Date());
    }
    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        return sdf.format(new Date());
    }
    public static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_TIME_FORMAT);
        return sdf.format(new Date());
    }    
    public static String getFormattedDateTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
        return sdf.format(date);
    }
}
