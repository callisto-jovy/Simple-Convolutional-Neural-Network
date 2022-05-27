package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.matrix.Matrix;
import org.jetbrains.annotations.NotNull;

public class ConvolutionLayer {

    private Matrix input;
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
        final Matrix paddedInput = input.padMatrix(p);

        final int rowLength = (input.getRows() + (2 * p) - kernel.getRows() + 1);
        final int columnLength = (input.getCols() + (2 * p) - kernel.getCols() + 1);

        final Matrix resultMatrix = new Matrix(rowLength, columnLength);

        for (int i = 0; i + kernel.getRows() < paddedInput.getRows(); i++) {
            for (int j = 0; j + kernel.getCols() < paddedInput.getCols(); j++) {

                final Matrix subMatrix = paddedInput.subMatrix(i, j, kernel.getRows(), kernel.getCols());
                final double res = subMatrix.sumAndMultiply(kernel);

                resultMatrix.setDataAtPos(i, j, res);
            }
        }
        return resultMatrix;
    }

    public Matrix[] forwardPropagation(final @NotNull Matrix input, final @NotNull Matrix[] kernels) {
        this.input = input;
        this.kernels = kernels;
        //  return Arrays.stream(kernels).map(matrix -> convolve(input, matrix)).toArray(Matrix[]::new);
        final Matrix[] matrices = new Matrix[kernels.length];
        for (int i = 0; i < kernels.length; i++) {
            matrices[i] = convolve(input, kernels[i]);
        }
        return matrices;
    }

}
