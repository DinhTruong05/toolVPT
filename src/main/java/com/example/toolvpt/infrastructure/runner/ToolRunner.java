package com.example.toolvpt.infrastructure.runner;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.domain.detector.BattleStateDetector;
import com.example.toolvpt.domain.detector.DetectionResult;
import org.springframework.stereotype.Component;

@Component
public class ToolRunner {

    private final BattleStateDetector detector;
    private final ToolVptProperties config;

    public ToolRunner(BattleStateDetector detector,
                      ToolVptProperties config) {
        this.detector = detector;
        this.config = config;
    }

    /**
     * Detect trạng thái game
     */
    public DetectionResult detect() {
        return detector.detect();
    }

    /**
     * 🔥 Interval lấy từ config (không hardcode)
     */
    public long getInterval() {
        return config.getCaptureIntervalMs() > 0
                ? config.getCaptureIntervalMs()
                : 100;
    }
}