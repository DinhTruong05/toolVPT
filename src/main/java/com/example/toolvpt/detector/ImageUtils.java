package com.example.toolvpt.detector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static BufferedImage loadImage(String path) throws IOException {
        return ImageIO.read(new File(path));
    }

    public static BufferedImage crop(BufferedImage image, Rectangle region) {
        return image.getSubimage(region.x, region.y, region.width, region.height);
    }

    public static double averageBrightness(BufferedImage image) {
        long sum = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        int total = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = new Color(image.getRGB(x, y));
                int brightness = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                sum += brightness;
            }
        }

        return sum / (double) total;
    }

    public static double averageRgbDistance(BufferedImage a, BufferedImage b) {
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
            throw new IllegalArgumentException("Hai ảnh phải cùng kích thước");
        }

        long sum = 0;
        int width = a.getWidth();
        int height = a.getHeight();
        int total = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color ca = new Color(a.getRGB(x, y));
                Color cb = new Color(b.getRGB(x, y));

                int dr = ca.getRed() - cb.getRed();
                int dg = ca.getGreen() - cb.getGreen();
                int db = ca.getBlue() - cb.getBlue();

                sum += (long) dr * dr + (long) dg * dg + (long) db * db;
            }
        }

        return Math.sqrt(sum / (double) total);
    }

    public static Rectangle centerRegion(int fullWidth, int fullHeight, int regionWidth, int regionHeight) {
        int x = (fullWidth - regionWidth) / 2;
        int y = (fullHeight - regionHeight) / 2;
        return new Rectangle(x, y, regionWidth, regionHeight);
    }
    public static BufferedImage loadFromResource(String path) throws IOException {
        return ImageIO.read(
                ImageUtils.class.getClassLoader().getResourceAsStream(path)
        );
    }
}