package com.example.toolvpt.infrastructure.input;

import com.example.toolvpt.application.BotController;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Component
    public static class GlobalStopHotkeyService {

        private static final Logger log = LoggerFactory.getLogger(GlobalStopHotkeyService.class);

        private static final int HOTKEY_ID_STOP = 1001;
        private static final int MOD_NOREPEAT = 0x4000;
        private static final int VK_F8 = 0x77;

        private final BotController controller;

        private volatile boolean running = false;
        private volatile int hotkeyThreadId = 0;
        private Thread hotkeyThread;

        public GlobalStopHotkeyService(BotController controller) {
            this.controller = controller;
        }

        @PostConstruct
        public void start() {
            if (!isWindows()) {
                log.warn("Global stop hotkey F8 is only supported on Windows");
                return;
            }

            running = true;
            hotkeyThread = new Thread(this::listenForHotkey, "toolvpt-global-stop-hotkey");
            hotkeyThread.setDaemon(true);
            hotkeyThread.start();
        }

        @PreDestroy
        public void stop() {
            running = false;

            if (hotkeyThreadId != 0) {
                User32.INSTANCE.PostThreadMessage(
                        hotkeyThreadId,
                        WinUser.WM_QUIT,
                        new WinDef.WPARAM(0),
                        new WinDef.LPARAM(0)
                );
            }
        }

        private void listenForHotkey() {
            hotkeyThreadId = Kernel32.INSTANCE.GetCurrentThreadId();

            boolean registered = User32.INSTANCE.RegisterHotKey(
                    null,
                    HOTKEY_ID_STOP,
                    MOD_NOREPEAT,
                    VK_F8
            );

            if (!registered) {
                log.warn("Cannot register global stop hotkey F8. Win32 error={}", Native.getLastError());
                return;
            }

            log.info("✅ Global stop hotkey registered: F8");

            try {
                WinUser.MSG msg = new WinUser.MSG();
                while (running) {
                    int result = User32.INSTANCE.GetMessage(msg, null, 0, 0);
                    if (result <= 0) {
                        break;
                    }

                    if (msg.message == WinUser.WM_HOTKEY && msg.wParam.intValue() == HOTKEY_ID_STOP) {
                        controller.stop();
                        log.info("🛑 Bot stopped by global hotkey F8");
                    }
                }
            } finally {
                User32.INSTANCE.UnregisterHotKey(null, HOTKEY_ID_STOP);
                log.info("Global stop hotkey unregistered");
            }
        }

        private boolean isWindows() {
            return System.getProperty("os.name", "").toLowerCase().contains("win");
        }
    }
}