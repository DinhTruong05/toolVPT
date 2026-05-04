package com.example.toolvpt.domain.detector;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.domain.decision.GameState;
import com.example.toolvpt.infrastructure.screen.ImageUtils;
import com.example.toolvpt.infrastructure.screen.ScreenCaptureService;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

@Component
public class BattleStateDetector {

    private final ScreenCaptureService screenService;
    private final ToolVptProperties config;

    private BufferedImage fightingSample;
    private BufferedImage idleSample;

    // 🔥 threshold có thể config sau
    private static final double THRESHOLD = 0.8;

    public BattleStateDetector(ScreenCaptureService screenService,
                               ToolVptProperties config) {
        this.screenService = screenService;
        this.config = config;
        loadSamples();
    }

    private void loadSamples() {
        try {
            fightingSample = loadImage("samples/fighting.png");
            idleSample = loadImage("samples/idle.png");

        } catch (Exception e) {
            throw new RuntimeException("❌ Load sample failed", e);
        }
    }

    private BufferedImage loadImage(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new RuntimeException("❌ Missing resource: " + path);
        }

        return ImageIO.read(is);
    }

    public DetectionResult detect() {

        BufferedImage screen = screenService.capture();

        BufferedImage region = cropSafe(screen,
                500, 300,   // TODO: nên đưa vào config
                200, 100
        );

        double fightScore = ImageUtils.compare(region, fightingSample);
        double idleScore = ImageUtils.compare(region, idleSample);

        System.out.println("📊 fight=" + fightScore + " idle=" + idleScore);

        if (fightScore > THRESHOLD) {
            return new DetectionResult(GameState.FIGHTING);
        }

        if (idleScore > THRESHOLD) {
            return new DetectionResult(GameState.IDLE);
        }

        return new DetectionResult(GameState.UNKNOWN);
    }

    /**
     * 🔥 Crop an toàn tránh crash
     */
    private BufferedImage cropSafe(BufferedImage src,
                                   int x, int y, int w, int h) {

        int maxW = src.getWidth();
        int maxH = src.getHeight();

        if (x + w > maxW) w = maxW - x;
        if (y + h > maxH) h = maxH - y;

        if (w <= 0 || h <= 0) {
            throw new RuntimeException("❌ Invalid crop region");
        }

        return src.getSubimage(x, y, w, h);
    }
}