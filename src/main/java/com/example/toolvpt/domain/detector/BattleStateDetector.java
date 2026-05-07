package com.example.toolvpt.domain.detector;

import com.example.toolvpt.infrastructure.screen.ImageUtils;
import com.example.toolvpt.infrastructure.screen.ScreenCaptureService;
import com.example.toolvpt.config.ToolVptProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class BattleStateDetector {

    private final ScreenCaptureService screenService;
    private final ToolVptProperties config;

    private BufferedImage fightingSample;
    private BufferedImage idleSample;

    public BattleStateDetector(ScreenCaptureService screenService, ToolVptProperties config) {
        this.screenService = screenService;
        this.config = config;
        loadSamples();
    }

    private void loadSamples() {
        try (InputStream f = getClass().getClassLoader().getResourceAsStream("samples/fighting.png");
             InputStream i = getClass().getClassLoader().getResourceAsStream("samples/idle.png")) {

            if (f == null || i == null) {
                throw new IllegalStateException("Missing detector sample images in resources/samples");
            }

            fightingSample = ImageIO.read(f);
            idleSample = ImageIO.read(i);

            if (fightingSample == null || idleSample == null) {
                throw new IllegalStateException("Detector sample images cannot be decoded");
            }

        } catch (Exception e) {
            throw new RuntimeException("Load sample failed", e);
        }
    }

    public DetectionResult detect() {
        BufferedImage screen = screenService.capture();

        int regionX = config.getDetectRegionX() > 0 ? config.getDetectRegionX() : 500;
        int regionY = config.getDetectRegionY() > 0 ? config.getDetectRegionY() : 300;
        int regionW = config.getDetectRegionWidth() > 0 ? config.getDetectRegionWidth() : idleSample.getWidth();
        int regionH = config.getDetectRegionHeight() > 0 ? config.getDetectRegionHeight() : idleSample.getHeight();

        int startX = Math.max(0, Math.min(regionX, screen.getWidth() - 1));
        int startY = Math.max(0, Math.min(regionY, screen.getHeight() - 1));
        int w = Math.min(regionW, screen.getWidth() - startX);
        int h = Math.min(regionH, screen.getHeight() - startY);

        if (w <= 0 || h <= 0) {
            return new DetectionResult(GameState.UNKNOWN);
        }

        BufferedImage region = screen.getSubimage(startX, startY, w, h);

        double fightScore = ImageUtils.compare(region, fightingSample);
        double idleScore = ImageUtils.compare(region, idleSample);

        System.out.printf(
                "🧠 Detect region=%d,%d %dx%d | fight=%.3f idle=%.3f%n",
                startX,
                startY,
                w,
                h,
                fightScore,
                idleScore
        );

        double fightThreshold = config.getDetectFightThreshold() > 0 ? config.getDetectFightThreshold() : 0.72;
        double idleThreshold = config.getDetectIdleThreshold() > 0 ? config.getDetectIdleThreshold() : 0.68;
        double minConfidence = config.getDetectMinConfidence() > 0 ? config.getDetectMinConfidence() : 0.55;

        if (fightScore >= fightThreshold) {
            return new DetectionResult(GameState.FIGHTING);
        }
        if (idleScore >= idleThreshold) {
            return new DetectionResult(GameState.IDLE);
        }

        // fallback: giảm UNKNOWN nếu một mẫu vượt mức tự tin tối thiểu
        if (Math.max(fightScore, idleScore) >= minConfidence) {
            return new DetectionResult(idleScore >= fightScore ? GameState.IDLE : GameState.FIGHTING);
        }

        return new DetectionResult(GameState.UNKNOWN);
    }
}