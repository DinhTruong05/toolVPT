package com.example.toolvpt;

import com.example.toolvpt.application.BotController;
import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.ui.MainUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

@SpringBootApplication
public class ToolVptApplication {

    private static final Logger log = LoggerFactory.getLogger(ToolVptApplication.class);

    public static void main(String[] args) {
        // 🔥 Fix headless & DPI scale (rất quan trọng cho Robot + UI)
        System.setProperty("java.awt.headless", "false");
        System.setProperty("sun.java2d.uiScale", "1.0");

        SpringApplication.run(ToolVptApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ApplicationContext context) {
        return args -> {
            // 🔍 Debug môi trường
            log.info("Is headless: {}", GraphicsEnvironment.isHeadless());

            // 🚀 Luôn chạy UI trên EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    log.info("Starting Tool VPT UI...");

                    ToolVptProperties config = context.getBean(ToolVptProperties.class);
                    BotController controller = context.getBean(BotController.class);

                    MainUI ui = new MainUI(controller, config);
                    ui.setLocationRelativeTo(null); // center màn hình
                    ui.setVisible(true);

                    log.info("UI started successfully");

                } catch (Exception e) {
                    log.error("Failed to start Tool VPT UI", e);
                    System.exit(1);
                }
            });
        };
    }
}