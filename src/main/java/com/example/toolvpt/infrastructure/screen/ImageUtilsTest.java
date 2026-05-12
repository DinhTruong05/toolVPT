package com.example.toolvpt.infrastructure.screen;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class ImageUtilsTest {

    @Test
    void compareIgnoresTransparentSamplePixelsForDetectorState() {
        BufferedImage captured = solidImage(4, 4, Color.BLUE);
        captured.setRGB(1, 1, Color.WHITE.getRGB());

        BufferedImage sample = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);
        sample.setRGB(1, 1, Color.WHITE.getRGB());

        double score = ImageUtils.compare(captured, sample);

        assertThat(score).isEqualTo(1.0);
    }

    @Test
    void compareReturnsZeroWhenSampleHasNoScorablePixels() {
        BufferedImage captured = solidImage(4, 4, Color.BLUE);
        BufferedImage sample = new BufferedImage(4, 4, BufferedImage.TYPE_INT_ARGB);

        assertThat(ImageUtils.compare(captured, sample)).isZero();
    }

    private BufferedImage solidImage(int width, int height, Color color) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }
}