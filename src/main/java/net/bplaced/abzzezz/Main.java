package net.bplaced.abzzezz;

import net.bplaced.abzzezz.network.CNN;
import net.bplaced.abzzezz.network.components.ConvolutionLayer;
import net.bplaced.abzzezz.util.image.ImageUtil;
import net.bplaced.abzzezz.util.matrix.Matrix;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(final String[] args) throws IOException {
        //CNN.CONVOLUTIONAL_NEURAL_NETWORK.train(1000);

//        final Matrix[] filters = CNN.initFilters(10);

        //   ConvolutionLayer.convolve(CNN.CONVOLUTIONAL_NEURAL_NETWORK.getRandomTrainingData().getInput(), filters[0]).print();

        /*
        final File imageFolder = new File("Images");
        final File image = imageFolder.listFiles()[0].listFiles()[0];

        final Matrix matrix = ImageUtil.getNormalizedMatrixFromImage(image, 128, 128);
        matrix.print();

         */
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

            for (final File file : innerFolder.listFiles()) {
                final Matrix matrix = ImageUtil.getMatrixFromImage(file, 128, 128);

                ImageUtil.writeMatrixToFile(matrix, new File(outputFolder, file.getName()));
            }
        }
    }
}
