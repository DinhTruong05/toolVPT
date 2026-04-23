package com.example.toolvpt.application;

import com.example.toolvpt.domain.detector.DetectionResult;

public interface ToolService {
    DetectionResult detect();
    long getInterval();
}