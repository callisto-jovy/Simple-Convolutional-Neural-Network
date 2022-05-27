package net.bplaced.abzzezz.util.matrix;
import java.util.Arrays;

public class MatrixUtil {

    /**
     * Subtracts two matrices from one another
     * @param matrix0 the first matrix
     * @param matrix1 the second matrix
     * @return a new matrix created from an element-wise subtraction of the two matrices
     */
    public static Matrix subtractMatrices(final Matrix matrix0, final Matrix matrix1) {
        if (matrix0.getRows() != matrix1.getRows() || matrix0.getCols() != matrix1.getCols()) {
            System.err.println("Matrix size mismatch");
            return null;
        }

        final Matrix temp = new Matrix(matrix0.getRows(), matrix0.getCols());
        for (int i = 0; i < matrix0.getRows(); i++) {
            for (int j = 0; j < matrix0.getCols(); j++) {
                final double value = matrix0.getDataAtPos(i, j) - matrix1.getDataAtPos(i, j);
                temp.setDataAtPos(i, j, value);
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
                    sum += matrix0.getDataAtPos(i, k) * matrix0.getDataAtPos(k, j);
                }
                temp.setDataAtPos(i, j, sum);
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
                temp.setDataAtPos(i, j, matrix0.getDataAtPos(i, j));
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
            temp.setDataAtPos(i, 0, array[i]);
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
                temp.setDataAtPos(i, j, array[i][j]);
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
                entries[i * matrix.getRows() + j] = matrix.getDataAtPos(i, j);
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
     * sums all the elements with in a vector.
     *
     * @param v the input vector.
     * @return the total doubleing sum.
     */
    public static double v_sum(double[][] v) {
        return Arrays.stream(v[0]).sum();
    }

    /**
     * pretty prints a doubling point vector with the specified number of significant figures.
     *
     * @param v       the input vector.
     * @param decimal the number of significant figures applied element-wise.
     */
    public static void v_print(double[] v, int decimal) {
        final String pattern = "%7." + decimal + "f";
        for (final double value : v) {
            System.out.printf(pattern, value);
        }
        System.out.println("");
    }

    /**
     * returns the square magnitude of a vector.
     *
     * @param v the input vector.
     * @return the doubling sum.
     */
    public static double v_sqr_mgn(double[][] v) {
        double sq_sum = 0;
        for (int i = 0; i < v[0].length; i++) {
            sq_sum += v[0][i] * v[0][i];
        }
        return sq_sum;
    }

    /**
     * returns a sub-array from the original matrix with the specified range of indices.
     *
     * @param mat the input matrix.
     * @param r_s start row index.
     * @param r_e end row index.
     * @param c_s start column index.
     * @param c_e end column index.
     * @return
     */
    public static double[][] m_sub(double[][] mat, int r_s, int r_e, int c_s, int c_e) {
        double[][] sub = new double[r_e - r_s + 1][c_e - c_s + 1];
        for (int i = 0; i < sub.length; i++) {
            System.arraycopy(mat[r_s + i], c_s, sub[i], 0, sub[0].length);
        }
        return sub;
    }

    /**
     * performs element-wise multiplication between two matrices and sums the result.
     *
     * @param mat1 the first input matrix.
     * @param mat2 the second input matrix.
     * @return the final sum of the product.
     */
    public static double mm_elsum(double[][] mat1, double[][] mat2) {
        double sum = 0;
        for (int i = 0; i < mat1.length; i++) {
            for (int j = 0; j < mat2[0].length; j++) {
                sum += mat1[i][j] * mat2[i][j];
            }
        }
        return sum;
    }


    /**
     * returns the maximum value from a vector.
     *
     * @param vec the input vector.
     * @return a doubleing value
     */
    public static double v_max(double[][] vec) {
        double max = vec[0][0];
        for (int i = 0; i < vec[0].length; i++) {
            max = Math.max(max, vec[0][i]);
        }
        return max;
    }

    /**
     * returns the index of the maximum value within a vector.
     *
     * @param vec the input vector.
     * @return an integer that corresponds to the index.
     */
    public static double v_argmax(double[][] vec) {
        int arg = 0;
        for (int i = 0; i < vec[0].length; i++) {
            arg = vec[0][arg] < vec[0][i] ? i : arg;
        }
        return arg;
    }

    //

    /**
     * pretty prints the shape of a 2D array.
     *
     * @param mat the input matrix.
     */
    public static void m_size(double[][] mat) {
        System.out.println("" + mat.length + " X " + mat[0].length);
    }

    /**
     * flattens a 2D array of shape [m] X [n] into a 2D array of shape
     * [1][m*n]
     *
     * @param mat the input matrix.
     * @return a 2D array of shape [1][m*n*p]
     */
    public static double[][] m_flatten(double[][] mat) {
        final double[][] v = new double[1][mat.length * mat[0].length];
        int k = 0; //vector iterator
        for (final double[] doubles : mat) {
            for (int j = 0; j < mat[0].length; j++) {
                v[0][k] = doubles[j];
                k++;
            }
        }
        return v;
    }


    /**
     * flattens a 3D array of shape [m] X [n] X [p] into a 2D array of shape
     * [1][m*n*p]
     *
     * @param mat the input matrix.
     * @return a 2D array of shape [1][m*n*p]
     */
    public static double[][] m_flatten(double[][][] mat) {
        final double[][] v = new double[1][mat.length * mat[0].length * mat[0][0].length];
        int l = 0; //vector iterator
        for (final double[][] doubles : mat) {
            for (int j = 0; j < mat[0].length; j++) {
                for (int k = 0; k < mat[0][0].length; k++) {
                    v[0][l] = doubles[j][k];
                    l++;
                }
            }
        }
        return v;
    }

    /**
     * converts a 1D java array into a row vector of the same size with the extra
     * dimension. i.e. [n] ----> [1][n].
     *
     * @param input the input 1D vector.
     * @return a 2D array with shape [1]X[n].
     */
    public static double[][] m_row(double[] input) {
        double[][] result = new double[1][input.length];
        System.arraycopy(input, 0, result[0], 0, input.length);
        return result;
    }

    /**
     * converts a 1D java array into a column vector of the same size with the extra
     * dimension. i.e. [m] ----> [m][1].
     *
     * @param input the input 1D vector.
     * @return a 2D array with shape [m]X[1].
     */
    public static double[][] m_column(double[] input) {
        double[][] result = new double[input.length][1];
        for (int i = 0; i < input.length; i++) {
            result[i][0] = input[i];
        }
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