package net.bplaced.abzzezz.network;

import net.bplaced.abzzezz.network.components.*;
import net.bplaced.abzzezz.util.TrainData;
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

import static net.bplaced.abzzezz.util.Const.*;

public class CNN {

    public static final CNN CONVOLUTIONAL_NEURAL_NETWORK = new CNN();

    final List<Matrix[]> kernelList = new ArrayList<>();
    final List<ConvolutionLayer> convolutionLayers = new ArrayList<>();
    final List<PoolingLayer> poolingLayers = new ArrayList<>();
    final List<FullyConnectedLayer> fullyConnectedLayers = new ArrayList<>();

    public CNN() {
        convolutionLayers.add(new ConvolutionLayer());
        poolingLayers.add(new PoolingLayer());
        kernelList.add(initFilters(5, 9));

        convolutionLayers.add(new ConvolutionLayer());
        poolingLayers.add(new PoolingLayer());
        kernelList.add(initFilters(10, 5));

        convolutionLayers.add(new ConvolutionLayer());
        poolingLayers.add(new PoolingLayer());
        kernelList.add(initFilters(5, 3));

        //A 64*64 sized image produces, after the given convolutions 16.000 Inputs for the NN
        fullyConnectedLayers.add(new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, 16000, MathUtil.ActivationFunction.RELU));

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
                final TrainData trainingData = getRandomTrainingData();

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
        final double accuracy = ((double) (tests - errorCount)) / tests;
        System.out.println("Accuracy: " + accuracy);
    }

    private Vec convolveInput(final TrainData trainingData) throws IOException {
        Matrix[] poolingOutputs = new Matrix[]{trainingData.getInput()};

        for (int j = 0; j < convolutionLayers.size(); j++) {
            final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);
            final PoolingLayer poolingLayer = poolingLayers.get(j);
            final Matrix[] kernels = kernelList.get(j);

            final List<Matrix> poolingOutputsTemp = new ArrayList<>();

            for (final Matrix output : poolingOutputs) {
                final Matrix[] convolutionOutput = convolutionLayer.forwardPropagation(output, kernels);
                final Matrix[] poolingOutput = poolingLayer.forwardPropagation(convolutionOutput);
                poolingOutputsTemp.addAll(Arrays.asList(poolingOutput)); //Add to the current pooling
            }
            poolingOutputs = poolingOutputsTemp.toArray(Matrix[]::new);
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
                final TrainData trainingData = getRandomTrainingData();
                Matrix[] poolingOutputs = new Matrix[]{trainingData.getInput()};

                for (int j = 0; j < convolutionLayers.size(); j++) {
                    final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);
                    final PoolingLayer poolingLayer = poolingLayers.get(j);
                    final Matrix[] kernels = kernelList.get(j);

                    final List<Matrix> poolingOutputsTemp = new ArrayList<>();

                    for (final Matrix output : poolingOutputs) {
                        final Matrix[] convolutionOutput = convolutionLayer.forwardPropagation(output, kernels);
                        final Matrix[] poolingOutput = poolingLayer.forwardPropagation(convolutionOutput);
                        poolingOutputsTemp.addAll(Arrays.asList(poolingOutput)); //Add to the current pooling
                    }
                    poolingOutputs = poolingOutputsTemp.toArray(Matrix[]::new);
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

                for (int j = convolutionLayers.size() - 1; j >= 0; j--) {
                    final Matrix[] kernels = kernelList.get(j);
                    final PoolingLayer poolingLayer = poolingLayers.get(j);
                    final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);

                    final Matrix[] errorMatrix = new Matrix[poolingOutputs.length];
                    for (int k = 0; k < errorMatrix.length; k++) {
                        errorMatrix[k] = new Matrix(poolingOutputs[k].getRows(), poolingOutputs[k].getCols());
                    }

                    //Insert the error from the error vector into the error matrix
                    for (int k = 0; k < errorMatrix.length; k++) {
                        final Matrix errorMatrixK = errorMatrix[k];
                        for (int l = 0; l < errorMatrixK.getRows(); l++) {
                            for (int m = 0; m < errorMatrixK.getCols(); m++) {
                                errorMatrixK.set(l, m, error.get(k * errorMatrixK.getRows() + l));
                            }
                        }
                    }

                    final Matrix[] errorMatrixPooling = poolingLayer.backwardsPropagation(errorMatrix);
                    final Matrix[] errorMatrixConvolution = convolutionLayer.backPropagation(errorMatrixPooling, kernels);

                    for (int k = 0; k < errorMatrixConvolution.length; k++) {
                        final Matrix errorMatrixK = errorMatrixConvolution[k];
                        for (int l = 0; l < errorMatrixK.getRows(); l++) {
                            for (int m = 0; m < errorMatrixK.getCols(); m++) {
                                errorMatrix[k].set(l, m, errorMatrixK.get(l, m));
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private TrainData getRandomTrainingData() throws IOException {
        final File dogImages = new File("Images");
        final File otherImages = new File("OtherImages");
        final int rnd = ThreadLocalRandom.current().nextInt(2); //2;
        //Dog directory

        //TODO: Convert vectors into constants
        if (rnd == 0) {
            final File randomInnerDir = getRandomFileFromDirectory(dogImages);
            return new TrainData(ImageUtil.getNormalizedMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), IMAGE_SIZE, IMAGE_SIZE), new Vec(1, 1));
        } else { //OtherImages image directory
            final File randomInnerDir = getRandomFileFromDirectory(otherImages);
            return new TrainData(ImageUtil.getNormalizedMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), IMAGE_SIZE, IMAGE_SIZE), new Vec(1, 0));
        }
    }

    private File getRandomFileFromDirectory(final @NotNull File dir) {
        assert dir.listFiles() == null : "Directory list files null";
        final int rnd = ThreadLocalRandom.current().nextInt(dir.listFiles().length);
        return Objects.requireNonNull(dir.listFiles())[rnd];
    }

    private Matrix @NotNull [] initFilters(final int filters, final int matrixSize) {
        final Matrix[] matrices = new Matrix[filters];
        for (int i = 0; i < matrices.length; i++) {
            //nxn filter matrix
            matrices[i] = new Matrix(matrixSize, matrixSize);
            matrices[i].randomize();
        }
        return matrices;
    }
}
