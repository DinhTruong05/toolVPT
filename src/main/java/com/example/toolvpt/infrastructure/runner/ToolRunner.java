package com.example.toolvpt.infrastructure.runner;

import com.example.toolvpt.domain.detector.BattleStateDetector;
import com.example.toolvpt.domain.detector.DetectionResult;
import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.screen.ScreenCaptureService;
import org.springframework.stereotype.Component;

@Component
public class ToolRunner {

    private final BattleStateDetector detector;

    public ToolRunner(ScreenCaptureService screenService, ToolVptProperties config) {
        this.detector = new BattleStateDetector(screenService, config);
    }

    public DetectionResult detect() {
        return detector.detect();
    }

    public long getInterval() {
        return 500;
    }
}