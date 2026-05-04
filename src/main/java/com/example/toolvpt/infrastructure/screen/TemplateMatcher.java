package com.example.toolvpt.infrastructure.screen;

import org.springframework.stereotype.Component;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

@Component
public class TemplateMatcher {

    private static final double ACCEPT_SCORE = 30.0;
    private static final int STEP = 3;
    private static final int MAX_RESULTS = 10;

    /**
     * 🔥 Find multiple match points
     */
    public List<Point> findPoints(BufferedImage screen, BufferedImage template) {

        int sw = screen.getWidth();
        int sh = screen.getHeight();

        int tw = template.getWidth();
        int th = template.getHeight();

        List<Point> results = new ArrayList<>();

        // 🔥 scan vùng trung tâm (giảm lag)
        int startX = sw / 4;
        int endX = sw * 3 / 4;

        int startY = sh / 4;
        int endY = sh * 3 / 4;

        for (int y = startY; y <= endY - th; y += STEP) {
            for (int x = startX; x <= endX - tw; x += STEP) {

                double score = matchScore(screen, template, x, y, ACCEPT_SCORE);

                if (score <= ACCEPT_SCORE) {

                    results.add(new Point(x + tw / 2, y + th / 2));

                    if (results.size() >= MAX_RESULTS) {
                        return results;
                    }
                }
            }
        }

        return results;
    }

    /**
     * 🔥 Matching score (càng nhỏ càng tốt)
     */
    private double matchScore(BufferedImage screen, BufferedImage template,
                              int startX, int startY, double currentBest) {

        long diff = 0;
        int count = 0;

        for (int y = 0; y < template.getHeight(); y += 2) {
            for (int x = 0; x < template.getWidth(); x += 2) {

                int g1 = gray(screen.getRGB(startX + x, startY + y));
                int g2 = gray(template.getRGB(x, y));

                diff += Math.abs(g1 - g2);
                count++;

                // 🔥 early stop
                if ((double) diff / count > currentBest) {
                    return Double.MAX_VALUE;
                }
            }
        }

        return (double) diff / count;
    }

    private int gray(int rgb) {
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return (r + g + b) / 3;
    }
}