package com.example.toolvpt.domain.decision;

public enum GameState {
    IDLE,
    FIGHTING,
    VICTORY,
    UNKNOWN;

    public static GameState from(String value) {
        try {
            return GameState.valueOf(value);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}