package net.bplaced.abzzezz.util.math;

import java.util.function.DoubleFunction;
import java.util.function.Function;

public class MathUtil {

    public enum ActivationFunction {

        SIGMOID(x -> 1 / (1 + Math.exp(-x)), x -> sigmoid(x) * (1 - sigmoid(x))),
        TANH(x -> Math.tanh(x), x -> 1 - Math.pow(Math.tanh(x), 2)),
        RELU(x -> Math.max(0, x), x -> x > 0. ? 1. : 0.),
        LEAKY_RELU(x -> Math.max(0.01 * x, x), x -> x > 0. ? 1. : 0.01),
        SOFTMAX(x -> Math.exp(x) / (Math.exp(x) + 1), x -> Math.exp(x) / (Math.exp(x) + 1)),
        LINEAR(x -> x, x -> 1.);

        private final DoubleFunction<Double> function;
        private final DoubleFunction<Double> derivative;

        ActivationFunction(final DoubleFunction<Double> function, final DoubleFunction<Double> derivative) {
            this.function = function;
            this.derivative = derivative;
        }

        public double apply(final double value) {
            return function.apply(value);
        }

        public double applyDerivative(final double value) {
            return derivative.apply(value);
        }

    }

    public static double randomDouble(final int min, final int max) {
        return Math.random() * (max - min) + min;
    }

    /**
     * Calculates the sigmoid for a given value
     *
     * @param value value to calculate
     * @return the sigmoid value
     */
    public static double sigmoid(final double value) {
        return 1 / (1 + Math.exp(-value));
    }

    /**
     * Calculate the sigmoid activation function's derivative at a given value
     *
     * @param value the value to calculate the derivative at
     * @return the derivative value
     */
    public static double sigmoidDerivative(final double value) {
        return sigmoid(value) * (1 - sigmoid(value));
    }

    /**
     * Calculates the relu activation function
     *
     * @param value the value to calculate
     * @return the relu value
     */
    public static double relu(final double value) {
        return Math.max(0.01 * value, value);
    }

    /**
     * Calculates the derivative of the relu activation function
     *
     * @param value the value to calculate
     * @return the derivative of the relu value
     */
    public static double reluDerivative(final double value) {
        return value > 0 ? 1 : 0;
    }

    /**
     * Computed the error ei = (ti - oi)^2
     *
     * @param expectedValue the expected output
     * @param actualValue   the actual output
     * @return the difference squared
     */
    public static double computeError(final double expectedValue, final double actualValue) {
        return Math.pow((expectedValue - actualValue), 2);
    }

    /**
     * Maps a value from one range to another
     *
     * @param value  value to map
     * @param min    the value's min
     * @param max    the value's max
     * @param newMin the new minimum value
     * @param newMax the new maximum value
     * @return the mapped value
     */
    public static int mapValue(double value, double min, double max, int newMin, int newMax) {
        return (int) (((value - min) / (max - min)) * (newMax - newMin) + newMin);
    }

    public static double tanh(double value) {
        return Math.tanh(value);
    }
}
