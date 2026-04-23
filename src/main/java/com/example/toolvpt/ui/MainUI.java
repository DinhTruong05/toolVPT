package com.example.toolvpt.ui;

import com.example.toolvpt.application.BotController;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    private final JLabel statusLabel = new JLabel("STOP", SwingConstants.CENTER);
    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn = new JButton("Stop");

    public MainUI(BotController controller) {

        setTitle("Tool Auto VPT");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));

        stopBtn.setEnabled(false);

        startBtn.addActionListener(e -> {
            controller.start();
            statusLabel.setText("Running...");
            startBtn.setEnabled(false);
            stopBtn.setEnabled(true);
        });

        stopBtn.addActionListener(e -> {
            controller.stop();
            statusLabel.setText("Stopped");
            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        });

        JPanel panel = new JPanel();
        panel.add(startBtn);
        panel.add(stopBtn);

        add(statusLabel, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }
}