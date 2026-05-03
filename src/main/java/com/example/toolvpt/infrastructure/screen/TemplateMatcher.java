package com.example.toolvpt.infrastructure.screen;

import org.springframework.stereotype.Component;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.*;

@Component
public class TemplateMatcher {

    private static final double ACCEPT_SCORE = 30.0;
    private static final int STEP = 2;
    private static final int MAX_RESULTS = 10;

    public Point find(BufferedImage screen, BufferedImage template) {
        return findBestMatch(screen, template);
    }

    public Point findBestMatch(BufferedImage screen, BufferedImage template) {

        int sw = screen.getWidth();
        int sh = screen.getHeight();

        int tw = template.getWidth();
        int th = template.getHeight();

        double bestScore = Double.MAX_VALUE;
        Point bestPoint = null;

        for (int y = 0; y <= sh - th; y += STEP) {
            for (int x = 0; x <= sw - tw; x += STEP) {

                double score = matchScore(screen, template, x, y, bestScore);

                if (score < bestScore) {
                    bestScore = score;
                    bestPoint = new Point(x + tw / 2, y + th / 2);
                }
            }
        }

        if (bestScore <= ACCEPT_SCORE) {
            System.out.println("✅ Match OK: score=" + bestScore);
            return bestPoint;
        }

        System.out.println("❌ No match. Best=" + bestScore);
        return null;
    }

    private double matchScore(BufferedImage screen, BufferedImage template,
                              int startX, int startY, double currentBest) {

        long totalDiff = 0;
        int count = 0;

        for (int y = 0; y < template.getHeight(); y += 2) {
            for (int x = 0; x < template.getWidth(); x += 2) {

                int rgb1 = screen.getRGB(startX + x, startY + y);
                int rgb2 = template.getRGB(x, y);

                totalDiff += colorDiff(rgb1, rgb2);
                count++;

                // 🔥 early stop nếu đã tệ hơn best hiện tại
                if (totalDiff / (double) count > currentBest) {
                    return Double.MAX_VALUE;
                }
            }
        }

        return (double) totalDiff / count;
    }

    private int colorDiff(int c1, int c2) {
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