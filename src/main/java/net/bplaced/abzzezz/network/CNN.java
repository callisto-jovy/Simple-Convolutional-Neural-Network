package net.bplaced.abzzezz.network;

import net.bplaced.abzzezz.network.components.ConvolutionLayer;
import net.bplaced.abzzezz.network.components.FullyConnectedLayer;
import net.bplaced.abzzezz.network.components.PoolingLayer;
import net.bplaced.abzzezz.network.components.SoftMaxLayer;
import net.bplaced.abzzezz.util.TrainData;
import net.bplaced.abzzezz.util.image.ImageUtil;
import net.bplaced.abzzezz.util.matrix.Matrix;
import net.bplaced.abzzezz.util.vector.Vec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static net.bplaced.abzzezz.util.Util.*;

public class CNN {

    public static final CNN CONVOLUTIONAL_NEURAL_NETWORK = new CNN();

    public void train(final int iterations) {
        final List<Matrix[]> kernelList = new ArrayList<>();
        final List<ConvolutionLayer> convolutionLayers = new ArrayList<>();
        final List<PoolingLayer> poolingLayers = new ArrayList<>();

        convolutionLayers.add(new ConvolutionLayer());
        poolingLayers.add(new PoolingLayer());
        //   convolutionLayers.add(new ConvolutionLayer());
        //    poolingLayers.add(new PoolingLayer());
        // kernelList.add(initFilters(15, 9));
        kernelList.add(initFilters(2, 5));
        //  kernelList.add(initFilters(10, 5));

        if (convolutionLayers.size() != poolingLayers.size()) {
            throw new IllegalStateException("Convolution and pooling layers must have the same amount of layers");
        }

        for (int i = 0; i < iterations; i++) {
            try {
                final TrainData trainData = getRandomTrainingData();
                Matrix[] poolingOutputs = new Matrix[]{trainData.getInput()};

                for (int j = 0; j < convolutionLayers.size(); j++) {
                    final ConvolutionLayer convolutionLayer = convolutionLayers.get(j);
                    final PoolingLayer poolingLayer = poolingLayers.get(j);
                    final Matrix[] kernels = kernelList.get(j);

                    final List<Matrix> poolingOutputsTemp = new ArrayList<>();

                    for (int k = 0; k < poolingOutputs.length; k++) {
                        final Matrix[] convolutionOutput = convolutionLayer.forwardPropagation(trainData.getInput(), kernels);
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

                System.out.printf("Feeding %d to the network%n", poolingOutputsVec.length());

                final FullyConnectedLayer inputLayer = new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, poolingOutputsVec.length());
                Vec output = inputLayer.forwardPropagation(poolingOutputsVec);

                for (int j = 0; j < FULLY_CONNECTED_NETWORK_DEPTH; j++) {
                    FullyConnectedLayer fullyConnectedLayer = new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, FULLY_CONNECTED_NETWORK_WIDTH);
                    output = fullyConnectedLayer.forwardPropagation(output);
                }

                final SoftMaxLayer maxLayer = new SoftMaxLayer(FULLY_CONNECTED_NETWORK_WIDTH, 1);
                maxLayer.forwardPropagation(output).print();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public TrainData getRandomTrainingData() throws IOException {
        final File dogImages = new File("Images");
        final File otherImages = new File("OtherImages");
        final int rnd = ThreadLocalRandom.current().nextInt(1); //2;
        //Dog directory
        if (rnd == 0) {
            final File randomInnerDir = getRandomFileFromDirectory(dogImages);
            return new TrainData(ImageUtil.getNormalizedMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), IMAGE_SIZE, IMAGE_SIZE), 1);
        } else { //OtherImages image directory
            final File randomInnerDir = getRandomFileFromDirectory(otherImages);
            return new TrainData(ImageUtil.getNormalizedMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), IMAGE_SIZE, IMAGE_SIZE), 0);
        }
    }

    private File getRandomFileFromDirectory(final @NotNull File dir) {
        assert dir.listFiles() == null : "Directory list files null";
        final int rnd = ThreadLocalRandom.current().nextInt(dir.listFiles().length);
        return Objects.requireNonNull(dir.listFiles())[rnd];
    }

    public static Matrix @NotNull [] initFilters(final int filters, final int matrixSize) {
        final Matrix[] matrices = new Matrix[filters];
        for (int i = 0; i < matrices.length; i++) {
            //nxn filter matrix
            matrices[i] = new Matrix(matrixSize, matrixSize);
            matrices[i].randomize();
        }
        return matrices;
    }
}
