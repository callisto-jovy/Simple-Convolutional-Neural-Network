package net.bplaced.abzzezz.util.image;

import net.bplaced.abzzezz.util.TrainData;
import net.bplaced.abzzezz.util.math.vector.Vec;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static net.bplaced.abzzezz.util.Const.IMAGE_SIZE;

public class ImageLoader {

    public static TrainData getRandomTrainingData() throws IOException {
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

    private static File getRandomFileFromDirectory(final @NotNull File dir) {
        assert dir.listFiles() == null : "Directory list files null";
        final List<File> files = Arrays.stream(dir.listFiles())
                .filter(file -> !file.isHidden())
                .toList();

        final int rnd = ThreadLocalRandom.current().nextInt(files.size());
        return files.get(rnd);
    }

}
