package com.example.toolvpt.detector;

import javax.swing.*;
import java.awt.*;

public class MainUI extends JFrame {

    private final JLabel statusLabel;
    private final JButton startBtn;
    private final JButton stopBtn;

    private volatile boolean running = false;

    public MainUI() {
        setTitle("Tool Auto VPT");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        statusLabel = new JLabel("Trạng thái: STOP", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));

        startBtn = new JButton("Start");
        stopBtn = new JButton("Stop");

        stopBtn.setEnabled(false);

        startBtn.addActionListener(e -> startDetect());
        stopBtn.addActionListener(e -> stopDetect());

        JPanel panel = new JPanel();
        panel.add(startBtn);
        panel.add(stopBtn);

        add(statusLabel, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
    }

    private void startDetect() {
        running = true;
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        statusLabel.setText("Đang chạy...");

        new Thread(this::runDetect).start();
    }

    private void stopDetect() {
        running = false;
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        statusLabel.setText("Đã dừng");
    }

    private void runDetect() {
        try {
            ToolRunner runner = new ToolRunner();

            while (running) {
                GameState state = runner.detect();

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Trạng thái: " + state);
                });

                Thread.sleep(300);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainUI().setVisible(true);
        });
    }
}