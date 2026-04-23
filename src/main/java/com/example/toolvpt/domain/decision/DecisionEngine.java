package com.example.toolvpt.domain.decision;

public class DecisionEngine {

    public BotAction decide(String state) {
        return switch (state) {
            case "IDLE" -> BotAction.SEARCH_ENEMY;
            case "FIGHTING" -> BotAction.ATTACK;
            case "VICTORY" -> BotAction.CLICK_REWARD;
            default -> BotAction.NONE;
        };
    }
}