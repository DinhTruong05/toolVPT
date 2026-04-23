package com.example.toolvpt.domain.detector;

public class DetectionResult {
    private final String state;

    public DetectionResult(String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }
}