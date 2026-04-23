package com.example.toolvpt.infrastructure.screen;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TemplateMatcher {

    public Point find(BufferedImage screen, BufferedImage template) {

        int sw = screen.getWidth();
        int sh = screen.getHeight();

        int tw = template.getWidth();
        int th = template.getHeight();

        for (int y = 0; y < sh - th; y += 5) {
            for (int x = 0; x < sw - tw; x += 5) {

                if (match(screen, template, x, y)) {
                    return new Point(x + tw / 2, y + th / 2);
                }
            }
        }

        return null;
    }

    private boolean match(BufferedImage screen, BufferedImage template, int startX, int startY) {

        int threshold = 30; // độ sai lệch cho phép

        for (int y = 0; y < template.getHeight(); y += 3) {
            for (int x = 0; x < template.getWidth(); x += 3) {

                int rgb1 = screen.getRGB(startX + x, startY + y);
                int rgb2 = template.getRGB(x, y);

                if (colorDiff(rgb1, rgb2) > threshold) {
                    return false;
                }
            }
        }

        return true;
    }

    private int colorDiff(int c1, int c2) {
        int r1 = (c1 >> 16) & 0xff;
        int g1 = (c1 >> 8) & 0xff;
        int b1 = c1 & 0xff;

        int r2 = (c2 >> 16) & 0xff;
        int g2 = (c2 >> 8) & 0xff;
        int b2 = c2 & 0xff;

        return Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
    }
}