package net.bplaced.abzzezz.util;

import java.text.DecimalFormat;

public class Util {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(".###");
    public static final int IMAGE_SIZE = 128;

    public static final int FULLY_CONNECTED_NETWORK_DEPTH = 2;

    public static final int FULLY_CONNECTED_NETWORK_WIDTH = 300;

    public static double sigmoid(final double value) {
        return 1 / (1 + Math.exp(-value));
    }

    public static double relu(final double value) {
        return Math.max(0, value);
    }
    public static double derelu(final double value) {
        return (value > 0.01) ? 1.0 : 0.0;
    }

    /**
     * Computed the error ei = (ti - oi) \square
     *
     * @param expectedValue the expected output
     * @param actualValue   the actual output
     * @return the difference squared
     */
    public static double computeError(final double expectedValue, final double actualValue) {
        return Math.pow((expectedValue - actualValue), 2);
    }
}
