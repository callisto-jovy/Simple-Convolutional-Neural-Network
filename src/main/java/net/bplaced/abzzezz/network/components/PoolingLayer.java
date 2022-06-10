package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.math.matrix.Matrix;
import org.jetbrains.annotations.NotNull;

public class PoolingLayer {

    private Matrix[] inputs;
    private Matrix[] output;


    public Matrix maxPool(@NotNull final Matrix input) {
        //Pooling size = 2
        final int p = 2;
        //Stride of 2
        final int s = 2;
        /*
        Dimensions of the new output matrix:
        with one channel: 128x128 -> 64x64
         */
        final int rowLength = (input.getRows() - p) / s + 1;
        final int columnLength = (input.getCols() - p) / s + 1;
        final Matrix matrix = new Matrix(rowLength, columnLength);

        for (int i = 0; i + p < input.getRows(); i += s) {
            for (int j = 0; j + p < input.getCols(); j += s) {
                final Matrix subMatrix = input.subMatrix(i, j, p, p);

                matrix.set((i - p) / s + 1, (j - p) / s + 1, subMatrix.maxValue());
            }
        }
        return matrix;
    }

    public Matrix[] forwardPropagation(final Matrix[] matrices) {
        this.inputs = matrices;
        final Matrix[] output = new Matrix[matrices.length];

        for (int i = 0; i < matrices.length; i++) {
            output[i] = maxPool(matrices[i]);
        }
        this.output = output;
        return output;
    }

    public Matrix[] backwardsPropagation(final Matrix[] delta) {
        if (output.length != delta.length)
            throw new IllegalArgumentException("Matrix size mismatch. @PoolingLayer#backwardsPropagation Input was " + delta.length + ", expected " + output.length);

        final int p = 2;
        final int s = 2;
        //New inputs
        final Matrix[] newInputs = new Matrix[inputs.length];

        for (int i = 0; i < inputs.length; i++) {
            final Matrix input = inputs[i];
            newInputs[i] = new Matrix(input.getRows(), input.getCols());
            newInputs[i].fillZeros();

            for (int l = 0; l < delta.length; l++) {
                for (int r = 0; r + p < input.getRows(); r += s) {
                    for (int c = 0; c + p < input.getCols(); c += s) {
                        //Same size matrix, as the output
                        final Matrix subMatrix = input.subMatrix(r, c, p, p);
                        final Matrix deltaMatrix = delta[l];
                        //Find the max value in the submatrix
                        final double maxValue = subMatrix.maxValue();
                        //Find the index of the max value
                        final int[] maxIndex = subMatrix.indexOf(maxValue).orElse(new int[]{0, 0});
                        //Set the value of the submatrix to the delta value
                        newInputs[i].set(r + maxIndex[0], c + maxIndex[1], deltaMatrix.get(maxIndex[0], maxIndex[1]));
                        //newInputs[i].set(maxIndex[0] + r, maxIndex[1] + c, deltaMatrix.get(row, column));
                    }
                }
            }
        }
        return newInputs;
    }
}
