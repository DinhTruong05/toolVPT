package com.example.toolvpt.domain.decision;

public class DecisionEngine {

    public BotAction decide(String state) {
        if (state == null || state.isBlank()) {
            return BotAction.SEARCH_ENEMY;
        }

        return switch (state) {
            case "IDLE" -> BotAction.SEARCH_ENEMY;
            case "FIGHTING" -> BotAction.ATTACK;
            case "VICTORY" -> BotAction.CLICK_REWARD;
            case "UNKNOWN" -> BotAction.SEARCH_ENEMY;
            default -> BotAction.SEARCH_ENEMY;
        };
    }
}