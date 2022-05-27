package net.bplaced.abzzezz.network;

import net.bplaced.abzzezz.util.TrainData;
import net.bplaced.abzzezz.util.image.ImageUtil;
import net.bplaced.abzzezz.util.matrix.Matrix;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class CNN {


    public static final CNN CONVOLUTIONAL_NEURAL_NETWORK = new CNN();

    public void train(final int iterations) {

    }

    public TrainData getRandomTrainingData() throws IOException {
        final File dogImages = new File("Images");
        final File otherImages = new File("OtherImages");
        final int rnd = ThreadLocalRandom.current().nextInt(2);
        //Dog directory
        if (rnd == 0) {
            final File randomInnerDir = getRandomFileFromDirectory(dogImages);
            return new TrainData(ImageUtil.getMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), 128, 128), 1);
        } else { //OtherImages image directory
            final File randomInnerDir = getRandomFileFromDirectory(otherImages);
            return new TrainData(ImageUtil.getMatrixFromImage(getRandomFileFromDirectory(randomInnerDir), 128, 128), 0);
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
            //5x5 filter matrix
            matrices[i] = new Matrix(matrixSize, matrixSize);
            matrices[i].randomize();
        }
        return matrices;
    }

}
