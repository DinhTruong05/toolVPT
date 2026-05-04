package com.example.toolvpt.domain.decision;

public class DecisionEngine {

    public BotAction decide(String stateStr) {
        GameState state = GameState.from(stateStr);
        return decide(state);
    }

    public BotAction decide(GameState state) {
        return switch (state) {
            case IDLE -> BotAction.SEARCH_ENEMY;
            case FIGHTING -> BotAction.ATTACK;
            case VICTORY -> BotAction.CLICK_REWARD;
            default -> BotAction.NONE;
        };
    }
}