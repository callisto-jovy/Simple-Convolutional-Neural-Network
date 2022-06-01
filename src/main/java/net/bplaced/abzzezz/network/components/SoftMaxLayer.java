package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.matrix.Matrix;
import net.bplaced.abzzezz.util.vector.Vec;

public class SoftMaxLayer {

    private Matrix weights;
    private Vec bias;

    private Matrix lastOutput;

    private Matrix lastInput;

    public SoftMaxLayer(final int input, final int output) {
        this.weights = new Matrix(input, output);
        this.weights.randomize();
        this.weights.multiply(1.0 / input);
        this.bias = new Vec(input, 0);
    }

    public Matrix forwardPropagation(final Matrix input) {
        //"Flatten" the matrix to a vector
        this.lastInput = input;
        //Multiply the input by the weights
        input.multiply(weights);
        input.add(bias.toMatrix());
        this.lastOutput = input; //Total activation value

        final Matrix temp = lastOutput;
        temp.exp();
        final double inverseActivationSum = 1. / temp.sum();

        temp.multiply(inverseActivationSum);
        return temp;
    }


}
