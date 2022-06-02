package net.bplaced.abzzezz.util.image;

import net.bplaced.abzzezz.util.matrix.Matrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

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
                final int r = (rgb >> 16) & 0xFF;
                final int g = (rgb >> 8) & 0xFF;
                final int b = (rgb & 0xFF);
                final int gray = (r + g + b) / 3;
                final Color color = new Color(gray, gray, gray);
                matrix.set(i, j, color.getRGB());
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
        //final BufferedImage grayAndScaled = makeGray(br); //Grayscale image

        final Matrix matrix = new Matrix(width, height);
        for (int i = 0; i < matrix.getRows(); i++) {
            for (int j = 0; j < matrix.getCols(); j++) {
                // final double value = (br.getRGB(i, j) & 0xFF) / 255.; //Normalize to a value between 0-1
                final int rgb = br.getRGB(i, j);
                final int gray = getGrayFromRGB(rgb);
                final int grayToRGB = ((gray & 0xFF) << 24) |
                        ((gray & 0xFF) << 16) |
                        ((gray & 0xFF) << 8) |
                        ((gray & 0xFF));
                //Map grayscale value to a value between 0-1
                final double value = (grayToRGB) / 255.;
                matrix.set(i, j, value);
            }
        }
        return matrix;
    }

    /**
     * Converts a rgb int into grayscale by averaging the rgb values
     *
     * @param rgb the rgb integer value
     * @return the average grayscale value
     */
    private static int getGrayFromRGB(final int rgb) {
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = (rgb & 0xFF);
        return (r + g + b) / 3;
    }

    /**
     * Splits the rgb value of a color into its red, green and blue value
     *
     * @param rgb the rgb value
     * @return an array consisting of the red, green and blue value
     */
    private static int[] getRGB(final int rgb) {
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = (rgb & 0xFF);
        return new int[]{r, g, b};
    }


    /**
     * Taken from https://stackoverflow.com/questions/9131678/convert-a-rgb-image-to-grayscale-image-reducing-the-memory-in-java
     * Modified by me.
     *
     * @param img buffered image to turn grayscale
     */
    private static BufferedImage makeGray(BufferedImage img) {
        /*
        for (int x = 0; x < img.getWidth(); ++x)
            for (int y = 0; y < img.getHeight(); ++y) {
                final int rgb = img.getRGB(x, y);
                final int r = (rgb >> 16) & 0xFF;
                final int g = (rgb >> 8) & 0xFF;
                final int b = (rgb & 0xFF);

                // Normalize and gamma correct:
                final double rr = Math.pow(r / 255.0, 2.2);
                final double gg = Math.pow(g / 255.0, 2.2);
                double bb = Math.pow(b / 255.0, 2.2);

                // Calculate luminance:
                final double lum = 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;

                // Gamma compand and rescale to byte range:
                final int grayLevel = (int) (255.0 * Math.pow(lum, 1.0 / 2.2));
                final int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
                img.setRGB(x, y, gray);
            }

         */
        return null;
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
                final int data = mapValue(matrix.get(i, j), 0, 1, 0, 255);
                bufferedImage.setRGB(i, j, data);
            }
        }
        ImageIO.write(bufferedImage, "PNG", outFile);
    }

    public static int mapValue(double value, double min, double max, int newMin, int newMax) {
        return (int) (((value - min) / (max - min)) * (newMax - newMin) + newMin);
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
