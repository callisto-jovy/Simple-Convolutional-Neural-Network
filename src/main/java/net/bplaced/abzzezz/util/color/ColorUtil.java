package net.bplaced.abzzezz.util.color;

public class ColorUtil {
    /**
     * Converts the gray value to the rgb int;
     * @param gray gray value to convert
     * @return the rgb int
     */
    public static int convertGrayValueToRGB(final int gray) {
        return ((gray & 0xFF) << 24) |
                ((gray & 0xFF) << 16) |
                ((gray & 0xFF) << 8) |
                ((gray & 0xFF));
    }

    /**
     * Converts a rgb int into grayscale by averaging the rgb values
     *
     * @param rgb the rgb integer value
     * @return the average grayscale value
     */
    public static int getGrayFromRGB(final int rgb) {
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
    public static int[] getRGB(final int rgb) {
        final int r = (rgb >> 16) & 0xFF;
        final int g = (rgb >> 8) & 0xFF;
        final int b = (rgb & 0xFF);
        return new int[]{r, g, b};
    }
}
