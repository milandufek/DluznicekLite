package cz.milandufek.dluzniceklite.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MyNumbers {

    public MyNumbers() {
    }

    /**
     * Round number to specified decimals
     * @param value
     * @param places
     * @return rounded number
     */
    public double roundIt(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
