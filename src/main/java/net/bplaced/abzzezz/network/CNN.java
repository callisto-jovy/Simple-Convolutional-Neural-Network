package net.bplaced.abzzezz.network;

import net.bplaced.abzzezz.network.components.ConvolutionLayer;
import net.bplaced.abzzezz.network.components.FullyConnectedLayer;
import net.bplaced.abzzezz.network.components.PoolingLayer;
import net.bplaced.abzzezz.network.components.SoftMaxLayer;
import net.bplaced.abzzezz.util.TrainData;
import net.bplaced.abzzezz.util.Util;
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
import java.util.stream.Collectors;

import static net.bplaced.abzzezz.util.Util.*;

public class CNN {

    public static final CNN CONVOLUTIONAL_NEURAL_NETWORK = new CNN();

    public void train(final int iterations) {
        final List<Matrix[]> kernelList = new ArrayList<>();
        final List<ConvolutionLayer> convolutionLayers = new ArrayList<>();
        final List<PoolingLayer> poolingLayers = new ArrayList<>();

        convolutionLayers.add(new ConvolutionLayer());
        poolingLayers.add(new PoolingLayer());
        convolutionLayers.add(new ConvolutionLayer());
        poolingLayers.add(new PoolingLayer());
        kernelList.add(initFilters(15, 9));
        kernelList.add(initFilters(10, 5));

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
                //This is a shitty, suboptimal way to do it, but it works
                //Loop through the latest pooling output and join them to one vector
                final List<Double> poolingOutputsJoined = new ArrayList<>();
                for (final Matrix poolingOutput : poolingOutputs) {
                    final Vec vector = poolingOutput.toVec();
                    for (int j = 0; j < vector.length(); j++) {
                        poolingOutputsJoined.add(vector.getValue(j));
                    }
                }
                //Fucking shit.
                final Vec poolingOutputsJoinedVec = new Vec(poolingOutputsJoined.size());
                for (int j = 0; j < poolingOutputsJoinedVec.length(); j++) {
                    poolingOutputsJoinedVec.set(j, poolingOutputsJoined.get(j));
                }

                final FullyConnectedLayer inputLayer = new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, poolingOutputsJoinedVec.length());

                Vec output = inputLayer.forwardPropagation(poolingOutputsJoinedVec);

                for (int j = 0; j < FULLY_CONNECTED_NETWORK_DEPTH; j++) {
                    FullyConnectedLayer fullyConnectedLayer = new FullyConnectedLayer(FULLY_CONNECTED_NETWORK_WIDTH, FULLY_CONNECTED_NETWORK_WIDTH);
                    output = fullyConnectedLayer.forwardPropagation(output);
                }

                final FullyConnectedLayer outputLayer = new FullyConnectedLayer(1, FULLY_CONNECTED_NETWORK_WIDTH);
                output = outputLayer.forwardPropagation(output);
                output.print();

                // final SoftMaxLayer maxLayer = new SoftMaxLayer(1, 1);
                // maxLayer.forwardPropagation(output.toMatrix()).print();
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
            return new TrainData(ImageUtil.getMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), IMAGE_SIZE, IMAGE_SIZE), 1);
        } else { //OtherImages image directory
            final File randomInnerDir = getRandomFileFromDirectory(otherImages);
            return new TrainData(ImageUtil.getMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), IMAGE_SIZE, IMAGE_SIZE), 0);
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
