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
        return robot.createScreenCapture(normalize(area));
    }

    /**
     * Chuẩn hóa vùng capture để caller dùng đúng tọa độ thực tế sau khi clamp vào màn hình.
     */
    public Rectangle normalize(Rectangle area) {
        return safeRect(area);
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
        if (r == null) {
            throw new RuntimeException("❌ Invalid capture area");
        }
        return safeRect(r.x, r.y, r.width, r.height);
    }

    private Rectangle safeRect(int x, int y, int w, int h) {

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

        if (screen.width <= 0 || screen.height <= 0 || w <= 0 || h <= 0) {
            throw new RuntimeException("❌ Invalid capture size");
        }

        x = Math.max(0, Math.min(x, screen.width - 1));
        y = Math.max(0, Math.min(y, screen.height - 1));
        w = Math.min(w, screen.width - x);
        h = Math.min(h, screen.height - y);

        if (w <= 0 || h <= 0) {
            throw new RuntimeException("❌ Capture area is outside screen");
        }

        return new Rectangle(x, y, w, h);
    }
}