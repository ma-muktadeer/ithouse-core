package com.ihouse.core.jdbc.utils;


import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static final String STR_dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss";
    public static final SimpleDateFormat DF_dd_MM_yyyy_HH_mm_ss = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static final String STR_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final SimpleDateFormat DF_yyyy_MM_dd_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final String STR_dd_MM_yyyy = "dd-MM-yyyy";
    public static final SimpleDateFormat DF_dd_MM_yyyy = new SimpleDateFormat("dd-MM-yyyy");
    public static final String STR_yyyy_MM_dd = "yyyy-MM-dd";
    public static final SimpleDateFormat DF_yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");

    public DateUtils() {
    }

    public static Date parse(String dtStr) throws Exception {
        Date dt = null;
        if (!StringUtils.isEmpty(dtStr)) {
            if (dtStr.split(" ").length == 1) {
                if (dtStr.split("-")[0].length() == 4) {
                    dt = DF_yyyy_MM_dd.parse(dtStr);
                } else if (dtStr.split("-")[0].length() <= 2) {
                    dt = DF_dd_MM_yyyy.parse(dtStr);
                }
            } else if (dtStr.split(" ").length == 2) {
                if (dtStr.split("-")[0].length() == 4) {
                    dt = DF_yyyy_MM_dd_HH_mm_ss.parse(dtStr);
                } else if (dtStr.split("-")[0].length() <= 2) {
                    dt = DF_dd_MM_yyyy_HH_mm_ss.parse(dtStr);
                }
            }
        }

        return dt;
    }

    public static Timestamp getSqlTimestamp(Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    public static java.sql.Date getSqlDate(Date date) {
        return date == null ? null : new java.sql.Date(date.getTime());
    }
}
