package com.example.toolvpt.application;

import com.example.toolvpt.domain.detector.DetectionResult;

/**
 * Service trung gian giữa BotEngine và detection layer
 */
public interface ToolService {

    /**
     * Thực hiện detect state hiện tại của game
     */
    DetectionResult detect();

    /**
     * Interval giữa các lần detect (ms)
     */
    long getInterval();
}