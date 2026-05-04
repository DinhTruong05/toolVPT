package com.example.toolvpt.infrastructure.screen;

import com.example.toolvpt.config.ToolVptProperties;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class ScreenCaptureService {

    private final Robot robot;
    private final ToolVptProperties config;

    public ScreenCaptureService(ToolVptProperties config) throws AWTException {
        this.robot = new Robot();
        this.config = config;
    }

    /**
     * 🔥 Capture window (default)
     */
    public BufferedImage capture() {
        Rectangle rect = safeRect(
                config.getWindowX(),
                config.getWindowY(),
                config.getWindowWidth(),
                config.getWindowHeight()
        );

        return robot.createScreenCapture(rect);
    }

    /**
     * Capture vùng custom
     */
    public BufferedImage capture(Rectangle area) {
        return robot.createScreenCapture(safeRect(area));
    }

    /**
     * Debug full screen
     */
    public BufferedImage captureFull() {
        return robot.createScreenCapture(
                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
        );
    }

    // ================= SAFE =================

    private Rectangle safeRect(Rectangle r) {
        return safeRect(r.x, r.y, r.width, r.height);
    }

    private Rectangle safeRect(int x, int y, int w, int h) {

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        if (w <= 0 || h <= 0) {
            throw new RuntimeException("❌ Invalid capture size");
        }

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x + w > screen.width) {
            w = screen.width - x;
        }

        if (y + h > screen.height) {
            h = screen.height - y;
        }

        return new Rectangle(x, y, w, h);
    }
}