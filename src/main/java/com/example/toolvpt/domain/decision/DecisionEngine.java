package com.example.toolvpt.domain.decision;

import com.example.toolvpt.domain.detector.GameState;

public class DecisionEngine {

    public BotAction decide(GameState state) {
        if (state == null) {
            return BotAction.SEARCH_ENEMY;
        }
        return switch (state) {
            case IDLE, UNKNOWN -> BotAction.SEARCH_ENEMY;
            case FIGHTING -> BotAction.ATTACK;
            case VICTORY -> BotAction.CLICK_REWARD;
        };
    }

    public BotAction decide(String state) {
        if (state == null || state.isBlank()) {
            return BotAction.SEARCH_ENEMY;
        }
        try {
            return decide(GameState.valueOf(state));
        } catch (IllegalArgumentException e) {
            return BotAction.SEARCH_ENEMY;
        }
    }
}