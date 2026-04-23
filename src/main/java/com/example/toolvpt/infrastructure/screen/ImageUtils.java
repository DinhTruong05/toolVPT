package com.example.toolvpt.infrastructure.screen;

import java.awt.image.BufferedImage;

public class ImageUtils {

    public static double compare(BufferedImage img1, BufferedImage img2) {
        int width = Math.min(img1.getWidth(), img2.getWidth());
        int height = Math.min(img1.getHeight(), img2.getHeight());

        long diff = 0;

        for (int y = 0; y < height; y += 5) { // skip pixel cho nhanh
            for (int x = 0; x < width; x += 5) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                diff += Math.abs((rgb1 & 0xFF) - (rgb2 & 0xFF));
            }
        }

        double maxDiff = 255.0 * ((double) width / 5) * ((double) height / 5);
        return 1.0 - (diff / maxDiff); // 0 → khác, 1 → giống
    }
}