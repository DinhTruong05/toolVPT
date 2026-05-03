package com.example.toolvpt.application;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.screen.TemplateMatcher;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@Component
public class TargetFinder {

    private final TemplateMatcher matcher;
    private final List<BufferedImage> templates;
    private final ToolVptProperties config;

    public TargetFinder(TemplateMatcher matcher,
                        List<BufferedImage> templates,
                        ToolVptProperties config) {
        this.matcher = matcher;
        this.templates = templates;
        this.config = config;
    }

    public Point findNearest(BufferedImage screen) {
        return findFromTemplates(screen, templates);
    }

    public Point findOnly(BufferedImage screen, int index) {
        if (index < 0 || index >= templates.size()) return null;
        return findFromTemplates(screen, List.of(templates.get(index)));
    }

    private Point findFromTemplates(BufferedImage screen, List<BufferedImage> list) {

        int width = screen.getWidth();
        int height = screen.getHeight();

        Point best = null;
        double bestDist = Double.MAX_VALUE;

        int centerX = width / 2;
        int centerY = height / 2;

        for (BufferedImage template : list) {

            List<Point> points = matcher.findPoints(screen, template);

            for (Point point : points) {

                double dist = Math.hypot(point.x - centerX, point.y - centerY);

                if (config.getMaxAcceptableDistance() > 0 &&
                        dist > config.getMaxAcceptableDistance()) {
                    continue;
                }

                if (dist < bestDist) {
                    bestDist = dist;
                    best = point;
                }
            }
        }

        if (config.isDebugSaveImage()) {
            saveDebug(screen);
        }

        if (best != null) {
            System.out.println("🎯 Selected nearest target: " + best.x + "," + best.y + " distance=" + bestDist);
        }

        return best;
    }

    private void saveDebug(BufferedImage img) {
        try {
            File file = new File(config.getDebugImagePath());
            ImageIO.write(img, "png", file);
            System.out.println("📸 Saved debug image");
        } catch (Exception ignored) {
        }
    }
}