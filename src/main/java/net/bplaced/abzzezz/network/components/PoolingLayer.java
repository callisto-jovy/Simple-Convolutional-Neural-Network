package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.matrix.Matrix;
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

        for (int i = 0; i + p <= input.getRows(); i += s) {
            for (int j = 0; j + p <= input.getCols(); j += s) {
                final Matrix subMatrix = input.subMatrix(i, j, p, p);

                matrix.set((i - p) / 2 + 1, (j - p) / 2 + 1, subMatrix.maxValue());
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
}
