package net.bplaced.abzzezz.util.vector;

import net.bplaced.abzzezz.util.matrix.Matrix;

import java.util.function.BiFunction;
import java.util.function.Function;

public class Vec {

    private final double[] data;

    public Vec(final int length) {
        this.data = new double[length];
    }

    public void randomize() {
        this.applyToElement(value -> Math.random());
    }

    public void zeros() {
        this.applyToElement(value -> 0.);
    }

    public void add(final double scaler) {
        this.applyToElement(value -> value + scaler);
    }

    public void add(final Vec vector) {
        if (vector.length() != length()) {
            System.err.println("Vector length mismatch.");
            return;
        }
        this.applyToElement((index, value) -> value + vector.getValue(index));
    }

    public void copyFromVector(final Vec vector) {
        System.arraycopy(vector.data, 0, data, 0, vector.length());
    }

    public void multiply(final double multiplicand) {
        this.applyToElement(aDouble -> aDouble * multiplicand);
    }

    public void multiply(final Vec vector) {
        if (vector.length() != length()) {
            System.err.println("Vector length mismatch.");
            return;
        }
        this.applyToElement((index, value) -> value * vector.getValue(index));
    }

    public double max() {
        double max = data[0];
        for (final double v : data) {
            max = Math.max(max, v);
        }
        return max;
    }

    public void sigmoid() {
        this.applyToElement(value -> 1 / (1 + Math.exp(-value)));
    }

    public Vec dsigmoid() {
        final Vec temp = new Vec(length());
        for (int i = 0; i < temp.length(); i++) {
            temp.setDataAtPos(i, data[i] * (1 - data[i]));
        }
        return temp;
    }

    public Matrix toMatrix() {
        final Matrix matrix = new Matrix(length(), 1);
        for (int i = 0; i < matrix.getRows(); i++) {
            matrix.setDataAtPos(i, 1, getValue(i));
        }
        return matrix;
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

    public void setDataAtPos(final int index, final double value) {
        this.data[index] = value;
    }

    public double getValue(final int index) {
        return data[index];
    }

    public int length() {
        return data.length;
    }

}
