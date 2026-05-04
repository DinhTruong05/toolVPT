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

    private final ToolService service;
    private final ToolVptProperties config;
    private final InputController input;
    private final ScreenCaptureService screenService;
    private final TargetFinder targetFinder;
    private final DecisionEngine decision;

    private volatile boolean running = false;
    private volatile String currentTarget = "Orc";

    private Thread botThread;
    private long lastClickTime = 0;

    // vùng scan (có thể update từ overlay)
    private volatile Rectangle dynamicRegion;

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

        List<BufferedImage> templates = loadTemplates();
        this.targetFinder = new TargetFinder(matcher, templates, config);
    }

    // ================= LOOP =================

    private void loop() {
        System.out.println("✅ Bot loop started");

        while (running) {
            try {
                var result = service.detect();

                BotAction action = decision.decide(result.getState());

                System.out.println("State: " + result.getState() + " | Action: " + action);

                execute(action);

                Thread.sleep(config.getCaptureIntervalMs());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("❌ Bot error:");
                e.printStackTrace();
            }
        }

        running = false;
        System.out.println("🛑 Bot loop stopped");
    }

    // ================= ACTION =================

    private void execute(BotAction action) {
        switch (action) {
            case SEARCH_ENEMY -> searchAndClick();

            case ATTACK -> {
                input.pressSpace();
                System.out.println("⚔️ Attack");
            }

            case CLICK_REWARD -> {
                input.click(700, 400); // TODO: config hóa sau
                System.out.println("🎁 Click reward");
            }

            default -> {
                // NONE
            }
        }
    }

    // ================= TARGET =================

    private void searchAndClick() {

        // 🔥 validate window trước
        if (!isWindowValid()) {
            System.out.println("❌ Window chưa được chọn!");
            return;
        }

        try {
            Rectangle region = getScanRegion();

            BufferedImage screen = screenService.capture(region);

            Point target = switch (currentTarget) {
                case "Orc" -> targetFinder.findOnly(screen, 0);
                case "Boss" -> targetFinder.findOnly(screen, 1);
                default -> targetFinder.findNearest(screen);
            };

            long now = System.currentTimeMillis();

            if (target != null && now - lastClickTime > 1000) {

                int clickX = target.x + region.x;
                int clickY = target.y + region.y;

                input.click(clickX, clickY);
                lastClickTime = now;

                System.out.println("🎯 Click: " + clickX + "," + clickY);

            } else if (target == null) {
                System.out.println("❌ No target found");
            }

        } catch (Exception e) {
            System.err.println("❌ searchAndClick error:");
            e.printStackTrace();
        }
    }

    /**
     * 🔥 Kiểm tra window hợp lệ
     */
    private boolean isWindowValid() {
        return config.getWindowWidth() > 0 && config.getWindowHeight() > 0;
    }

    /**
     * 🔥 Lấy vùng scan
     */
    private Rectangle getScanRegion() {

        if (dynamicRegion != null) {
            return dynamicRegion;
        }

        int width = config.getRegionWidth() > 0
                ? config.getRegionWidth()
                : config.getWindowWidth();

        int height = config.getRegionHeight() > 0
                ? config.getRegionHeight()
                : config.getWindowHeight();

        return new Rectangle(
                config.getWindowX(),
                config.getWindowY(),
                width,
                height
        );
    }

    // ================= CONTROL =================

    public synchronized void start() {

        if (running) return;

        if (!isWindowValid()) {
            System.out.println("❌ Cannot start bot: chưa chọn window!");
            return;
        }

        running = true;

        botThread = new Thread(this::loop, "toolvpt-bot-thread");
        botThread.setDaemon(true); // 🔥 không block JVM
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
        if (target != null) {
            this.currentTarget = target;
            System.out.println("🎯 Set target: " + target);
        }
    }

    public void updateRegion(Rectangle rect) {
        if (rect != null) {
            this.dynamicRegion = new Rectangle(rect);
            System.out.println("📐 Updated scan region: " + rect);
        }
    }

    // ================= LOAD TEMPLATE =================

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
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new RuntimeException("❌ Missing file: " + path);
        }

        return ImageIO.read(is);
    }
}