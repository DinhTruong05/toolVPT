package com.example.toolvpt.application;
import com.example.toolvpt.application.TargetFinder;
import com.example.toolvpt.application.ToolService;
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

    private final DecisionEngine decision;
    private final InputController input;
    private final ScreenCaptureService screenService;
    private final TargetFinder targetFinder;

    private final List<BufferedImage> templates;

    private volatile boolean running = false;
    private volatile String currentTarget = "Orc";

    private long lastClickTime = 0;
    private volatile Rectangle dynamicRegion;

    public BotEngine(ToolService service, ToolVptProperties config) throws Exception {
        this.service = service;
        this.config = config;

        this.decision = new DecisionEngine();
        this.input = new InputController();
        this.screenService = new ScreenCaptureService();

        this.templates = loadTemplates();
        this.targetFinder = new TargetFinder(new TemplateMatcher(), templates, config);
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

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("🛑 Bot loop stopped");
    }

    // ================= ACTION =================

    private void execute(BotAction action) {
        switch (action) {
            case SEARCH_ENEMY -> searchAndClick();
            case ATTACK -> input.pressSpace();
            case CLICK_REWARD -> input.click(700, 400);
            default -> {}
        }
    }

    // ================= TARGET =================

    private void searchAndClick() {
        try {
            Rectangle region = dynamicRegion != null
                    ? dynamicRegion
                    : new Rectangle(
                    config.getWindowX(),
                    config.getWindowY(),
                    config.getRegionWidth(),
                    config.getRegionHeight()
            );

            // 🔥 capture đúng vùng
            BufferedImage screen = screenService.capture(region);

            Point target;

            switch (currentTarget) {
                case "Orc" -> target = targetFinder.findOnly(screen, 0);
                case "Boss" -> target = targetFinder.findOnly(screen, 1);
                default -> target = targetFinder.findNearest(screen);
            }

            long now = System.currentTimeMillis();

            if (target != null && now - lastClickTime > 1200) {

                // ✅ CHỈ cộng offset 1 lần ở đây
                int clickX = target.x + region.x;
                int clickY = target.y + region.y;

                input.click(clickX, clickY);
                lastClickTime = now;

                System.out.println("🎯 Click: " + clickX + "," + clickY);
            } else if (target == null) {
                System.out.println("❌ No target found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= CONTROL =================

    public synchronized void start() {
        if (running) return;
        running = true;

        new Thread(this::loop, "bot-thread").start();
        System.out.println("✅ Bot started");
    }

    public synchronized void stop() {
        running = false;
        System.out.println("🛑 Bot stopped");
    }

    public boolean isRunning() {
        return running;
    }

    public void setTarget(String target) {
        this.currentTarget = target;
    }

    public void updateRegion(Rectangle rect) {
        this.dynamicRegion = new Rectangle(rect);
        System.out.println("📐 Updated scan region: " + rect);
    }

    // ================= LOAD =================

    private List<BufferedImage> loadTemplates() {
        try {
            return List.of(
                    loadImage("samples/orc.png"),
                    loadImage("samples/boss.png")
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage loadImage(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);
        if (is == null) throw new RuntimeException("Missing: " + path);
        return ImageIO.read(is);
    }
}