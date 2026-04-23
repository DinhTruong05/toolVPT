package com.example.toolvpt.domain.detector;

import com.example.toolvpt.infrastructure.screen.ImageUtils;
import com.example.toolvpt.infrastructure.screen.ScreenCaptureService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class BattleStateDetector {

    private final ScreenCaptureService screenService;

    private BufferedImage fightingSample;
    private BufferedImage idleSample;

    public BattleStateDetector(ScreenCaptureService screenService) {
        this.screenService = screenService;
        loadSamples();
    }

    private void loadSamples() {
        try {
            InputStream f = getClass().getClassLoader()
                    .getResourceAsStream("samples/fighting.png");
            InputStream i = getClass().getClassLoader()
                    .getResourceAsStream("samples/idle.png");

            fightingSample = ImageIO.read(f);
            idleSample = ImageIO.read(i);

        } catch (Exception e) {
            throw new RuntimeException("Load sample failed", e);
        }
    }

    public DetectionResult detect() {
        BufferedImage screen = screenService.capture();

        // ⚠️ crop vùng quan trọng (fix theo game bạn)
        BufferedImage region = screen.getSubimage(500, 300, 200, 100);

        double fightScore = ImageUtils.compare(region, fightingSample);
        double idleScore = ImageUtils.compare(region, idleSample);

        if (fightScore > 0.8) return new DetectionResult("FIGHTING");
        if (idleScore > 0.8) return new DetectionResult("IDLE");

        return new DetectionResult("UNKNOWN");
    }
}