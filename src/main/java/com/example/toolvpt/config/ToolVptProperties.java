package com.example.toolvpt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toolvpt")
public record ToolVptProperties(
        int windowX,
        int windowY,
        int windowWidth,
        int windowHeight,
        int regionWidth,
        int regionHeight,
        double maxAcceptableDistance,
        boolean debugSaveImage,
        String debugImagePath,
        double minDistanceGap,
        long captureIntervalMs
) {}