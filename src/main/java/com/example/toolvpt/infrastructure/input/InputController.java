package com.example.toolvpt.infrastructure.input;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class InputController {

    private final Robot robot;

    public InputController() throws AWTException {
        this.robot = new Robot();
        this.robot.setAutoDelay(10); // delay nhẹ giữa các event
    }

    /**
     * Click chính xác (dùng khi cần)
     */
    public synchronized void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * 🔥 Click có random offset (anti-detect)
     */
    public synchronized void clickHuman(int x, int y) {

        int offsetX = random(-3, 3);
        int offsetY = random(-3, 3);

        int targetX = x + offsetX;
        int targetY = y + offsetY;

        moveSmooth(targetX, targetY);

        sleepRandom(30, 80);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        sleepRandom(20, 50);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    /**
     * Di chuyển chuột mượt hơn (giống người)
     */
    private void moveSmooth(int targetX, int targetY) {

        Point current = MouseInfo.getPointerInfo().getLocation();

        int steps = 10;

        for (int i = 1; i <= steps; i++) {
            int x = current.x + (targetX - current.x) * i / steps;
            int y = current.y + (targetY - current.y) * i / steps;

            robot.mouseMove(x, y);
            sleepRandom(5, 15);
        }
    }

    public synchronized void pressSpace() {
        robot.keyPress(KeyEvent.VK_SPACE);
        sleepRandom(20, 50);
        robot.keyRelease(KeyEvent.VK_SPACE);
    }

    // ================= UTIL =================

    private int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    private void sleepRandom(int minMs, int maxMs) {
        try {
            Thread.sleep(random(minMs, maxMs));
        } catch (InterruptedException ignored) {
        }
    }
}