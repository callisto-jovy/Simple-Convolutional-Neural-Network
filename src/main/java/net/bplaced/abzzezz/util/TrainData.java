package net.bplaced.abzzezz.util;

import net.bplaced.abzzezz.util.math.matrix.Matrix;
import net.bplaced.abzzezz.util.math.vector.Vec;

public class TrainData {

    private final Matrix input;
    private final Vec expectedResult;

    public TrainData(Matrix input, Vec expectedResult) {
        this.input = input;
        this.expectedResult = expectedResult;
    }

    public Matrix getInput() {
        return input;
    }

    public Vec getExpectedResult() {
        return expectedResult;
    }
}
