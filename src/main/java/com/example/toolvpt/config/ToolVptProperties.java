package com.example.toolvpt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toolvpt")
public class ToolVptProperties {

    // ===== Window / Scan region =====
    private int windowX = 150;
    private int windowY = 50;
    private int windowWidth = 1496;
    private int windowHeight = 936;

    private int regionWidth = 1100;
    private int regionHeight = 700;

    // ===== Target select =====
    private double maxAcceptableDistance = 80.0;
    private double minDistanceGap = 10.0;
    private int scanRadius = 300;

    // ===== Debug =====
    private boolean debugSaveImage = false;
    private String debugImagePath = "debug.png";

    // ===== Loop =====
    private long captureIntervalMs = 300;

    // ===== Detector region =====
    private int detectRegionX = 500;
    private int detectRegionY = 300;
    private int detectRegionWidth = 200;
    private int detectRegionHeight = 100;

    // ===== Detector threshold =====
    private double detectFightThreshold = 0.80;
    private double detectIdleThreshold = 0.72;
    private double detectMinConfidence = 0.60;

    // ===== Matcher tuning =====
    private double matcherAcceptScore = 30.0;
    private int matcherStep = 2;
    private int matcherMaxResults = 10;

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

    public int getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(int scanRadius) {
        this.scanRadius = scanRadius;
    }

    public long getCaptureIntervalMs() {
        return captureIntervalMs;
    }

    public void setCaptureIntervalMs(long captureIntervalMs) {
        this.captureIntervalMs = captureIntervalMs;
    }

    public int getDetectRegionX() {
        return detectRegionX;
    }

    public void setDetectRegionX(int detectRegionX) {
        this.detectRegionX = detectRegionX;
    }

    public int getDetectRegionY() {
        return detectRegionY;
    }

    public void setDetectRegionY(int detectRegionY) {
        this.detectRegionY = detectRegionY;
    }

    public int getDetectRegionWidth() {
        return detectRegionWidth;
    }

    public void setDetectRegionWidth(int detectRegionWidth) {
        this.detectRegionWidth = detectRegionWidth;
    }

    public int getDetectRegionHeight() {
        return detectRegionHeight;
    }

    public void setDetectRegionHeight(int detectRegionHeight) {
        this.detectRegionHeight = detectRegionHeight;
    }

    public double getDetectFightThreshold() {
        return detectFightThreshold;
    }

    public void setDetectFightThreshold(double detectFightThreshold) {
        this.detectFightThreshold = detectFightThreshold;
    }

    public double getDetectIdleThreshold() {
        return detectIdleThreshold;
    }

    public void setDetectIdleThreshold(double detectIdleThreshold) {
        this.detectIdleThreshold = detectIdleThreshold;
    }

    public double getDetectMinConfidence() {
        return detectMinConfidence;
    }

    public void setDetectMinConfidence(double detectMinConfidence) {
        this.detectMinConfidence = detectMinConfidence;
    }

    public double getMatcherAcceptScore() {
        return matcherAcceptScore;
    }

    public void setMatcherAcceptScore(double matcherAcceptScore) {
        this.matcherAcceptScore = matcherAcceptScore;
    }

    public int getMatcherStep() {
        return matcherStep;
    }

    public void setMatcherStep(int matcherStep) {
        this.matcherStep = matcherStep;
    }

    public int getMatcherMaxResults() {
        return matcherMaxResults;
    }

    public void setMatcherMaxResults(int matcherMaxResults) {
        this.matcherMaxResults = matcherMaxResults;
    }
}