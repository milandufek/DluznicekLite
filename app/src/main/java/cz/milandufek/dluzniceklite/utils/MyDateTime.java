package cz.milandufek.dluzniceklite.utils;

import java.util.Calendar;

public class MyDateTime {

    public MyDateTime() {
    }

    /**
     * Get time now
     * Format HH:mm (24h)
     */
    public String getTimeNow() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String hourZeroPrefix = "";
        String minuteZeroPrefix = "";
        if (hour < 10)
            hourZeroPrefix = "0";
        if (minute < 10)
            minuteZeroPrefix = "0";

        return hourZeroPrefix + hour + ":" + minuteZeroPrefix + minute;
    }

    /**
     * Get today's date in YYYY-MM-DD format
     */
    public String getDateToday() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month = month + 1;

        String monthZeroPrefix = "";
        String dayZeroPrefix = "";
        if (month < 10)
            monthZeroPrefix = "0";
        if (day < 10)
            dayZeroPrefix = "0";

        return year + "-" + monthZeroPrefix + month + "-" + dayZeroPrefix + day;
    }
}
