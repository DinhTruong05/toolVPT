package com.example.toolvpt.application;

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
}