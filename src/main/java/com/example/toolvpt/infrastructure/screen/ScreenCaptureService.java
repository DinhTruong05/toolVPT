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

    public BufferedImage capture() {
        Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        return robot.createScreenCapture(rect);
    }
}