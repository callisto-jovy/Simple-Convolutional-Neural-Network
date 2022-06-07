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
                //System.out.println((int) subMatrix.maxValue());
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
        if (delta[0].getRows() != output[0].getRows() || delta[0].getCols() != output[0].getCols()) {
            System.err.printf("Matrix size mismatch. @PoolingPayer#backwardsPropagation Input was %d, expected %d",
                    delta.length,
                    inputs.length);
            return null;
        }
        final int p = 2;
        final int s = 2;
        //New inputs
        final Matrix[] newInputs = new Matrix[inputs.length];
        //Iterate over all inputs
        for (int i = 0; i < inputs.length; i++) {
            final Matrix input = inputs[i];
            newInputs[i] = new Matrix(input.getRows(), input.getCols());
            newInputs[i].fillZeros();

            for (int j = 0; j + p < input.getRows(); j += s) {
                for (int k = 0; k + p < input.getCols(); k += s) {
                    //Same size matrix, as the output
                    final Matrix subMatrix = input.subMatrix(j, k, p, p);
                    for (int l = 0; l < delta.length; l++) {
                        final Matrix deltaMatrix = delta[l];
                        //Find the max value in the submatrix
                        final double maxValue = subMatrix.maxValue();
                        //Find the index of the max value
                        final int[] maxIndex = subMatrix.indexOfValue(maxValue).orElseThrow(() -> new IllegalStateException("No max value found"));
                        //Set the value of the submatrix to the delta value
                        subMatrix.set(maxIndex[0], maxIndex[1], deltaMatrix.get(j / s + 1, k / s + 1));
                    }

                }
            }
        }
        return newInputs;
    }
    /**
     //TODO: Rework (translate from delta as a matrix to input as a matrix)
     public Matrix[] backwardsPropagation(final Matrix[] delta) {
     System.out.println(delta.length);
     if (delta[0].getRows() != output[0].getRows() || delta[0].getCols() != output[0].getCols()) {
     System.err.printf("Matrix size mismatch. @PoolingPayer#backwardsPropagation Input was %d, expected %d",
     delta.length,
     inputs.length);
     return null;
     }
     final Matrix[] deltas = new Matrix[delta.length];

     for (int i = 0; i < delta.length; i++) {
     final Matrix input = inputs[i];
     deltas[i] = new Matrix(input.getRows(), input.getCols());
     deltas[i].fillZeros();

     final int p = 2;
     final int s = 2;

     for (int j = 0; j + p < input.getRows(); j += s) {
     for (int k = 0; k + p < input.getCols(); k += s) {
     final int row = (j - p) / s + 1;
     final int column = (k - p) / s + 1;
     final int finalJ = j;
     final int finalK = k;

     final double value = delta[i].get(row, column);
     final Matrix subMatrix = input.subMatrix(j, k, p, p);

     final int[] maxIndex = subMatrix.indexOfValue(subMatrix.maxValue())
     .map(index -> new int[]{index[0] + finalJ, index[1] + finalK})
     .orElseThrow(() -> new IllegalStateException("No max value found"));

     deltas[i].set(maxIndex[0], maxIndex[1], value);
     }
     }
     }
     return deltas;
     }
     */
}
