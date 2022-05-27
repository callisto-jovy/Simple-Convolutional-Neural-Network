package net.bplaced.abzzezz.util;

import java.text.DecimalFormat;

public class Util {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".###");

    public static double sigmoid(final double value) {
        return 1 / (1 + Math.exp(-value));
    }
}
