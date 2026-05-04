package com.example.toolvpt;

import com.example.toolvpt.application.BotController;
import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.ui.MainUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class ToolVptApplication {

    private static final Logger log = LoggerFactory.getLogger(ToolVptApplication.class);

    public static void main(String[] args) {

        // 🔥 Fix headless & DPI
        System.setProperty("java.awt.headless", "false");
        System.setProperty("sun.java2d.uiScale", "1.0");

        SpringApplication.run(ToolVptApplication.class, args);
    }

    /**
     * 🔥 Inject trực tiếp bean (clean hơn)
     */
    @Bean
    public CommandLineRunner run(BotController controller,
                                 ToolVptProperties config) {

        return args -> {

            log.info("Is headless: {}", GraphicsEnvironment.isHeadless());

            // 🔥 chạy UI đúng thread
            SwingUtilities.invokeLater(() -> {
                try {

                    setupLookAndFeel();

                    log.info("Starting Tool VPT UI...");

                    MainUI ui = new MainUI(controller, config);
                    ui.setLocationRelativeTo(null);
                    ui.setVisible(true);

                    log.info("UI started successfully");

                } catch (Exception e) {
                    log.error("Failed to start UI", e);

                    // ❗ không exit thô
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

    /**
     * 🔥 UI đẹp hơn chút 😄
     */
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
    }
}