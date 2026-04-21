package com.example.toolvpt.detector;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenCaptureService {

    private final Robot robot;

    public ScreenCaptureService() throws AWTException {
        this.robot = new Robot();
    }

    public BufferedImage capture(Rectangle region) {
        return robot.createScreenCapture(region);
    }
}