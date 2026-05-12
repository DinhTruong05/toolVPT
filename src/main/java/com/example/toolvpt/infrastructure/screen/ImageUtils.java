package com.example.toolvpt.infrastructure.screen;

import java.awt.image.BufferedImage;

public class ImageUtils {

    private static final int STEP = 2;
    private static final int MIN_ALPHA = 32;

    /**
     * So sánh 2 ảnh cùng kích thước hoặc khác kích thước.
     * Trả về 0 → 1, càng cao càng giống.
     * Pixel trong ảnh mẫu có alpha thấp sẽ được bỏ qua để mẫu PNG trong suốt
     * chỉ chấm điểm phần UI/target thật, không phạt nền xung quanh.
     */
    public static double compare(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            return compareSameSize(img1, img2);
        }
        return compareScaled(img1, img2);
    }

    /**
     * So sánh theo tỉ lệ khi ảnh capture và ảnh sample khác kích thước.
     */
    public static double compareScaled(BufferedImage img1, BufferedImage img2) {
        int width = img1.getWidth();
        int height = img1.getHeight();

        if (width <= 0 || height <= 0 || img2.getWidth() <= 0 || img2.getHeight() <= 0) {
            return 0.0;
        }

        long diff = 0;
        int count = 0;

        for (int y = 0; y < height; y += STEP) {
            int sampleY = Math.min(img2.getHeight() - 1, y * img2.getHeight() / height);

            for (int x = 0; x < width; x += STEP) {
                int sampleX = Math.min(img2.getWidth() - 1, x * img2.getWidth() / width);
                int sampleRgb = img2.getRGB(sampleX, sampleY);
                if (isTransparent(sampleRgb)) {
                    continue;
                }

                int g1 = gray(img1.getRGB(x, y));
                int g2 = gray(sampleRgb);

                diff += Math.abs(g1 - g2);
                count++;
            }
        }

        return score(diff, count);
    }

    private static double compareSameSize(BufferedImage img1, BufferedImage img2) {
        long diff = 0;
        int count = 0;

        for (int y = 0; y < img1.getHeight(); y += STEP) {
            for (int x = 0; x < img1.getWidth(); x += STEP) {
                int sampleRgb = img2.getRGB(x, y);
                if (isTransparent(sampleRgb)) {
                    continue;
                }

                int g1 = gray(img1.getRGB(x, y));
                int g2 = gray(sampleRgb);

                diff += Math.abs(g1 - g2);
                count++;
            }
        }

        return score(diff, count);
    }

    private static double score(long diff, int count) {
        if (count <= 0) {
            return 0.0;
        }

        double maxDiff = 255.0 * count;
        double result = 1.0 - (diff / maxDiff);
        return Math.max(0.0, Math.min(1.0, result));
    }

    private static boolean isTransparent(int argb) {
        return ((argb >>> 24) & 0xff) < MIN_ALPHA;
    }

    private static int gray(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }
}