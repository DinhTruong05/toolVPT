package com.example.toolvpt.infrastructure.screen;

import com.example.toolvpt.config.ToolVptProperties;
import org.springframework.stereotype.Component;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class TemplateMatcher {

    private static final double DEFAULT_ACCEPT_SCORE = 30.0;
    private static final int DEFAULT_STEP = 2;
    private static final int DEFAULT_MAX_RESULTS = 10;

    private final ToolVptProperties config;

    public TemplateMatcher(ToolVptProperties config) {
        this.config = config;
    }

    public Point find(BufferedImage screen, BufferedImage template) {
        MatchResult result = findBestMatchResult(screen, template);
        return result == null ? null : result.point();
    }

    public Point findBestMatch(BufferedImage screen, BufferedImage template) {
        MatchResult result = findBestMatchResult(screen, template);
        return result == null ? null : result.point();
    }

    public MatchResult findBestMatchResult(BufferedImage screen, BufferedImage template) {
        int sw = screen.getWidth();
        int sh = screen.getHeight();

        int tw = template.getWidth();
        int th = template.getHeight();
        if (tw <= 0 || th <= 0 || tw > sw || th > sh) {
            System.out.println("❌ Invalid template size: " + tw + "x" + th + " for screen " + sw + "x" + sh);
            return null;
        }

        int step = Math.max(1, config.getMatcherStep() > 0 ? config.getMatcherStep() : DEFAULT_STEP);
        double acceptScore = config.getMatcherAcceptScore() > 0 ? config.getMatcherAcceptScore() : DEFAULT_ACCEPT_SCORE;

        double bestScore = Double.MAX_VALUE;
        Point bestPoint = null;

        for (int y = 0; y <= sh - th; y += step) {
            for (int x = 0; x <= sw - tw; x += step) {
                double score = matchScore(screen, template, x, y, bestScore);
                if (score < bestScore) {
                    bestScore = score;
                    bestPoint = new Point(x + tw / 2, y + th / 2);
                }
            }
        }

        if (bestScore <= acceptScore) {
            System.out.println("✅ Match OK: score=" + bestScore);
            return new MatchResult(bestPoint, bestScore, tw, th);
        }

        System.out.println("❌ No match. Best=" + bestScore);
        return null;
    }

    public List<Point> findPoints(BufferedImage screen, BufferedImage template) {
        return findScoredPoints(screen, template)
                .stream()
                .map(MatchResult::point)
                .toList();
    }

    public List<MatchResult> findScoredPoints(BufferedImage screen, BufferedImage template) {
        int sw = screen.getWidth();
        int sh = screen.getHeight();

        int tw = template.getWidth();
        int th = template.getHeight();
        if (tw <= 0 || th <= 0 || tw > sw || th > sh) {
            System.out.println("❌ Invalid template size: " + tw + "x" + th + " for screen " + sw + "x" + sh);
            return List.of();
        }

        int step = Math.max(1, config.getMatcherStep() > 0 ? config.getMatcherStep() : DEFAULT_STEP);
        double acceptScore = config.getMatcherAcceptScore() > 0 ? config.getMatcherAcceptScore() : DEFAULT_ACCEPT_SCORE;
        int maxResults = config.getMatcherMaxResults() > 0 ? config.getMatcherMaxResults() : DEFAULT_MAX_RESULTS;

        List<MatchResult> candidates = new ArrayList<>();

        for (int y = 0; y <= sh - th; y += step) {
            for (int x = 0; x <= sw - tw; x += step) {
                double score = matchScore(screen, template, x, y, acceptScore);
                if (score <= acceptScore) {
                    candidates.add(new MatchResult(new Point(x + tw / 2, y + th / 2), score, tw, th));
                }
            }
        }

        candidates.sort(Comparator.comparingDouble(MatchResult::score));

        List<MatchResult> results = new ArrayList<>();
        double minCenterDistance = Math.max(6, Math.min(tw, th) / 4.0);

        for (MatchResult candidate : candidates) {
            boolean tooClose = results.stream().anyMatch(p -> p.point().distance(candidate.point()) < minCenterDistance);
            if (!tooClose) {
                results.add(candidate);
                if (results.size() >= maxResults) break;
            }
        }

        return results;
    }

    private double matchScore(BufferedImage screen, BufferedImage template, int startX, int startY, double currentBest) {
        long totalDiff = 0;
        int count = 0;

        for (int y = 0; y < template.getHeight(); y += 2) {
            for (int x = 0; x < template.getWidth(); x += 2) {
                int rgb1 = screen.getRGB(startX + x, startY + y);
                int rgb2 = template.getRGB(x, y);

                totalDiff += colorDiff(rgb1, rgb2);
                count++;

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

    public record MatchResult(Point point, double score, int templateWidth, int templateHeight) {}
}