package net.bplaced.abzzezz.network.components;

import net.bplaced.abzzezz.util.Util;
import net.bplaced.abzzezz.util.matrix.Matrix;
import net.bplaced.abzzezz.util.vector.Vec;

public class SoftMaxLayer {
    private Vec bias;

    private Vec lastOutput;

    private Vec lastInput;

    public SoftMaxLayer(final int input, final int output) {
        this.bias = new Vec(output, 0);
        this.lastOutput = new Vec(output);
        this.lastInput = new Vec(input);
    }

    public Vec forwardPropagation(final Vec input) {
        //Softmax
        if (input.length() != lastInput.length()) {
            System.err.printf("Vector length mismatch. @SoftMaxLayer#computeOutput Input was: %d, expected: %d%n", input.length(), lastInput.length());
            return null;
        }
        lastInput.copyFromVector(input);
        for (int i = 0; i < lastOutput.length(); i++) {
            double sum = 0;
            for (int j = 0; j < lastInput.length(); j++) {
                sum += Math.exp(lastInput.get(j) + bias.get(i));
            }
            lastOutput.set(i, Math.exp(lastInput.get(i) + bias.get(i)) / sum);
        }
        return lastOutput;
    }


}
