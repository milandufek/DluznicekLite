package cz.milandufek.dluzniceklite.utils;

import java.util.Calendar;

public final class MyDateTime {

    private static final String TAG = MyDateTime.class.toString();

    private MyDateTime() { }

    /**
     * Get time now
     * Format HH:mm (24h)
     */
    public static String getTimeNow() {
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

        // requires API26+
//        LocalDate date = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        return date.format(formatter);
    }

    /**
     * Get today's date
     * Format: YYYY-MM-DD format
     */
    public static String getDateToday() {
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

        // requires API26+
//        LocalDate date = LocalDate.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-DD");
//        return date.format(formatter);
    }

    /**
     * Convert date from DD.MM.YYYY to YYYY-MMM-DD
     * @param date
     * @return
     */
    public static String convertDateEuToUS(String date) {
        String [] tokensDate = date.split(".");
        return tokensDate[2] + "-" + tokensDate[1] + "-" + tokensDate[0];
    }

    /**
     * Convert date from YYYY-MMM-DD to DD.MM.YYYY
     * @param date
     * @return
     */
    public static String convertDateUsToEu(String date) {
        String [] tokensDate = date.split("-");
        return tokensDate[2] + "." + tokensDate[1] + "." + tokensDate[0];
    }
}
