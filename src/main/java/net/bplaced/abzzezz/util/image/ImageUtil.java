package net.bplaced.abzzezz.util.image;

import net.bplaced.abzzezz.util.color.ColorUtil;
import net.bplaced.abzzezz.util.math.MathUtil;
import net.bplaced.abzzezz.util.math.matrix.Matrix;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * For further information and a further method to greyscale the image:
 * https://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
 */
public class ImageUtil {


    /**
     * Converts an image given from a specific file to a matrix, firstly the image is downscaled to a given width and height
     * then the image is greyscale
     *
     * @param file   file to load the image from
     * @param width  desired with
     * @param height desired height
     * @return the matrix
     * @throws IOException file input / output
     */
    public static Matrix getMatrixFromImage(final File file, final int width, final int height) throws IOException {
        final BufferedImage br = Scalr.resize(ImageIO.read(file), Scalr.Mode.FIT_EXACT, width, height);
        final Matrix matrix = new Matrix(width, height);
        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getCols(); j++) {
                final int rgb = br.getRGB(i, j);
                final int gray = ColorUtil.getGrayFromRGB(rgb);
                final int grayToRGB = ColorUtil.convertGrayValueToRGB(gray);
                matrix.set(i, j, grayToRGB);
            }
        }
        return matrix;
    }

    /**
     * Converts an image given from a specific file to a matrix, firstly the image is downscaled to a given width and height
     * then the image is greyscale and normalized
     *
     * @param file   file to load the image from
     * @param width  desired with
     * @param height desired height
     * @return the matrix
     * @throws IOException file input / output
     */
    public static Matrix getNormalizedMatrixFromImage(final File file, final int width, final int height) throws IOException {
        final BufferedImage br = Scalr.resize(ImageIO.read(file), Scalr.Mode.FIT_EXACT, width, height);
        final Matrix matrix = new Matrix(width, height);
        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getCols(); j++) {
                final int rgb = br.getRGB(i, j);
                final int gray = ColorUtil.getGrayFromRGB(rgb);
                //Map grayscale value to a value between 0-1
                matrix.set(i, j, gray / 225.);
            }
        }
        return matrix;
    }

    /**
     * Writes a given matrix to an image file
     *
     * @param matrix  matrix to process
     * @param outFile file to output the matrix's data to
     * @throws IOException Imageoutputstream
     */
    public static void writeMatrixToFile(final Matrix matrix, final File outFile) throws IOException {
        final BufferedImage bufferedImage = new BufferedImage(
                matrix.getRows(),
                matrix.getCols(),
                BufferedImage.TYPE_BYTE_GRAY);

        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getCols(); j++) {
                final int data = MathUtil.mapValue(matrix.get(i, j), 0, 1, 0, 255);
                bufferedImage.setRGB(i, j, data);
            }
        }
        ImageIO.write(bufferedImage, "PNG", outFile);
    }


    /**
     * Converts an image (via file) to a base64 string
     *
     * @param file image file to be encoded
     * @return a string with the image's data encoded in base64
     * @throws IOException IORead/IOWrite
     */
    public static String getImageBase64FromFile(final File file) throws IOException {
        final InputStream is = file.toURI().toURL().openStream(); //Open inputstream to the file
        final String encoded = Base64.getEncoder().encodeToString(is.readAllBytes()); //Encode all bytes in base64
        is.close(); //Close the inputstream
        return encoded;
    }
}
