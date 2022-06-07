package net.bplaced.abzzezz;

import net.bplaced.abzzezz.network.CNN;
import net.bplaced.abzzezz.network.components.ConvolutionLayer;
import net.bplaced.abzzezz.network.components.PoolingLayer;
import net.bplaced.abzzezz.util.image.ImageUtil;
import net.bplaced.abzzezz.util.math.matrix.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {


    public static void main(final String[] args) throws IOException {
        CNN.CONVOLUTIONAL_NEURAL_NETWORK.train(1000);
        CNN.CONVOLUTIONAL_NEURAL_NETWORK.test(10);

        /*
        final Matrix input = new Matrix(5, 5);
        input.print();
        System.out.println("----------------");
        final ConvolutionLayer convolutionLayer = new ConvolutionLayer();
        final Matrix[] kernels = CNN.initFilters(1, 3);

        final Matrix[] matrices = convolutionLayer.forwardPropagation(input, kernels);

        for (Matrix matrix : matrices) {
            matrix.print();
            System.out.println("----------------");
        }

        final PoolingLayer poolingLayer = new PoolingLayer();
        final Matrix[] pooled = poolingLayer.forwardPropagation(matrices);

        System.out.println("Pooled results:");
        for (Matrix matrix : pooled) {
            matrix.print();
            System.out.println("----------------");
        }

        for (Matrix matrix : poolingLayer.backwardsPropagation(pooled)) {
            matrix.print();
            System.out.println("----------------");
        }

         */

        // convertImages();
    }

    public static void convertImages() throws IOException {
        //Test Image conversion
        final File imageFolder = new File("Images");
        final File outputFolder = new File("ImagesOut");
        if (!outputFolder.exists())
            outputFolder.mkdir();

        //load in all files
        final int breeds = (int) Files.walk(imageFolder.toPath()).count();
        //Take first breed as a test
        for (int i = 0; i < 1 /*breeds */; i++) {
            final File innerFolder = imageFolder.listFiles()[i];
            assert innerFolder.listFiles() != null : "No files found.";

            for (int j = 0; j < 1; j++) {
                final File file = innerFolder.listFiles()[j];
                final Matrix matrix = ImageUtil.getNormalizedMatrixFromImage(file, 32, 32);
                //   matrix.print();
                // ImageUtil.writeMatrixToFile(matrix, new File(outputFolder, file.getName()));
            }
        }
    }
}
