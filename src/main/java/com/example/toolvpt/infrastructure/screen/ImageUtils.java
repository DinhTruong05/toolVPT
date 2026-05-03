package com.example.toolvpt.infrastructure.screen;

import java.awt.image.BufferedImage;

public class ImageUtils {

    public static double compare(BufferedImage img1, BufferedImage img2) {

        int width = Math.min(img1.getWidth(), img2.getWidth());
        int height = Math.min(img1.getHeight(), img2.getHeight());

        long diff = 0;
        int count = 0;

        for (int y = 0; y < height; y += 2) {
            for (int x = 0; x < width; x += 2) {

                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                diff += colorDiff(rgb1, rgb2);
                count++;
            }
        }

        double maxDiff = 255.0 * 3 * count;

        return 1.0 - (diff / maxDiff);
    }

    private static int colorDiff(int c1, int c2) {
        int r1 = (c1 >> 16) & 0xff;
        int g1 = (c1 >> 8) & 0xff;
        int b1 = c1 & 0xff;

        int r2 = (c2 >> 16) & 0xff;
        int g2 = (c2 >> 8) & 0xff;
        int b2 = c2 & 0xff;

        return Math.abs(r1 - r2)
                + Math.abs(g1 - g2)
                + Math.abs(b1 - b2);
    }
}