package com.example.toolvpt.domain.detector;

import com.example.toolvpt.domain.decision.GameState;

/**
 * Kết quả detect trạng thái game
 */
public class DetectionResult {

    private final GameState state;

    public DetectionResult(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    @Override
    public String toString() {
        return "DetectionResult{state=" + state + '}';
    }
}