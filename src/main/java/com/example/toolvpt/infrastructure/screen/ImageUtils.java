package com.example.toolvpt.infrastructure.screen;

import java.awt.image.BufferedImage;

public class ImageUtils {

    private static final int STEP = 2;

    /**
     * So sánh 2 ảnh (trả về 0 → 1, càng cao càng giống)
     */
    public static double compare(BufferedImage img1, BufferedImage img2) {

        int width = Math.min(img1.getWidth(), img2.getWidth());
        int height = Math.min(img1.getHeight(), img2.getHeight());

        long diff = 0;
        int count = 0;

        for (int y = 0; y < height; y += STEP) {
            for (int x = 0; x < width; x += STEP) {

                int g1 = gray(img1.getRGB(x, y));
                int g2 = gray(img2.getRGB(x, y));

                diff += Math.abs(g1 - g2);
                count++;

                // 🔥 early stop nếu lệch quá nhiều
                if (count > 50 && diff / (double) count > 80) {
                    return 0.0;
                }
            }
        }

        double maxDiff = 255.0 * count;

        return 1.0 - (diff / maxDiff);
    }

    /**
     * Convert RGB → grayscale
     */
    private static int gray(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }
}