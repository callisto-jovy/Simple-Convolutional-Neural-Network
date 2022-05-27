package net.bplaced.abzzezz.util.matrix;

import net.bplaced.abzzezz.util.vector.Vec;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

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

    /**
     * Each element is assigned a random value between 0 and 1
     */
    public void randomize() {
        this.applyToElement(value -> Math.random());
    }

    /**
     * Fills the matrix with all zeros
     */
    public void fillZeros() {
        this.applyToElement(value -> 0.);
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
        this.applyToElement((row, col) -> data[row][col] + matrix.getDataAtPos(row, col));
    }

    public Matrix padMatrix(final int paddingSize) {
        final Matrix matrix = new Matrix(getRows() + (paddingSize * 2), getCols() + (paddingSize * 2));
        /*
         * Old:
         * [1, 1, 1]
         * [1, 1, 1]
         * New:
         * [0, 0, 0, 0, 0]
         * [0, 1, 1, 1, 0]
         * [0, 1, 1, 1, 0]
         * [0, 0, 0, 0, 0]
         */
        //Fill the new matrix with zeros
        matrix.fillZeros();
        for (int i = 0; i < getRows(); i++) {
            for (int j = 0; j < getCols(); j++) {
                final double data = getDataAtPos(i, j);
                matrix.setDataAtPos(i + paddingSize, j + paddingSize, data);
            }
        }
        return matrix;
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
                max = Math.max(max, getDataAtPos(i, j));
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
        this.applyToElement((row, col) -> data[row][col] *= matrix.getDataAtPos(row, col));
    }

    /**
     * Multiply the matrix element-wise by a given multiplier
     *
     * @param multiplier static multiplier for each element
     */
    public void multiply(final int multiplier) {
        this.applyToElement(value -> value * multiplier);
    }

    public void sigmoid() {
        this.applyToElement(value -> 1 / (1 + Math.exp(-value)));
    }

    public Matrix dsigmoid() {
        final Matrix temp = new Matrix(rows, cols);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                final double value = getDataAtPos(i, j) * (1 - getDataAtPos(i, j));
                temp.setDataAtPos(i, j, value);
            }
        }
        return temp;
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
                sum += getDataAtPos(i, j);
            }
        }
        return sum;
    }

    /**
     * Returns the sum of two matrices multiplied element-wise
     * @param matrix another matrix, the same size as the current matrix
     * @return the sum of one matrix's elements multiplied by the other matrices element-wise
     */
    public double sumAndMultiply(final Matrix matrix) {
        if (matrix.rows != rows || matrix.cols != cols) {
            System.err.println("Matrix mismatch");
            return -1;
            //throw new RuntimeException("Matrix mismatch");
        }
        double sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                sum += getDataAtPos(i, j) * matrix.getDataAtPos(i, j);
            }
        }
        return sum;
    }

    /**
     * Creates a sub-matrix from the given starting positions with a new height and width
     * @param row row to start at
     * @param col column to start at
     * @param width the new matrix's width, i.e. the new matrix's rows
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
                final double data = getDataAtPos(i, j);
                temp.setDataAtPos(i1, j1, data);
            }
        }
        return temp;
    }
    /**
     * Pretty prints the matrix
     */
    public void print() {
        for (int i = 0; i < getCols(); i++) {
            for (int j = 0; j < getRows(); j++) {
                System.out.printf("%,.7g", getDataAtPos(j, i));
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
                vec.setDataAtPos(index, value);
            }
        }
        return vec;
    }

    /**
     * Applies a given function to each element
     *
     * @param function Function which takes in a double (the value at the position) and returns a new double which in turn is the elements new value at the particular position
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

    public void setDataAtPos(final int row, final int col, final double data) {
        this.data[row][col] = data;
    }

    public double getDataAtPos(final int row, final int col) {
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
