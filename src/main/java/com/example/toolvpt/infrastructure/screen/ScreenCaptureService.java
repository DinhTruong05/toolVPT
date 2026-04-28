package com.example.toolvpt.infrastructure.screen;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

@Component
public class ScreenCaptureService {

    private final Robot robot;

    public ScreenCaptureService() throws AWTException {
        this.robot = new Robot();
    }

    // capture full màn hình
    public BufferedImage capture() {
        return robot.createScreenCapture(
                new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())
        );
    }

    // ✅ capture theo vùng (cái bạn đang cần)
    public BufferedImage capture(Rectangle area) {
        return robot.createScreenCapture(area);
    }
}