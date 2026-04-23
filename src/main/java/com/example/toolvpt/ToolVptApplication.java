package com.example.toolvpt;

import com.example.toolvpt.application.BotController;
import com.example.toolvpt.application.BotEngine;
import com.example.toolvpt.application.ToolService;
import com.example.toolvpt.ui.MainUI;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.swing.SwingUtilities;

@SpringBootApplication
public class ToolVptApplication {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(ToolVptApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(ApplicationContext context) {
        return args -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    ToolService service = context.getBean(ToolService.class);

                    BotEngine engine = new BotEngine(service);
                    BotController controller = new BotController(engine);

                    new MainUI(controller).setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        };
    }
}