package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.Util;
import net.bplaced.abzzezz.util.vector.Vec;
import org.jetbrains.annotations.NotNull;

public class FullyConnectedLayer {

    private final double[][] weights;
    private final Vec lastInput;
    private final Vec lastOutput;

    public FullyConnectedLayer(final double[] @NotNull [] weights) {
        this.weights = weights;
        this.lastInput = new Vec(weights[0].length);
        this.lastOutput = new Vec(weights.length);
        // Set the last value to be the offset. This will never change.
        this.lastInput.setDataAtPos(lastInput.length() - 1, -1);
    }

    public Vec computeOutput(final @NotNull Vec input) {
        if (input.length() != lastInput.length() - 1) {
            System.err.printf("Input length in fully connected layer wad %d, though it should be %d%n",
                    input.length(),
                    lastInput.length());
            return null;
        }
        //Copies the supplied input into the attribute "lastInput"
        lastInput.copyFromVector(input);
        /*
        For the length of the last output vector,
        calculate the last inputs sum, from each element multiplied by the corresponding weight
         */
        for (int i = 0; i < lastOutput.length(); i++) {
            double sum = 0;
            for (int j = 0; j < lastInput.length(); j++) {
                sum += weights[i][j] * lastInput.getValue(j);
            }
            lastOutput.setDataAtPos(i, Util.sigmoid(sum));
        }
        return lastOutput;
    }

    public Vec propagateBackwards(final Vec dout) {
        return null;
    }
}
