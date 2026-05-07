package com.example.toolvpt.application;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.screen.TemplateMatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

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
        if (index < 0 || index >= templates.size()) {
            return null;
        }
        return findFromTemplates(screen, List.of(templates.get(index)));
    }

    private Point findFromTemplates(BufferedImage screen, List<BufferedImage> list) {
        int width = screen.getWidth();
        int height = screen.getHeight();

        int centerX = width / 2;
        int centerY = height / 2;

        int radius = config.getScanRadius();
        int minX = radius > 0 ? Math.max(0, centerX - radius) : 0;
        int maxX = radius > 0 ? Math.min(width - 1, centerX + radius) : width - 1;
        int minY = radius > 0 ? Math.max(0, centerY - radius) : 0;
        int maxY = radius > 0 ? Math.min(height - 1, centerY + radius) : height - 1;

        Point best = null;
        double bestDist = Double.MAX_VALUE;

        for (BufferedImage template : list) {
            List<Point> points = matcher.findPoints(screen, template);

            for (Point point : points) {
                if (point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
                    continue;
                }

                double dist = Math.hypot(point.x - centerX, point.y - centerY);
                if (config.getMaxAcceptableDistance() > 0 && dist > config.getMaxAcceptableDistance()) {
                    continue;
                }

                if (dist < bestDist) {
                    bestDist = dist;
                    best = point;
                }
            }
        }

        if (config.isDebugSaveImage()) {
            saveDebug(screen, best);
        }

        if (best != null) {
            System.out.println("🎯 Selected target: " + best.x + "," + best.y + " dist=" + bestDist);
        } else {
            System.out.println("❌ No target found");
        }

        return best;
    }

    private void saveDebug(BufferedImage img, Point target) {
        try {
            BufferedImage copy = new BufferedImage(
                    img.getWidth(),
                    img.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = copy.createGraphics();
            g.drawImage(img, 0, 0, null);

            if (target != null) {
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(3));
                g.drawOval(target.x - 10, target.y - 10, 20, 20);
            }

            g.dispose();

            File file = new File(config.getDebugImagePath());
            ImageIO.write(copy, "png", file);

            System.out.println("📸 Saved debug image");
        } catch (Exception e) {
            System.out.println("⚠️ Debug save failed: " + e.getMessage());
        }
    }
}