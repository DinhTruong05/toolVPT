package com.example.toolvpt.config;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toolvpt")
public class ToolVptProperties {

    // ===== WINDOW =====
    private int windowX = 0;
    private int windowY = 0;
    private int windowWidth = 800;
    private int windowHeight = 600;

    // ===== REGION SCAN =====
    private int regionWidth = 400;
    private int regionHeight = 300;

    // 🔥 NEW: bán kính scan quanh center (quan trọng)
    private int scanRadius = 300;

    // ===== TARGET FILTER =====
    private double maxAcceptableDistance = 0; // 0 = disable
    private double minDistanceGap = 0;

    // ===== DEBUG =====
    private boolean debugSaveImage = false;
    private String debugImagePath = "debug.png";

    // ===== LOOP =====
    private long captureIntervalMs = 100;

    // ===== VALIDATION =====
    @PostConstruct
    public void validate() {

        if (windowWidth <= 0 || windowHeight <= 0) {
            throw new IllegalArgumentException("Window size must be > 0");
        }

        if (scanRadius <= 0) {
            scanRadius = 300;
        }

        if (captureIntervalMs < 10) {
            captureIntervalMs = 10; // tránh CPU 100%
        }

        if (debugImagePath == null || debugImagePath.isBlank()) {
            debugImagePath = "debug.png";
        }
    }

    // ===== GETTER & SETTER =====

    public int getWindowX() {
        return windowX;
    }

    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public int getRegionWidth() {
        return regionWidth;
    }

    public void setRegionWidth(int regionWidth) {
        this.regionWidth = regionWidth;
    }

    public int getRegionHeight() {
        return regionHeight;
    }

    public void setRegionHeight(int regionHeight) {
        this.regionHeight = regionHeight;
    }

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = scanRadius;
    }

    public double getMaxAcceptableDistance() {
        return maxAcceptableDistance;
    }

    public void setMaxAcceptableDistance(double maxAcceptableDistance) {
        this.maxAcceptableDistance = maxAcceptableDistance;
    }

    public boolean isDebugSaveImage() {
        return debugSaveImage;
    }

    public void setDebugSaveImage(boolean debugSaveImage) {
        this.debugSaveImage = debugSaveImage;
    }

    public String getDebugImagePath() {
        return debugImagePath;
    }

    public void setDebugImagePath(String debugImagePath) {
        this.debugImagePath = debugImagePath;
    }

    public double getMinDistanceGap() {
        return minDistanceGap;
    }

    public void setMinDistanceGap(double minDistanceGap) {
        this.minDistanceGap = minDistanceGap;
    }

    public long getCaptureIntervalMs() {
        return captureIntervalMs;
    }

    public void setCaptureIntervalMs(long captureIntervalMs) {
        this.captureIntervalMs = captureIntervalMs;
    }
}