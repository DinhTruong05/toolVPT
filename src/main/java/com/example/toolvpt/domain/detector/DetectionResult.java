package com.example.toolvpt.domain.detector;

public class DetectionResult {
    private final GameState state;

    public DetectionResult(String state) {
        GameState parsed;
        try {
            parsed = state == null ? GameState.UNKNOWN : GameState.valueOf(state);
        } catch (Exception ignored) {
            parsed = GameState.UNKNOWN;
        }
        this.state = parsed;
    }

    public DetectionResult(GameState state) {
        this.state = state == null ? GameState.UNKNOWN : state;
    }

    public String getState() {
        return state.name();
    }

    public GameState getGameState() {
        return state;
    }
}