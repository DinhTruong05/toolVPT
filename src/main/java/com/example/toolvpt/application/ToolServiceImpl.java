package com.example.toolvpt.application;

import com.example.toolvpt.domain.detector.DetectionResult;
import com.example.toolvpt.infrastructure.runner.ToolRunner;
import org.springframework.stereotype.Service;

@Service
public class ToolServiceImpl implements ToolService {

    private final ToolRunner runner;

    public ToolServiceImpl(ToolRunner runner) {
        this.runner = runner;
    }

    @Override
    public DetectionResult detect() {
        return runner.detect();
    }

    @Override
    public long getInterval() {
        long interval = runner.getInterval();

        // 🔥 tránh trường hợp trả về 0 gây loop CPU 100%
        return interval > 0 ? interval : 100;
    }
}