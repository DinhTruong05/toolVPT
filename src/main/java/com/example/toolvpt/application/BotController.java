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

    // ✅ fix đúng
    public void setTarget(String target) {
        engine.setTarget(target);
    }

    public void updateRegion(Rectangle rect) {
        engine.updateRegion(rect);
    }
}