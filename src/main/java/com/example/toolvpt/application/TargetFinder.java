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
        if (index < 0 || index >= templates.size()) return null;
        return findFromTemplates(screen, List.of(templates.get(index)));
    }

    // 🔥 CORE FIXED
    private Point findFromTemplates(BufferedImage screen, List<BufferedImage> list) {

        int width = screen.getWidth();
        int height = screen.getHeight();

        Point best = null;
        double bestDist = Double.MAX_VALUE;

        int centerX = width / 2;
        int centerY = height / 2;

        for (BufferedImage template : list) {

            Point p = matcher.find(screen, template); // ❗ dùng full screen

            if (p != null) {

                double dist = Math.hypot(p.x - centerX, p.y - centerY);

                if (config.getMaxAcceptableDistance() > 0 &&
                        dist > config.getMaxAcceptableDistance()) {
                    continue;
                }

                if (dist < bestDist) {
                    bestDist = dist;
                    best = p; // ❗ KHÔNG cộng offset ở đây
                }
            }
        }

        if (config.isDebugSaveImage()) {
            saveDebug(screen);
        }

        return best;
    }

    private void saveDebug(BufferedImage img) {
        try {
            File file = new File(config.getDebugImagePath());
            ImageIO.write(img, "png", file);
            System.out.println("📸 Saved debug image");
        } catch (Exception ignored) {}
    }
}