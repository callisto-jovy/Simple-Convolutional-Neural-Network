package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.Util;
import net.bplaced.abzzezz.util.matrix.Matrix;
import net.bplaced.abzzezz.util.vector.Vec;
import org.jetbrains.annotations.NotNull;

public class FullyConnectedLayer {

    private final Matrix weights;
    private Vec lastInput;
    private final Vec lastOutput;

    public FullyConnectedLayer(final int numNodes, final int numInputs) {
        this.weights = new Matrix(numNodes, numInputs + 1);
        this.weights.randomize();
        //TODO: Check if this is right
        this.lastInput = new Vec(weights.getCols());
        this.lastOutput = new Vec(weights.getRows());
        // Set the last value to be the offset. This will never change.
        this.lastInput.set(lastInput.length() - 1, -1);
    }

    public Vec forwardPropagation(final @NotNull Vec input) {
        if (input.length() != lastInput.length() - 1) {
            System.err.printf("Vector length mismatch. @FullyConnectedLayer#computeOutput Input was: %d, expected: %d%n", input.length(), lastInput.length());
            return null;
        }
        //Copies the supplied input into the attribute "lastInput"
        //lastInput.copyFromVector(input);
        lastInput = input;
        /*
        For the length of the last output vector,
        calculate the last inputs sum, from each element multiplied by the corresponding weight
         */
        for (int i = 0; i < lastOutput.length(); i++) {
            double sum = 0;
            for (int j = 0; j < lastInput.length(); j++) {
                sum += weights.getDataAtPos(i, j) * lastInput.getValue(j);
            }
            lastOutput.set(i, Util.sigmoid(sum));
        }
        return lastOutput;
    }

    public Vec propagateBackwards(final Vec delta) {

        return null;
    }
}
