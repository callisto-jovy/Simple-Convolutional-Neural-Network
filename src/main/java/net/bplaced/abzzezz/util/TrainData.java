package net.bplaced.abzzezz.util;

import net.bplaced.abzzezz.util.math.matrix.Matrix;

public class TrainData {

    private final Matrix input;
    private final double expectedResult;

    public TrainData(Matrix input, double expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    public Matrix getInput() {
        return input;
    }

    public double getExpectedResult() {
        return expectedResult;
    }
}
