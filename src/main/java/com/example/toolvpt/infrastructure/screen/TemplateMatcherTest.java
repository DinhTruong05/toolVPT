package com.example.toolvpt.infrastructure.screen;

import com.example.toolvpt.config.ToolVptProperties;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

class TemplateMatcherTest {

    @Test
    void ignoresTransparentTemplatePixelsWhenFindingTarget() {
        ToolVptProperties config = new ToolVptProperties();
        config.setMatcherAcceptScore(1.0);
        config.setMatcherStep(1);
        TemplateMatcher matcher = new TemplateMatcher(config);

        BufferedImage screen = solidImage(20, 20, Color.BLUE);
        screen.setRGB(10, 10, Color.RED.getRGB());

        BufferedImage template = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        template.setRGB(2, 2, Color.RED.getRGB());

        Point point = matcher.findBestMatch(screen, template);

        assertThat(point).isEqualTo(new Point(10, 10));
    }

    @Test
    void transparentOnlyTemplateIsRejected() {
        ToolVptProperties config = new ToolVptProperties();
        config.setMatcherAcceptScore(1.0);
        config.setMatcherStep(1);
        TemplateMatcher matcher = new TemplateMatcher(config);

        BufferedImage screen = solidImage(10, 10, Color.BLUE);
        BufferedImage template = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);

        assertThat(matcher.findBestMatch(screen, template)).isNull();
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