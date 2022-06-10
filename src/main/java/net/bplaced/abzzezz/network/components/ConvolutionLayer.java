package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.Const;
import net.bplaced.abzzezz.util.math.MathUtil;
import net.bplaced.abzzezz.util.math.matrix.Matrix;
import net.bplaced.abzzezz.util.math.matrix.MatrixUtil;
import org.jetbrains.annotations.NotNull;

public class ConvolutionLayer {

    private Matrix[] input;
    private Matrix[] kernels;

    public Matrix convolve(@NotNull Matrix input, @NotNull Matrix kernel) {
        /*
        If the input is of size n*n and the kernel (or filter) of size ⨍ x ⨍, then the output is of size
        is g(n, ⨍) = (n - ⨍) + 1
        In this case the input is padded (p)
        n becomes: (n + 2p)
        and the output:
        n + 2p - ⨍ + 1
        with p = 0 no padding is given, this is called "same" convolution
        When p = (⨍ - 1) / 2 the input and the output are the same size
        Note that the stride is equal to one
         */
        final int p = (kernel.getRows() - 1) / 2;
        final Matrix paddedInput = input.pad(p);

        final int rowLength = (input.getRows() + (2 * p) - kernel.getRows() + 1);
        final int columnLength = (input.getCols() + (2 * p) - kernel.getCols() + 1);

        final Matrix resultMatrix = new Matrix(rowLength, columnLength);

        for (int i = 0; i + kernel.getRows() < paddedInput.getRows(); i++) {
            for (int j = 0; j + kernel.getCols() < paddedInput.getCols(); j++) {

                final Matrix subMatrix = paddedInput.subMatrix(i, j, kernel.getRows(), kernel.getCols());
                final double res = MatrixUtil.sumAndMultiply(subMatrix, kernel);
                resultMatrix.set(i, j, res);
            }
        }
        return resultMatrix;
    }

    public Matrix[] forwardPropagation(final @NotNull Matrix[] input, final @NotNull Matrix[] kernels) {
        this.input = input;
        this.kernels = kernels;
        //  return Arrays.stream(kernels).map(matrix -> convolve(input, matrix)).toArray(Matrix[]::new);
        final Matrix[] matrices = new Matrix[kernels.length * input.length];

        for (int i = 0; i < kernels.length; i++) {
            for (int j = 0; j < input.length; j++) {
                matrices[i * input.length + j] = convolve(input[j], kernels[i]);
            }
        }
        return matrices;
    }


    public Matrix[] backwardsPropagation(final Matrix[] delta) {
        if (delta.length != input.length * kernels.length) {
            System.err.printf("Matrix size mismatch. @ConvolutionPayer#backwardsPropagation Input was %d, expected %d",
                    delta.length,
                    input.length * kernels.length);
            return null;
        }

        final Matrix[] deltaKernels = new Matrix[kernels.length];
        final Matrix[] deltaInput = new Matrix[input.length];

        for (int i = 0; i < input.length; i++) {
            final Matrix input = this.input[i];
            deltaInput[i] = new Matrix(input.getRows(), input.getCols());
            //Iterate over the input matrix
            for (int row = 0; row + kernels[0].getRows() < input.getRows(); row++) {
                for (int column = 0; column + kernels[0].getCols() < input.getCols(); column++) {
                    //Iterate over all the kernels
                    for (int l = 0; l < kernels.length; l++) {
                        final Matrix kernel = kernels[l];
                        deltaKernels[l] = new Matrix(kernel.getRows(), kernel.getCols());
                        //Iterate over the input regions
                        final Matrix region = input.subMatrix(row, column, kernel.getRows(), kernel.getCols());
                        //Get the delta for the region
                        final double d = delta[i * kernels.length + l].get(row, column);
                        //Multiply the delta with the region
                        region.multiply(d);
                        //Add the result to the deltaKernels
                        deltaKernels[l].add(region);

                        for (int j = 0; j < deltaKernels[l].getRows(); j++) {
                            for (int k = 0; k < deltaKernels[l].getRows(); k++) {
                                deltaInput[i].set(row + j, column + k, deltaKernels[l].get(j, k));
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < kernels.length; i++) {
            final Matrix m1 = MatrixUtil.transpose(deltaKernels[i]);
            kernels[i] = MatrixUtil.addMatrices(kernels[i], m1.multiply(-Const.LEARNING_RATE));
        }
        return deltaInput;
    }
}