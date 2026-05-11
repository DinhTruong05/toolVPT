package com.example.toolvpt.application;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.domain.decision.BotAction;
import com.example.toolvpt.domain.decision.DecisionEngine;
import com.example.toolvpt.infrastructure.input.InputController;
import com.example.toolvpt.infrastructure.screen.ScreenCaptureService;
import com.example.toolvpt.infrastructure.screen.TemplateMatcher;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

@Component
public class BotEngine {

    private static final String TARGET_ORC = "Orc";
    private static final String TARGET_BOSS = "Boss";
    private static final long CLICK_COOLDOWN_MS = 1200;
    private static final int EXPLORE_CLICK_RADIUS = 140;
    private static final int NO_TARGET_EXPLORE_THRESHOLD = 4;

    private final ToolService service;
    private final ToolVptProperties config;
    private final InputController input;
    private final ScreenCaptureService screenService;
    private final TargetFinder targetFinder;
    private final DecisionEngine decision;

    private volatile boolean running = false;
    private volatile String currentTarget = TARGET_ORC;
    private volatile Rectangle dynamicRegion;

    private Thread botThread;
    private long lastClickTime = 0;
    private int noTargetStreak = 0;
    private Point lockedTarget;
    private int stableTargetFrames = 0;

    public BotEngine(
            ToolService service,
            ToolVptProperties config,
            InputController input,
            ScreenCaptureService screenService,
            TemplateMatcher matcher
    ) {
        this.service = service;
        this.config = config;
        this.input = input;
        this.screenService = screenService;
        this.decision = new DecisionEngine();
        this.targetFinder = new TargetFinder(matcher, loadTemplates(), config);
    }

    private void loop() {
        System.out.println("✅ Bot loop started");

        while (running) {
            try {
                var result = service.detect();
                BotAction action = decision.decide(result.getGameState());

                System.out.println("State: " + result.getGameState() + " | Action: " + action);
                execute(action);

                Thread.sleep(config.getCaptureIntervalMs());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        running = false;
        System.out.println("🛑 Bot loop stopped");
    }

    private void execute(BotAction action) {
        switch (action) {
            case SEARCH_ENEMY -> searchAndClick();
            case ATTACK -> {
                input.pressSpace();
                System.out.println("⚔️ Attack");
            }
            case CLICK_REWARD -> {
                input.click(700, 400);
                System.out.println("🎁 Click reward");
            }
            default -> {
                // NONE / IDLE
            }
        }
    }

    private void searchAndClick() {
        try {
            Rectangle region = screenService.normalize(getScanRegion());
            BufferedImage screen = screenService.capture(region);

            Point target = switch (currentTarget) {
                case TARGET_ORC -> targetFinder.findOnly(screen, 0);
                case TARGET_BOSS -> targetFinder.findOnly(screen, 1);
                default -> targetFinder.findNearest(screen);
            };

            boolean fallbackUsed = false;
            if (target == null && (TARGET_ORC.equals(currentTarget) || TARGET_BOSS.equals(currentTarget))) {
                target = targetFinder.findNearest(screen);
                fallbackUsed = target != null;
            }

            if (target != null) {
                target = stabilizeTarget(target);
                if (target == null) {
                    return;
                }
            }

            long now = System.currentTimeMillis();
            if (target != null && now - lastClickTime > CLICK_COOLDOWN_MS) {
                int clickX = target.x + region.x;
                int clickY = target.y + region.y;

                input.click(clickX, clickY);
                lastClickTime = now;
                noTargetStreak = 0;

                System.out.println("🎯 Click: " + clickX + "," + clickY);
                if (fallbackUsed) {
                    System.out.println("↪️ Fallback target used (nearest available)");
                }
            } else if (target == null) {
                resetTargetLock();
                noTargetStreak++;
                System.out.println("❌ No target found (streak=" + noTargetStreak + ")");

                if (noTargetStreak >= NO_TARGET_EXPLORE_THRESHOLD && now - lastClickTime > CLICK_COOLDOWN_MS) {
                    clickExplorePoint(region);
                    lastClickTime = now;
                    noTargetStreak = 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Point stabilizeTarget(Point detectedTarget) {
        int requiredFrames = Math.max(1, config.getTargetStableFrames());
        if (requiredFrames <= 1) {
            lockedTarget = new Point(detectedTarget);
            stableTargetFrames = 1;
            return detectedTarget;
        }

        int lockRadius = Math.max(1, config.getTargetLockRadius());
        if (lockedTarget == null || lockedTarget.distance(detectedTarget) > lockRadius) {
            lockedTarget = new Point(detectedTarget);
            stableTargetFrames = 1;
            System.out.println("🔒 Target lock started: " + lockedTarget.x + "," + lockedTarget.y);
            System.out.println("⏳ Waiting stable target: " + stableTargetFrames + "/" + requiredFrames);
            return null;
        }

        lockedTarget = new Point(
                (lockedTarget.x + detectedTarget.x) / 2,
                (lockedTarget.y + detectedTarget.y) / 2
        );
        stableTargetFrames++;

        if (stableTargetFrames < requiredFrames) {
            System.out.println("⏳ Waiting stable target: " + stableTargetFrames + "/" + requiredFrames);
            return null;
        }

        return new Point(lockedTarget);
    }

    private void resetTargetLock() {
        lockedTarget = null;
        stableTargetFrames = 0;
    }

    private void clickExplorePoint(Rectangle region) {
        int centerX = region.x + region.width / 2;
        int centerY = region.y + region.height / 2;

        int offsetX = (int) (Math.random() * (EXPLORE_CLICK_RADIUS * 2 + 1)) - EXPLORE_CLICK_RADIUS;
        int offsetY = (int) (Math.random() * (EXPLORE_CLICK_RADIUS * 2 + 1)) - EXPLORE_CLICK_RADIUS;

        int clickX = Math.max(region.x, Math.min(region.x + region.width - 1, centerX + offsetX));
        int clickY = Math.max(region.y, Math.min(region.y + region.height - 1, centerY + offsetY));

        input.click(clickX, clickY);
        System.out.println("🧭 Explore click: " + clickX + "," + clickY);
    }

    private Rectangle getScanRegion() {
        if (dynamicRegion != null) {
            return new Rectangle(dynamicRegion);
        }

        int width = Math.min(config.getRegionWidth(), config.getWindowWidth());
        int height = Math.min(config.getRegionHeight(), config.getWindowHeight());

        return new Rectangle(
                config.getWindowX(),
                config.getWindowY(),
                Math.max(1, width),
                Math.max(1, height)
        );
    }

    public synchronized void start() {
        if (running) {
            return;
        }

        noTargetStreak = 0;
        lastClickTime = 0;
        resetTargetLock();
        running = true;
        botThread = new Thread(this::loop, "toolvpt-bot-thread");
        botThread.start();

        System.out.println("✅ Bot started");
    }

    public synchronized void stop() {
        running = false;

        if (botThread != null) {
            botThread.interrupt();
            botThread = null;
        }

        System.out.println("🛑 Bot stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public void setTarget(String target) {
        if (target == null || target.isBlank()) {
            return;
        }

        this.currentTarget = target.trim();
        this.noTargetStreak = 0;
        resetTargetLock();
        System.out.println("🎯 Set target: " + this.currentTarget);
    }

    public void updateRegion(Rectangle rect) {
        if (rect == null) {
            return;
        }

        this.dynamicRegion = new Rectangle(rect);
        this.noTargetStreak = 0;
        resetTargetLock();
        System.out.println("📐 Updated scan region: " + rect);
    }

    private List<BufferedImage> loadTemplates() {
        try {
            return List.of(
                    loadImage("samples/orc.png"),
                    loadImage("samples/boss.png")
            );
        } catch (Exception e) {
            throw new RuntimeException("❌ Load template failed", e);
        }
    }

    private BufferedImage loadImage(String path) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("❌ Missing file: " + path);
            }

            BufferedImage image = ImageIO.read(is);
            if (image == null) {
                throw new RuntimeException("❌ Cannot decode image: " + path);
            }

            return image;
        }
    }
}