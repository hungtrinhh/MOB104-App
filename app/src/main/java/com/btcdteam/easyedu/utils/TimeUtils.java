package com.btcdteam.easyedu.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtils {
    public final static String monthAgo = " tháng trước";
    public final static String weekAgo = " tuần trước";
    public final static String daysAgo = " ngày trước";
    public final static String hoursAgo = " giờ trước";
    public final static String minAgo = " phút trước";
    public final static String secAgo = " giây trước";
    static final int second = 1000; // milliseconds
    static final int minute = 60;
    static final int hour = minute * 60;
    static final int day = hour * 24;
    static final int week = day * 7;
    static final int month = day * 30;
    static final int year = month * 12;

    public static String convertToDate(long timestamp) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        return formatter.format(calendar.getTime());
    }

    public static String timeAgo(long fromDate) {
        long diff = 0;
        long ms2 = System.currentTimeMillis();
        diff = ms2 - fromDate;

        int diffInSec = Math.abs((int) (diff / (second)));
        String difference = "";
        if (diffInSec < minute) {
            difference = diffInSec + secAgo;
        } else if ((diffInSec / hour) < 1) {
            difference = (diffInSec / minute) + minAgo;
        } else if ((diffInSec / day) < 1) {
            difference = (diffInSec / hour) + hoursAgo;
        } else if ((diffInSec / week) < 1) {
            difference = (diffInSec / day) + daysAgo;
        } else if ((diffInSec / month) < 1) {
            difference = (diffInSec / week) + weekAgo;
        } else if ((diffInSec / year) < 1) {
            difference = (diffInSec / month) + monthAgo;
        } else {
            // return date
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(fromDate);

            SimpleDateFormat format_before = new SimpleDateFormat(
                    "dd-MM-yyyy HH:mm:ss", Locale.getDefault());

            difference = format_before.format(c.getTime());
        }
        return difference;
    }
}
