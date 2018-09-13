package cz.milandufek.dluzniceklite.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Utility class
public final class MyNumbers {

    private MyNumbers() {
    }

    /**
     * Round number to specified decimals
     * @param value
     * @param places
     * @return rounded number
     */
    public static double roundIt(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static boolean numberToBoolean(int number) {
        return number == 1;
    }
    public static boolean numberToBoolean(double number) {
        return number == 1;
    }
}
