package net.bplaced.abzzezz.util.math.vector;

import net.bplaced.abzzezz.util.math.MathUtil;
import net.bplaced.abzzezz.util.math.matrix.Matrix;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.DoubleStream;

public class Vec {
    /**
     * Array representing the vectors internal data,
     * which may be manipulated, however the vector is of fixed length
     */
    private final double[] data;

    /**
     * Creates a new vector of length η with random values
     *
     * @param length the new vector's length
     */
    public Vec(final int length) {
        this.data = new double[length];
        this.randomize();
    }

    /**
     * Creates a new vector of length η with a value
     *
     * @param length       the new vector's length
     * @param preMadeValue the value to fill the vector with e.g. 0
     */
    public Vec(final int length, final double preMadeValue) {
        this.data = new double[length];
        this.applyToElement((integer, aDouble) -> preMadeValue);
    }

    /**
     * Constructs a new vector from an array of values
     *
     * @param data the array of values
     */
    public Vec(final double[] data) {
        this.data = data;
    }

    /**
     * Fills the vector with random values from 0-1
     */
    public void randomize() {
        this.applyToElement(value -> Math.random());
    }

    /**
     * Fills the vector with the value 0
     */
    public void zeros() {
        this.applyToElement(value -> 0.);
    }

    /**
     * Performs an element-wise addition
     *
     * @param scaler the value to add
     */
    public void add(final double scaler) {
        this.applyToElement(value -> value + scaler);
    }

    /**
     * Adds two vectors together, element-wise
     *
     * @param vector the second vector
     */
    public void add(final Vec vector) {
        if (vector.length() != length()) {
            System.err.println("Vector length mismatch. Cannot add vectors.");
            return;
        }
        this.applyToElement((index, value) -> value + vector.get(index));
    }

    /**
     * Copies another vectors values of length η into this vector of length η,
     * if η != η, then the operation will not be performed
     *
     * @param vector the vector to copy the values from
     */
    public void copyFromVector(final Vec vector) {
        System.arraycopy(vector.data, 0, data, 0, vector.length());
    }


    /**
     * Multiplies element-wise by a multiplier
     *
     * @param multiplier fixed value to multiply each element by
     */
    public void multiply(final double multiplier) {
        this.applyToElement(aDouble -> aDouble * multiplier);
    }

    public void multiply(final double[] doubles) {
        if (doubles.length != length()) {
            System.err.println("Vector length mismatch. Cannot multiply vector.");
            return;
        }
        this.applyToElement((index, value) -> value * doubles[index]);
    }


    /**
     * Multiplies two vectors element-wise if this vector is of length η and the supplied
     * vector is of length η, otherwise the operation will not be performed
     *
     * @param vector the vector to multiply the values with
     */
    public void multiply(final Vec vector) {
        if (vector.length() != length()) {
            System.err.println("Vector length mismatch.");
            return;
        }
        this.applyToElement((index, value) -> value * vector.get(index));
    }

    /**
     * @return the vector's max value
     */
    public double max() {
        double max = data[0];
        for (final double v : data) {
            max = Math.max(max, v);
        }
        return max;
    }

    /**
     * Applies the relu function to each element of the vector
     */
    public void relu() {
        this.applyToElement(MathUtil::relu);
    }

    /**
     * Applies the relu's derivative function to each element of the vector
     */
    public void reluDerivative() {
        this.applyToElement(MathUtil::reluDerivative);
    }

    /**
     * Applied the sigmoid function to each element of the vector
     */
    public void sigmoid() {
        this.applyToElement(MathUtil::sigmoid);
    }

    /**
     * Applies the sigmoid's derivative function to each element of the vector
     */
    public void sigmoidDerivative() {
        this.applyToElement(MathUtil::sigmoidDerivative);
    }

    /**
     * Converts the vector into a matrix of size η * 1
     *
     * @return a matrix of size η * 1
     */
    public Matrix toMatrix() {
        final Matrix matrix = new Matrix(length(), 1);
        for (int i = 0; i < matrix.getRows(); i++) {
            matrix.set(i, 0, get(i));
        }
        return matrix;
    }

    /**
     * Pretty prints the vectors contents
     *
     * @see Matrix#print for a similar implementation
     */
    public void print() {
        for (final double d : data) {
            System.out.printf("[ %f ]%n", d);
        }
    }

    /**
     * Sums the vectors contents
     *
     * @return the total sum of all values
     */
    public double sum() {
        return Arrays.stream(data).sum();
    }

    public void exp() {
        this.applyToElement(Math::exp);
    }

    public void applyToElement(final Function<Double, Double> doubleDoubleFunction) {
        for (int i = 0; i < data.length; i++) {
            data[i] = doubleDoubleFunction.apply(data[i]);
        }
    }

    public void applyToElement(final BiFunction<Integer, Double, Double> doubleDoubleFunction) {
        for (int i = 0; i < data.length; i++) {
            data[i] = doubleDoubleFunction.apply(i, data[i]);
        }
    }

    /**
     * Converts the vector's data to a java 8 Stream
     *
     * @return stream from the data array
     */
    public DoubleStream stream() {
        return DoubleStream.of(data);
    }

    /**
     * Sets the data in the array at the given position to the given value
     *
     * @param index index of the value to set
     * @param value value to set
     */
    public void set(final int index, final double value) {
        this.data[index] = value;
    }

    public double get(final int index) {
        return data[index];
    }

    public int length() {
        return data.length;
    }

    public boolean matches(Vec expectedResult) {
        if (expectedResult.length() != length()) {
            return false;
        }
        for (int i = 0; i < length(); i++) {
            if (get(i) != expectedResult.get(i)) {
                return false;
            }
        }
        return true;
    }
}
