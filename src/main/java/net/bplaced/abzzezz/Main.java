package net.bplaced.abzzezz;

import net.bplaced.abzzezz.network.CNN;
import net.bplaced.abzzezz.network.components.ConvolutionLayer;
import net.bplaced.abzzezz.network.components.PoolingLayer;
import net.bplaced.abzzezz.util.image.ImageLoader;
import net.bplaced.abzzezz.util.image.ImageUtil;
import net.bplaced.abzzezz.util.math.matrix.Matrix;
import net.bplaced.abzzezz.util.math.matrix.MatrixUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {


    public static void main(final String[] args) {
        CNN.CONVOLUTIONAL_NEURAL_NETWORK.train(100);
        CNN.CONVOLUTIONAL_NEURAL_NETWORK.test(10);

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
