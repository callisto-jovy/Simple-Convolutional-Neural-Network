package net.bplaced.abzzezz.util.math.matrix;

import net.bplaced.abzzezz.util.Const;
import net.bplaced.abzzezz.util.math.MathUtil;
import net.bplaced.abzzezz.util.math.vector.Vec;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.bplaced.abzzezz.util.Const.RANDOM;

public class Matrix {
    /**
     * the matrix's data:
     * Row -->   Column
     * [0, 0, 0]   ↓
     * [0, 0, 0]   ↓
     * ↓
     */
    private final double[][] data;
    private final int rows;
    private final int cols;

    /**
     * Initialize a new matrix with all random values
     *
     * @param rows the matrix's desired rows
     * @param cols the matrix's desired columns
     */
    public Matrix(final int rows, final int cols) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
        this.randomize();
    }

    public Matrix(final int rows, final int cols, final double value) {
        this.rows = rows;
        this.cols = cols;
        this.data = new double[rows][cols];
        this.fill(value);
    }

    /**
     * Each element is assigned a random value between 0 and 1
     */
    public void randomize() {
        this.applyToElement(value -> RANDOM.nextGaussian());
    }

    public void randomizeInteger() {
        this.applyToElement(value -> (double) ThreadLocalRandom.current().nextInt(5));
    }

    /**
     * Fills the matrix with all zeros
     */
    public void fillZeros() {
        this.applyToElement(value -> 0.);
    }

    /**
     * Fills the matrix with a supplied value
     *
     * @param value the value to fill the matrix with
     */
    public void fill(final double value) {
        this.applyToElement((integer) -> value);
    }

    /**
     * Element-wise add a number
     *
     * @param scaler number to add to each element
     */
    public void add(final double scaler) {
        this.applyToElement(value -> value + scaler);
    }

    /**
     * Element-wise add two matrices
     *
     * @param matrix to add with
     */
    public void add(final Matrix matrix) {
        if (matrix.rows != rows || matrix.cols != cols) {
            System.err.println("Matrix mismatch");
            return;
            //throw new RuntimeException("Matrix mismatch");
        }
        this.applyToElement((row, col) -> data[row][col] + matrix.get(row, col));
    }

    /**
     * Creates a padded matrix from the current matrix with a specified padding size
     * Example: A 2*2 Matrix filled with the value 1
     * Old:
     * [1, 1, 1]
     * [1, 1, 1]
     * New surrounded by zeros:
     * [0, 0, 0, 0, 0]
     * [0, 1, 1, 1, 0]
     * [0, 1, 1, 1, 0]
     * [0, 0, 0, 0, 0]
     *
     * @param paddingSize the amount of padding applied around the matrix's values
     * @return a new matrix with the current matrix's padded
     */
    public Matrix pad(final int paddingSize) {
        final Matrix matrix = new Matrix(getRows() + (paddingSize * 2), getCols() + (paddingSize * 2));
        //Fill the new matrix with zeros
        matrix.fillZeros();
        //Transfer the current matrix's values into the new matrix, which are then surrounded by padding,
        //See the example above
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                final double data = get(i, j);
                matrix.set(i + paddingSize, j + paddingSize, data);
            }
        }
        return matrix;
    }

    public Matrix subtract(final Matrix matrix) {
        return MatrixUtil.subtractMatrices(this, matrix);
    }

    /**
     * Element-wise exp
     */
    public void exp() {
        this.applyToElement(Math::exp);
    }

    /**
     * @return the matrix's max value
     */
    public double maxValue() {
        double max = data[0][0];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                max = Math.max(max, get(i, j));
            }
        }
        return max;
    }

    /**
     * Multiplies this matrix with a supplied matrix element-wise
     *
     * @param matrix another matrix whose elements are to be multiplied with
     */
    public void multiply(final Matrix matrix) {
        if (matrix.rows != rows || matrix.cols != cols) {
            System.err.println("Matrix mismatch. Cannot multiply matrices");
            return;
        }
        this.applyToElement((row, col) -> data[row][col] * matrix.get(row, col));
    }

    /**
     * Multiply the matrix element-wise by a given multiplier
     *
     * @param multiplier static multiplier for each element
     */
    public void multiply(final double multiplier) {
        this.applyToElement(value -> value * multiplier);
    }

    public void sigmoid() {
        this.applyToElement(MathUtil::sigmoid);
    }

    public void sigmoidDerivative() {
        this.applyToElement(MathUtil::sigmoidDerivative);
    }

    public void relu() {
        this.applyToElement(MathUtil::relu);
    }

    public void reluDerivative() {
        this.applyToElement(MathUtil::reluDerivative);
    }

    /**
     * Calculates the sum of all elements; the rows are iterated and the sum of each column is
     * in turn added
     *
     * @return the sum of all elements
     */
    public double sum() {
        double sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum += get(i, j);
            }
        }
        return sum;
    }

    /**
     * Returns the sum of two matrices multiplied element-wise
     *
     * @param matrix another matrix, the same size as the current matrix
     * @return the sum of one matrix's elements multiplied by the other matrices element-wise
     */
    public double sumAndMultiply(final Matrix matrix) {
        if (matrix.rows != rows || matrix.cols != cols) {
            System.err.println("Matrix mismatch. Cannot multiply matrices and sum the result.");
            return -1;
        }
        double sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum += get(i, j) * matrix.get(i, j);
            }
        }
        return sum;
    }

    /**
     * Creates a sub-matrix from the given starting positions with a new height and width
     *
     * @param row    row to start at
     * @param col    column to start at
     * @param width  the new matrix's width, i.e. the new matrix's rows
     * @param height the new matrix's height, i.e. the nw matrix's columns
     * @return a sub-matrix constructed from the current matrix or null if the requested sub-matrix's bounds
     * exceed the current matrix's boundaries
     */
    public Matrix subMatrix(final int row, final int col, final int width, final int height) {
        if (row + width > getRows() || col + height > getCols()) {
            System.err.println("Requested sub-matrix width or height exceeds the matrix's bounds.");
            return null;
        }
        final Matrix temp = new Matrix(width, height);

        for (int i = row, i1 = 0; i < row + width; i++, i1++) {
            for (int j = col, j1 = 0; j < col + height; j++, j1++) {
                final double data = get(i, j);
                temp.set(i1, j1, data);
            }
        }
        return temp;
    }

    /**
     * Searches the matrix for a specific value and returns the row and column of the first occurrence
     *
     * @param value the value to search for
     * @return an array containing the row and column of the first occurrence of the value,
     * or null if the value is not found, wrapped in an optional
     */
    public Optional<int[]> indexOfValue(final double value) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (get(i, j) == value) {
                    return Optional.of(new int[]{i, j});
                }
            }
        }
        return Optional.empty();
    }



    /**
     * Pretty prints the matrix
     */
    public void print() {
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                System.out.printf("%f \t", data[i][j]);
            }
            System.out.println();
        }
    }

    /**
     * Converts the matrix to a new vector
     *
     * @return vec with the matrix's data
     */
    public Vec toVec() {
        final Vec vec = new Vec(rows * cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final double value = data[i][j];
                final int index = i * rows + j;
            /*
                System.out.println("index = " + index);
                System.out.println("i = " + i);
                System.out.println("rows = " + rows);
                System.out.println("j = " + j);
                System.out.println("cols = " + cols);

             */

                vec.set(index, value);
            }
        }
        return vec;
    }

    /**
     * Applies a given function to each element
     *
     * @param function Function which takes in a double (the value at the position)
     *                 and returns a new double which in turn is the elements new value at the particular position
     */
    private void applyToElement(final Function<Double, Double> function) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] = function.apply(data[i][j]);
            }
        }
    }

    private void applyToElement(final BiFunction<Integer, Integer, Double> function) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.data[i][j] = function.apply(i, j);
            }
        }
    }

    /**
     * @return the matrix's rows
     * @prarm row the row's index
     */
    public double[] getRow(final int row) {
        return data[row];
    }

    /**
     * @param column the column to get
     * @return the matrix's columns
     */
    public double[] getColumn(final int column) {
        final double[] columnData = new double[rows];
        for (int i = 0; i < rows; i++) {
            columnData[i] = data[i][column];
        }
        return columnData;
    }

    public void set(final int row, final int col, final double data) {
        this.data[row][col] = data;
    }

    public double get(final int row, final int col) {
        return this.data[row][col];
    }

    public double[][] getData() {
        return data;
    }

    public int getCols() {
        return cols;
    }

    public int getRows() {
        return rows;
    }
}
