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
        int regionW = config.getDetectRegionWidth() > 0 ? config.getDetectRegionWidth() : 200;
        int regionH = config.getDetectRegionHeight() > 0 ? config.getDetectRegionHeight() : 100;

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

        if (fightScore > 0.8) return new DetectionResult(GameState.FIGHTING);
        if (idleScore > 0.8) return new DetectionResult(GameState.IDLE);

        return new DetectionResult(GameState.UNKNOWN);
    }
}