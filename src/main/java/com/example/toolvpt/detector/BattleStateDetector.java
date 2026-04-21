package com.example.toolvpt.detector;

import java.awt.image.BufferedImage;

public class BattleStateDetector {

    private final BufferedImage idleSample;
    private final BufferedImage fightingSample;
    private final double maxAcceptableDistance;

    public BattleStateDetector(BufferedImage idleSample,
                               BufferedImage fightingSample,
                               double maxAcceptableDistance) {
        this.idleSample = idleSample;
        this.fightingSample = fightingSample;
        this.maxAcceptableDistance = maxAcceptableDistance;
    }

    public DetectionResult detect(BufferedImage current) {
        double distanceToIdle = ImageUtils.averageRgbDistance(current, idleSample);
        double distanceToFighting = ImageUtils.averageRgbDistance(current, fightingSample);

        GameState state;

        if (distanceToIdle > maxAcceptableDistance && distanceToFighting > maxAcceptableDistance) {
            state = GameState.UNKNOWN;
        } else if (distanceToIdle < distanceToFighting) {
            state = GameState.IDLE;
        } else {
            state = GameState.FIGHTING;
        }

        return new DetectionResult(state, distanceToIdle, distanceToFighting);
    }

    public static class DetectionResult {
        private final GameState state;
        private final double distanceToIdle;
        private final double distanceToFighting;

        public DetectionResult(GameState state, double distanceToIdle, double distanceToFighting) {
            this.state = state;
            this.distanceToIdle = distanceToIdle;
            this.distanceToFighting = distanceToFighting;
        }

        public GameState getState() {
            return state;
        }

        public double getDistanceToIdle() {
            return distanceToIdle;
        }

        public double getDistanceToFighting() {
            return distanceToFighting;
        }

        @Override
        public String toString() {
            return "DetectionResult{" +
                    "state=" + state +
                    ", distanceToIdle=" + String.format("%.2f", distanceToIdle) +
                    ", distanceToFighting=" + String.format("%.2f", distanceToFighting) +
                    '}';
        }
    }
}
