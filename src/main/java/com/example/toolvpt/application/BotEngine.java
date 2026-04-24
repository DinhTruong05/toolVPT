package com.example.toolvpt.application;

import com.example.toolvpt.domain.decision.BotAction;
import com.example.toolvpt.domain.decision.DecisionEngine;
import com.example.toolvpt.infrastructure.input.InputController;
import com.example.toolvpt.infrastructure.screen.ScreenCaptureService;
import com.example.toolvpt.infrastructure.screen.TemplateMatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public class BotEngine {

    private final ToolService service;
    private final DecisionEngine decision;
    private final InputController input;
    private final ScreenCaptureService screenService;
    private final TemplateMatcher matcher;
    private final TargetFinder targetFinder;

    private List<BufferedImage> templates;

    private volatile boolean running = false;

    // 🎯 target từ UI
    private String currentTarget = "All";

    // 🛑 anti spam click
    private long lastClickTime = 0;

    public BotEngine(ToolService service) throws Exception {
        this.service = service;
        this.decision = new DecisionEngine();
        this.input = new InputController();
        this.screenService = new ScreenCaptureService();
        this.matcher = new TemplateMatcher();

        // ✅ load template trước
        this.templates = loadTemplates();

        // ✅ inject finder
        this.targetFinder = new TargetFinder(matcher, templates);
    }

    // ================= LOAD TEMPLATE =================

    private List<BufferedImage> loadTemplates() {
        try {
            return List.of(
                    loadImage("samples/orc.png"),
                    loadImage("samples/boss.png")
            );
        } catch (Exception e) {
            throw new RuntimeException("Load template failed", e);
        }
    }

    private BufferedImage loadImage(String path) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(path);

        if (is == null) {
            throw new RuntimeException("Không tìm thấy file: " + path);
        }

        return ImageIO.read(is);
    }

    // ================= BOT CONTROL =================

    public void start() {
        if (running) return;

        running = true;
        new Thread(this::loop, "bot-thread").start();

        System.out.println("✅ Bot started");
    }

    public void stop() {
        running = false;
        System.out.println("🛑 Bot stopped");
    }

    public boolean isRunning() {
        return running;
    }

    // ================= MAIN LOOP =================

    private void loop() {
        while (running) {
            try {
                var result = service.detect();
                BotAction action = decision.decide(result.getState());

                System.out.println("State: " + result.getState() + " | Action: " + action);

                execute(action);

                sleepRandom();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ================= ACTION EXECUTION =================

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

            case IDLE, NONE -> {
                // do nothing
            }
        }
    }

    // ================= TARGET LOGIC =================

    private void searchAndClick() {
        try {
            BufferedImage screen = screenService.capture();

            Point target;

            // 🎯 chọn theo UI
            switch (currentTarget) {
                case "Orc" -> target = targetFinder.findOnly(screen, 0);
                case "Boss" -> target = targetFinder.findOnly(screen, 1);
                default -> target = targetFinder.findNearest(screen);
            }

            // 🛑 anti spam
            if (target != null && System.currentTimeMillis() - lastClickTime > 1200) {

                input.click(target.x, target.y);
                lastClickTime = System.currentTimeMillis();

                System.out.println("🎯 Click target: " + target.x + ", " + target.y);

            } else if (target == null) {
                System.out.println("❌ No target found");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= TARGET SET =================

    public void setTarget(String target) {
        if (!target.equals(this.currentTarget)) {
            this.currentTarget = target;
            System.out.println("🎯 Set target: " + target);
        }
    }

    // ================= UTILS =================

    private void sleepRandom() {
        try {
            Thread.sleep(400 + (int) (Math.random() * 200));
        } catch (InterruptedException ignored) {}
    }
}