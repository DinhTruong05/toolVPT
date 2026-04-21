package com.example.toolvpt.detector;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

        // ====== CẤU HÌNH ======

        // Kích thước cửa sổ game
        int windowWidth = 1496;
        int windowHeight = 936;

        // Vị trí cửa sổ trên màn hình (CHỈNH nếu game không ở góc trái)
        int windowX = 0;
        int windowY = 0;

        // Vùng check (ROI)
        int regionWidth = 320;
        int regionHeight = 180;

        // Ngưỡng phân biệt
        double maxAcceptableDistance = 80.0;

        // ====== TÍNH ROI ======
        Rectangle centerRegion = ImageUtils.centerRegion(windowWidth, windowHeight, regionWidth, regionHeight);

        Rectangle screenRegion = new Rectangle(
                windowX + centerRegion.x,
                windowY + centerRegion.y,
                centerRegion.width,
                centerRegion.height
        );

        System.out.println("ROI screen = " + screenRegion);

        // ====== LOAD SAMPLE (từ resources) ======
        BufferedImage idleSample = ImageUtils.loadFromResource("samples/idle.png");
        BufferedImage fightingSample = ImageUtils.loadFromResource("samples/fighting.png");

        // ====== VALIDATE ======
        if (idleSample.getWidth() != regionWidth || idleSample.getHeight() != regionHeight) {
            throw new IllegalArgumentException("idle.png phải đúng size: "
                    + regionWidth + "x" + regionHeight);
        }

        if (fightingSample.getWidth() != regionWidth || fightingSample.getHeight() != regionHeight) {
            throw new IllegalArgumentException("fighting.png phải đúng size: "
                    + regionWidth + "x" + regionHeight);
        }

        // ====== INIT ======
        ScreenCaptureService captureService = new ScreenCaptureService();

        BattleStateDetector detector = new BattleStateDetector(
                idleSample,
                fightingSample,
                maxAcceptableDistance
        );

        GameState lastState = GameState.UNKNOWN;

        System.out.println("🚀 Bắt đầu detect...");

        // ====== LOOP ======
        while (true) {

            // Capture màn hình
            BufferedImage current = captureService.capture(screenRegion);

            // DEBUG: lưu ảnh ROI để check (có thể tắt nếu không cần)
            ImageIO.write(current, "png", new File("debug.png"));

            // Detect state
            BattleStateDetector.DetectionResult result = detector.detect(current);

            // Chỉ log khi state thay đổi
            if (result.getState() != lastState) {

                System.out.println(
                        "[STATE] " + result.getState()
                                + " | idle=" + String.format("%.2f", result.getDistanceToIdle())
                                + " | fighting=" + String.format("%.2f", result.getDistanceToFighting())
                );

                // Detect chuyển trạng thái
                if (lastState == GameState.FIGHTING && result.getState() == GameState.IDLE) {
                    System.out.println("🔥 VỪA ĐÁNH XONG!");
                }

                if (result.getState() == GameState.IDLE) {
                    System.out.println("=> Trạng thái: IDLE (bình thường)");
                } else if (result.getState() == GameState.FIGHTING) {
                    System.out.println("=> Trạng thái: FIGHTING (đang đánh)");
                } else {
                    System.out.println("=> Trạng thái: UNKNOWN");
                }

                lastState = result.getState();
            }

            Thread.sleep(300);
        }
    }
}