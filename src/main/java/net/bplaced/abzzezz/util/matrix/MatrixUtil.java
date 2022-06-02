package net.bplaced.abzzezz.util.matrix;
import java.util.Arrays;

public class MatrixUtil {

    /**
     * Subtracts two matrices from one another
     * @param matrix0 the first matrix
     * @param matrix1 the second matrix
     * @return a new matrix created from an element-wise subtraction of the two matrices
     * @see Matrix#subtract(Matrix) 
     */
    public static Matrix subtractMatrices(final Matrix matrix0, final Matrix matrix1) {
        if (matrix0.getRows() != matrix1.getRows() || matrix0.getCols() != matrix1.getCols()) {
            System.err.println("Matrix size mismatch");
            return null;
        }

        final Matrix temp = new Matrix(matrix0.getRows(), matrix0.getCols());
        for (int i = 0; i < matrix0.getRows(); i++) {
            for (int j = 0; j < matrix0.getCols(); j++) {
                final double value = matrix0.get(i, j) - matrix1.get(i, j);
                temp.set(i, j, value);
            }
        }
        return temp;
    }

    /**
     * Performs naive matrix multiplication between two matrices of shape [k][l] and [l][m]
     *
     * @param matrix0 the first matrix
     * @param matrix1 the second matrix
     * @return a new matrix with each element being the result of an element-wise multiplication
     */
    public static Matrix multiplyMatrices(final Matrix matrix0, final Matrix matrix1) {
        if (matrix0.getRows() != matrix1.getRows() || matrix0.getCols() != matrix1.getCols()) {
            System.err.println("Matrix size mismatch");
            return null;
        }

        final Matrix temp = new Matrix(matrix0.getRows(), matrix0.getCols());
        for (int i = 0; i < temp.getRows(); i++) {
            for (int j = 0; j < temp.getCols(); j++) {
                double sum = 0;
                for (int k = 0; k < matrix0.getCols(); k++) {
                    sum += matrix0.get(i, k) * matrix0.get(k, j);
                }
                temp.set(i, j, sum);
            }
        }
        return temp;
    }

    /**
     * Transposes a matrix to a new one
     * @param matrix0 matrix to transpose from
     * @return the new matrix
     */
    public static Matrix transpose(final Matrix matrix0) {
        final Matrix temp = new Matrix(matrix0.getRows(), matrix0.getCols());
        for (int i = 0; i < matrix0.getRows(); i++) {
            for (int j = 0; j < matrix0.getCols(); j++) {
                temp.set(i, j, matrix0.get(i, j));
            }
        }
        return temp;
    }

    /**
     * Creates a new matrix from a one-dimensional array of doubles
     * @param array a double array to convert from
     * @return a matrix constructed from the given array
     * with one colum e.g.: [  data  ]
     */
    public static Matrix from1DArray(final double[] array) {
        final Matrix temp = new Matrix(array.length, 1);
        for (int i = 0; i < array.length; i++) {
            temp.set(i, 0, array[i]);
        }
        return temp;
    }

    /**
     * Converts a two-dimensional array of doubles to a matrix
     * @param array double array
     * @return a matrix constructed from the array of doubles
     */
    public static Matrix from2DArray(final double[][] array) {
        final Matrix temp = new Matrix(array.length, array[0].length);
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                temp.set(i, j, array[i][j]);
            }
        }
        return temp;
    }

    /**
     * Converts a matrix to a one-dimensional array
     * @param matrix matrix to convert
     * @return the matrix's data reduced to a one-dimenstional array of doubles
     */
    public static double[] matrixToArray(final Matrix matrix) {
        final double[] entries = new double[matrix.getRows() * matrix.getCols()];

        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getCols(); j++) {
                entries[i * matrix.getRows() + j] = matrix.get(i, j);
            }
        }
        return entries;
    }

    /**
     * creates a vector of size and assigns the specified value to every element.
     *
     * @param size  size of the vector.
     * @param value the value to be assigned to every element.
     * @return a 2D array of size [1][size].
     */
    public static double[][] v_assign(int size, double value) {
        final double[][] result = new double[1][size];
        Arrays.fill(result[0], value);
        return result;
    }


    /**
     * reorganizes a matrix into the desired 3D matrix of shape [d][h][w].
     * the size of the input and d*h*w must be the equal.
     *
     * @param input the input matrix.
     * @param d     the depth of the reshaped matrix.
     * @param h     the row size of the reshaped matrix.
     * @param w     the column size of the reshaped matrix.
     * @return
     */
    public static double[][][] reshape(double[][] input, int d, int h, int w) {
        //input --> [1Xn]  output --> [d][h][w]
        double[][][] output = new double[d][h][w];
        int input_index = 0;
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < w; k++) {
                    output[i][j][k] = input[0][input_index];
                    input_index++;
                }
            }
        }
        return output;
    }


}