package com.example.toolvpt.application;

import org.springframework.stereotype.Component;

import java.awt.*;

@Component
public class BotController {

    private final BotEngine engine;

    public BotController(BotEngine engine) {
        this.engine = engine;
    }

    public void start() {
        engine.start();
    }

    public void stop() {
        engine.stop();
    }

    public boolean isRunning() {
        return engine.isRunning();
    }

    /**
     * Set target (Orc / Boss / ...)
     */
    public void setTarget(String target) {
        engine.setTarget(target);
    }

    /**
     * Update vùng scan (overlay UI)
     */
    public void updateRegion(Rectangle rect) {
        engine.updateRegion(rect);
    }
}