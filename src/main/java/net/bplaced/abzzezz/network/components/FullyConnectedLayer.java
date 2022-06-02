package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.Util;
import net.bplaced.abzzezz.util.matrix.Matrix;
import net.bplaced.abzzezz.util.vector.Vec;
import org.jetbrains.annotations.NotNull;

public class FullyConnectedLayer {

    private final Matrix weights;
    private final Vec lastInput;
    private final Vec lastOutput;

    private final int bias = 0;

    public FullyConnectedLayer(final int numNodes, final int numInputs) {
        this.weights = new Matrix(numNodes, numInputs + 1);
        this.lastInput = new Vec(numInputs + 1);
        this.lastOutput = new Vec(numNodes);
        // Set the last value to be the offset. This will never change.
        this.lastInput.set(lastInput.length() - 1, -1);
    }

    public Vec forwardPropagation(final @NotNull Vec input) {
        if (input.length() != lastInput.length() - 1) {
            System.err.printf("Vector length mismatch. @FullyConnectedLayer#computeOutput Input was: %d, expected: %d%n", input.length(), lastInput.length() - 1);
            return null;
        }
        //Copies the supplied input into the attribute "lastInput"
        lastInput.copyFromVector(input);
        /*
        For the length of the last output vector,
        calculate the last inputs sum, from each element multiplied by the corresponding weight
        [n1, n2, n3, ..., nN] * [w1, w2, w3, ..., wN]
         */
        for (int i = 0; i < lastOutput.length(); i++) {
            double sum = 0;
            for (int j = 0; j < lastInput.length() - 1; j++) {
                sum += weights.get(i, j) * lastInput.get(j);
                //    System.out.println(weights.get(i, j) + " * " + lastInput.get(j));
            }
            sum += bias;
            lastOutput.set(i, Util.sigmoid(sum));
        }
        return lastOutput;
    }

    public Vec propagateBackwards(final Vec delta) {

        return null;
    }
}
