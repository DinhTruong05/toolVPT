package com.example.toolvpt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "toolvpt")
public class ToolVptProperties {

    private int windowX;
    private int windowY;
    private int windowWidth;
    private int windowHeight;

    private int regionWidth;
    private int regionHeight;

    private double maxAcceptableDistance;

    private boolean debugSaveImage;
    private String debugImagePath;

    private double minDistanceGap;

    private long captureIntervalMs;

    private int detectRegionX;
    private int detectRegionY;
    private int detectRegionWidth;
    private int detectRegionHeight;

    private double matcherAcceptScore;
    private int matcherStep;
    private int matcherMaxResults;

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

    public double getMaxAcceptableDistance() {
        return maxAcceptableDistance;
    }

    public void setMaxAcceptableDistance(double maxAcceptableDistance) {
        this.maxAcceptableDistance = maxAcceptableDistance;
    }

    public boolean isDebugSaveImage() { // ✅ chuẩn Java Bean
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