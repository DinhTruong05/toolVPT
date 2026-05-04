package com.example.toolvpt;

import com.example.toolvpt.application.BotController;
import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.window.WindowScanner;
import com.example.toolvpt.ui.MainUI;
import com.formdev.flatlaf.FlatLightLaf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class ToolVptApplication {

    private static final Logger log = LoggerFactory.getLogger(ToolVptApplication.class);

    public static void main(String[] args) {

        // 🔥 Fix Robot + Swing + DPI
        System.setProperty("java.awt.headless", "false");
        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SpringApplication.run(ToolVptApplication.class, args);
    }

    @Bean
    public ApplicationRunner uiRunner(BotController controller,
                                      ToolVptProperties config,
                                      WindowScanner scanner) {

        return args -> {

            log.info("Is headless: {}", GraphicsEnvironment.isHeadless());

            // 🔥 đảm bảo UI luôn chạy đúng EDT
            EventQueue.invokeLater(() -> {
                try {

                    setupLookAndFeel();

                    log.info("🚀 Starting Tool VPT UI...");

                    MainUI ui = new MainUI(controller, config, scanner);
                    ui.setLocationRelativeTo(null);

                    // 🔥 handle close chuẩn
                    ui.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    ui.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            shutdown(controller, ui);
                        }
                    });

                    ui.setVisible(true);

                    // 🔥 shutdown hook (backup)
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        log.info("🛑 JVM shutdown → stopping bot...");
                        controller.stop();
                    }));

                    log.info("✅ UI started successfully");

                } catch (Exception e) {
                    log.error("❌ Failed to start UI", e);

                    JOptionPane.showMessageDialog(
                            null,
                            "Không thể khởi động UI\n" + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            });
        };
    }

    // ================= UI CONFIG =================

    private void setupLookAndFeel() {
        try {
            com.formdev.flatlaf.FlatLightLaf.setup();

            // 🔥 Force font toàn bộ UI
            Font font = new Font("Segoe UI", Font.PLAIN, 14);

            UIManager.put("defaultFont", font);

            // Fix riêng cho ComboBox + List (QUAN TRỌNG)
            UIManager.put("ComboBox.font", font);
            UIManager.put("Label.font", font);
            UIManager.put("Button.font", font);
            UIManager.put("List.font", font);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ================= SHUTDOWN =================

    private void shutdown(BotController controller, JFrame ui) {

        int confirm = JOptionPane.showConfirmDialog(
                ui,
                "Thoát tool?",
                "Confirm Exit",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            try {
                controller.stop(); // 🔥 stop bot an toàn
            } catch (Exception ignored) {
            }

            ui.dispose();
            System.exit(0);
        }
    }
}