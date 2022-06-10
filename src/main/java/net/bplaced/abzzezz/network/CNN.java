package net.bplaced.abzzezz.network;

import net.bplaced.abzzezz.network.components.*;
import net.bplaced.abzzezz.util.TrainData;
import net.bplaced.abzzezz.util.image.ImageLoader;
import net.bplaced.abzzezz.util.image.ImageUtil;
import net.bplaced.abzzezz.util.math.MathUtil;
import net.bplaced.abzzezz.util.math.matrix.Matrix;
import net.bplaced.abzzezz.util.math.vector.Vec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static net.bplaced.abzzezz.util.Const.*;
import static net.bplaced.abzzezz.util.math.matrix.MatrixUtil.initKernels;

public class CNN {

    public static final CNN CONVOLUTIONAL_NEURAL_NETWORK = new CNN();

    final List<Matrix[]> kernelList = new ArrayList<>();
    final List<ConvolutionLayer> convolutionLayers = new ArrayList<>();
    final List<PoolingLayer> poolingLayers = new ArrayList<>();
    final List<FullyConnectedLayer> fullyConnectedLayers = new ArrayList<>();

    public CNN() {
        convolutionLayers.add(new ConvolutionLayer());
        kernelList.add(initKernels(5, 9));
        poolingLayers.add(new PoolingLayer());

        convolutionLayers.add(new ConvolutionLayer());
        kernelList.add(initKernels(10, 5));
        poolingLayers.add(new PoolingLayer());
        /*
        convolutionLayers.add(new ConvolutionLayer());
        kernelList.add(initFilters(5, 3));
        poolingLayers.add(new PoolingLayer());

         */

        /*
        A 64*64 sized image produces, after the given convolutions 12.800 Inputs for the NN,
        for the input image is 64*64, which is then downscaled to 16*16 for second convolution
        the number of layers goes up, from 1 to 5 and then to 50.
        In total this means: 16*16*50 = 12800 inputs for the NN.
         */
        fullyConnectedLayers.add(new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, 12_800, MathUtil.ActivationFunction.RELU));

        for (int j = 0; j < FULLY_CONNECTED_NETWORK_DEPTH; j++) {
            fullyConnectedLayers.add(new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, FULLY_CONNECTED_NETWORK_WIDTH, MathUtil.ActivationFunction.RELU));
        }
        fullyConnectedLayers.add(new FullyConnectedLayer(1, FULLY_CONNECTED_NETWORK_WIDTH, MathUtil.ActivationFunction.SOFTMAX));

    }

    public void test(final int tests) {
        if (convolutionLayers.size() != poolingLayers.size()) {
            throw new IllegalStateException("Convolution and pooling layers must have the same amount of layers");
        }

        int errorCount = 0;
        for (int i = 0; i < tests; i++) {
            System.out.println("Test: " + i);
            try {
                final TrainData trainingData = ImageLoader.getRandomTrainingData();

                final Vec poolingOutputsVec = convolveInput(trainingData);

                System.out.printf("Feeding %d inputs to the network%n", poolingOutputsVec.length());

                Vec output = poolingOutputsVec;
                for (final FullyConnectedLayer fullyConnectedLayer : fullyConnectedLayers) {
                    output = fullyConnectedLayer.forwardPropagation(output);
                }

                if (!output.matches(trainingData.getExpectedResult())) {
                    errorCount++;
                }

                System.out.println("Expected: ");
                trainingData.getExpectedResult().print();
                System.out.println("Actual: ");
                output.print();

            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
        final double accuracy = (tests - errorCount) / (double) tests;
        System.out.println("Accuracy: " + accuracy);
    }

    private Vec convolveInput(final TrainData trainingData) {
        Matrix[] poolingOutputs = new Matrix[]{trainingData.getInput()};

        for (int j = 0; j < convolutionLayers.size(); j++) {
            final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);
            final PoolingLayer poolingLayer = poolingLayers.get(j);
            final Matrix[] kernels = kernelList.get(j);

            //final Matrix[] poolTemp = new Matrix[kernels.length * poolingOutputs.length];

            //For every pooling layer, convolve the input with the kernels and pool the result
            final Matrix[] convolutionOutput = convolutionLayer.forwardPropagation(poolingOutputs, kernels);
            //Copy the current pooling output into the temp array from the current index (k*length)
            //System.arraycopy(poolingOutput, 0, poolTemp, k * poolingOutput.length, poolingOutput.length);
            poolingOutputs = poolingLayer.forwardPropagation(convolutionOutput);
        }
        //Create a vector the size off all matrices
        final int vectorSize = Arrays.stream(poolingOutputs)
                .mapToInt(value -> value.getRows() * value.getCols())
                .sum();
        //Add every matrix's elements to the vector, thereby flatten the output
        final Vec poolingOutputsVec = new Vec(vectorSize);
        for (int j = 0; j < poolingOutputs.length; j++) {
            final Matrix matrix = poolingOutputs[j];
            for (int k = 0; k < matrix.getRows(); k++) {
                for (int l = 0; l < matrix.getCols(); l++) {
                    poolingOutputsVec.set(j * matrix.getRows() + l, matrix.get(k, l));
                }
            }
        }
        return poolingOutputsVec;
    }

    public void train(final int iterations) {
        if (convolutionLayers.size() != poolingLayers.size()) {
            throw new IllegalStateException("Convolution and pooling layers must have the same amount of layers");
        }

        for (int i = 0; i < iterations; i++) {
            System.out.println("Training iteration: " + i);
            try {
                final TrainData trainingData = ImageLoader.getRandomTrainingData();
                Matrix[] poolingOutputs = new Matrix[]{trainingData.getInput()};

                for (int j = 0; j < convolutionLayers.size(); j++) {
                    final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);
                    final PoolingLayer poolingLayer = poolingLayers.get(j);
                    final Matrix[] kernels = kernelList.get(j);

                    //final Matrix[] poolTemp = new Matrix[kernels.length * poolingOutputs.length];

                    //For every pooling layer, convolve the input with the kernels and pool the result
                    final Matrix[] convolutionOutput = convolutionLayer.forwardPropagation(poolingOutputs, kernels);
                    //Copy the current pooling output into the temp array from the current index (k*length)
                    //System.arraycopy(poolingOutput, 0, poolTemp, k * poolingOutput.length, poolingOutput.length);
                    poolingOutputs = poolingLayer.forwardPropagation(convolutionOutput);
                }
                //Create a vector the size off all matrices
                final int vectorSize = Arrays.stream(poolingOutputs)
                        .mapToInt(value -> value.getRows() * value.getCols())
                        .sum();
                //Add every matrix's elements to the vector, thereby flatten the output
                final Vec poolingOutputsVec = new Vec(vectorSize);
                for (int j = 0; j < poolingOutputs.length; j++) {
                    final Matrix matrix = poolingOutputs[j];
                    for (int k = 0; k < matrix.getRows(); k++) {
                        for (int l = 0; l < matrix.getCols(); l++) {
                            poolingOutputsVec.set(j * matrix.getRows() + l, matrix.get(k, l));
                        }
                    }
                }

                System.out.printf("Feeding %d inputs to the network%n", poolingOutputsVec.length());

                Vec output = poolingOutputsVec;
                for (final FullyConnectedLayer fullyConnectedLayer : fullyConnectedLayers) {
                    output = fullyConnectedLayer.forwardPropagation(output);
                }

                //Calculate error
                final Vec expectedOutput = trainingData.getExpectedResult();

                Vec error = new Vec(output.length());
                error.applyToElement((integer, aDouble) -> MathUtil.computeError(aDouble, expectedOutput.get(integer)));
                error.applyToElement(MathUtil.ActivationFunction.SOFTMAX::apply);

                for (int j = fullyConnectedLayers.size() - 1; j >= 0; j--) {
                    error = fullyConnectedLayers.get(j).propagateBackwards(error);
                }

                Matrix[] errorMatrix = new Matrix[poolingOutputs.length];
                for (int j = 0; j < errorMatrix.length; j++) {
                    errorMatrix[j] = new Matrix(poolingOutputs[j].getRows(), poolingOutputs[j].getCols());
                }
                //Insert the error from the error vector into the error matrix
                for (int j = 0; j < errorMatrix.length; j++) {
                    final Matrix matrix = errorMatrix[j];
                    for (int k = 0; k < matrix.getRows(); k++) {
                        for (int l = 0; l < matrix.getCols(); l++) {
                            matrix.set(k, l, error.get(j * matrix.getRows() + l));
                        }
                    }
                }

                for (int j = convolutionLayers.size() - 1; j >= 0; j--) {
                    final PoolingLayer poolingLayer = poolingLayers.get(j);
                    final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);

                    final Matrix[] poolingOutput = poolingLayer.backwardsPropagation(errorMatrix);
                    errorMatrix = convolutionLayer.backwardsPropagation(poolingOutput);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
