package com.example.toolvpt.application;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.screen.TemplateMatcher;
import com.example.toolvpt.infrastructure.screen.TemplateMatcher.MatchResult;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TargetFinder {

    private static final int MAX_DEBUG_CANDIDATES = 12;

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

        List<RankedMatch> acceptedMatches = new ArrayList<>();

        for (BufferedImage template : list) {
            for (MatchResult match : matcher.findScoredPoints(screen, template)) {
                Point point = match.point();
                if (point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
                    continue;
                }

                double dist = Math.hypot(point.x - centerX, point.y - centerY);
                if (config.getMaxAcceptableDistance() > 0 && dist > config.getMaxAcceptableDistance()) {
                    continue;
                }

                double rank = match.score() + dist * config.getTargetDistanceWeight();
                acceptedMatches.add(new RankedMatch(match, dist, rank));
            }
        }

        acceptedMatches.sort(Comparator.comparingDouble(RankedMatch::rank));
        RankedMatch best = chooseBest(acceptedMatches);

        if (config.isDebugSaveImage()) {
            saveDebug(screen, best, acceptedMatches, minX, minY, maxX, maxY);
        }

        if (best != null) {
            Point point = best.match().point();
            System.out.printf(
                    "🎯 Selected target: %d,%d score=%.2f dist=%.1f rank=%.2f candidates=%d%n",
                    point.x,
                    point.y,
                    best.match().score(),
                    best.distance(),
                    best.rank(),
                    acceptedMatches.size()
            );
        } else {
            System.out.println("❌ No target found (accepted candidates=0)");
        }

        return best == null ? null : best.match().point();
    }

    private RankedMatch chooseBest(List<RankedMatch> acceptedMatches) {
        RankedMatch best = null;
        double rankGap = Math.max(0.0, config.getMinDistanceGap());

        for (RankedMatch candidate : acceptedMatches) {
            if (best == null) {
                best = candidate;
                continue;
            }

            double rankDiff = candidate.rank() - best.rank();
            if (rankDiff < -rankGap || (Math.abs(rankDiff) <= rankGap && candidate.distance() < best.distance())) {
                best = candidate;
            }
        }

        return best;
    }

    private void saveDebug(BufferedImage img,
                           RankedMatch target,
                           List<RankedMatch> acceptedMatches,
                           int minX,
                           int minY,
                           int maxX,
                           int maxY) {
        try {
            BufferedImage copy = new BufferedImage(
                    img.getWidth(),
                    img.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = copy.createGraphics();
            g.drawImage(img, 0, 0, null);

            drawScanArea(g, img, minX, minY, maxX, maxY);
            drawCandidates(g, acceptedMatches);
            drawDebugText(g, target, acceptedMatches.size());
            drawSelectedTarget(g, target);

            g.dispose();

            File file = new File(config.getDebugImagePath());
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            ImageIO.write(copy, "png", file);

            System.out.println("📸 Saved debug image: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("⚠️ Debug save failed: " + e.getMessage());
        }
    }

    private void drawScanArea(Graphics2D g, BufferedImage img, int minX, int minY, int maxX, int maxY) {
        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.GREEN);
        g.drawLine(centerX - 12, centerY, centerX + 12, centerY);
        g.drawLine(centerX, centerY - 12, centerX, centerY + 12);

        g.setColor(new Color(255, 193, 7));
        g.drawRect(minX, minY, Math.max(1, maxX - minX), Math.max(1, maxY - minY));
    }

    private void drawCandidates(Graphics2D g, List<RankedMatch> acceptedMatches) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.CYAN);

        int limit = Math.min(MAX_DEBUG_CANDIDATES, acceptedMatches.size());
        for (int i = 0; i < limit; i++) {
            MatchResult match = acceptedMatches.get(i).match();
            Point point = match.point();
            int halfW = Math.max(6, match.templateWidth() / 2);
            int halfH = Math.max(6, match.templateHeight() / 2);
            g.drawRect(point.x - halfW, point.y - halfH, halfW * 2, halfH * 2);
        }
    }

    private void drawSelectedTarget(Graphics2D g, RankedMatch target) {
        if (target == null) {
            return;
        }

        MatchResult match = target.match();
        Point point = match.point();
        int halfW = Math.max(10, match.templateWidth() / 2);
        int halfH = Math.max(10, match.templateHeight() / 2);

        g.setColor(Color.RED);
        g.setStroke(new BasicStroke(3));
        g.drawOval(point.x - 12, point.y - 12, 24, 24);
        g.drawRect(point.x - halfW, point.y - halfH, halfW * 2, halfH * 2);
    }

    private void drawDebugText(Graphics2D g, RankedMatch target, int acceptedCandidates) {
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(Color.YELLOW);
        g.drawString("ToolVPT debug", 10, 20);
        g.drawString("candidates=" + acceptedCandidates, 10, 40);

        if (target != null) {
            Point point = target.match().point();
            g.drawString(
                    String.format(
                            "target=%d,%d score=%.2f dist=%.1f rank=%.2f",
                            point.x,
                            point.y,
                            target.match().score(),
                            target.distance(),
                            target.rank()
                    ),
                    10,
                    60
            );
        } else {
            g.drawString("target=NOT FOUND", 10, 60);
        }
    }

    private record RankedMatch(MatchResult match, double distance, double rank) {}
}