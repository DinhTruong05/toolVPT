package com.example.toolvpt.application;

import com.example.toolvpt.infrastructure.screen.TemplateMatcher;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class TargetFinder {

    private final TemplateMatcher matcher;
    private final List<BufferedImage> templates;

    public TargetFinder(TemplateMatcher matcher, List<BufferedImage> templates) {
        this.matcher = matcher;
        this.templates = templates;
    }

    public Point findNearest(BufferedImage screen) {

        int offsetX = 300;
        int offsetY = 150;
        int width = 600;
        int height = 400;

        BufferedImage region = screen.getSubimage(offsetX, offsetY, width, height);

        Point best = null;
        double bestDist = Double.MAX_VALUE;

        int centerX = width / 2;
        int centerY = height / 2;

        for (BufferedImage template : templates) {
            Point p = matcher.find(region, template);

            if (p != null) {
                double dist = Math.hypot(p.x - centerX, p.y - centerY);

                if (dist < bestDist) {
                    bestDist = dist;
                    best = new Point(p.x + offsetX, p.y + offsetY);
                }
            }
        }

        return best;
    }
}