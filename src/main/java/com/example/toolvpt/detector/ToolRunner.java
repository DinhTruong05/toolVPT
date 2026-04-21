package com.example.toolvpt.detector;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ToolRunner {

    private final ScreenCaptureService captureService;
    private final BattleStateDetector detector;
    private final Rectangle screenRegion;

    public ToolRunner() throws Exception {

        int windowWidth = 1496;
        int windowHeight = 936;

        int windowX = 0;
        int windowY = 0;

        int regionWidth = 320;
        int regionHeight = 180;

        Rectangle centerRegion = ImageUtils.centerRegion(windowWidth, windowHeight, regionWidth, regionHeight);

        screenRegion = new Rectangle(
                windowX + centerRegion.x,
                windowY + centerRegion.y,
                centerRegion.width,
                centerRegion.height
        );

        BufferedImage idle = ImageUtils.loadFromResource("samples/idle.png");
        BufferedImage fighting = ImageUtils.loadFromResource("samples/fighting.png");

        captureService = new ScreenCaptureService();
        detector = new BattleStateDetector(idle, fighting, 100.0);
    }

    public GameState detect() {
        BufferedImage current = captureService.capture(screenRegion);
        return detector.detect(current).getState();
    }
}