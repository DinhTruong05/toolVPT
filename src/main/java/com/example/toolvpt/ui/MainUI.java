package com.example.toolvpt.ui;

import com.example.toolvpt.application.BotController;
import com.example.toolvpt.config.ToolVptProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class MainUI extends JFrame {

    private final JLabel statusLabel = new JLabel("STOP", SwingConstants.CENTER);
    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn = new JButton("Stop");
    private final JButton overlayBtn = new JButton("Show Overlay");

    private final JComboBox<String> targetBox =
            new JComboBox<>(new String[]{"Orc", "Boss", "All"});

    private final ToolVptProperties config;
    private final BotController controller;

    private boolean running = false;
    private OverlayFrame overlay;

    public MainUI(BotController controller, ToolVptProperties config) {
        this.controller = controller;
        this.config = config;

        setTitle("Tool Auto VPT");
        setSize(420, 280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== STATUS =====
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel centerPanel = new JPanel(new GridLayout(3, 1));

        JPanel targetPanel = new JPanel();
        targetPanel.add(new JLabel("Target:"));
        targetPanel.add(targetBox);

        JLabel hotkeyLabel = new JLabel("Hotkey: F6 Start/Stop", SwingConstants.CENTER);

        centerPanel.add(targetPanel);
        centerPanel.add(hotkeyLabel);
        centerPanel.add(overlayBtn);

        add(centerPanel, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel panel = new JPanel();
        panel.add(startBtn);
        panel.add(stopBtn);

        add(panel, BorderLayout.SOUTH);

        stopBtn.setEnabled(false);

        // ===== ACTION =====
        startBtn.addActionListener(e -> start());
        stopBtn.addActionListener(e -> stop());

        overlayBtn.addActionListener(e -> toggleOverlay());

        // ===== HOTKEY =====
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F6) {
                        toggle();
                        return true;
                    }
                    return false;
                });

        // ===== TARGET CHANGE =====
        targetBox.addActionListener(e -> {
            String selected = (String) targetBox.getSelectedItem();
            controller.setTarget(selected);
        });
    }

    // ================= OVERLAY =================

    private void toggleOverlay() {
        if (overlay == null) {

            overlay = new OverlayFrame(
                    config.getWindowX(),
                    config.getWindowY(),
                    config.getRegionWidth(),
                    config.getRegionHeight()
            );

            overlay.setVisible(true);
            overlayBtn.setText("Hide Overlay");

            // 🔥 sync ngay
            controller.updateRegion(overlay.getBounds());

            // 🔥 sync liên tục khi drag
            new Timer(200, e -> {
                if (overlay != null) {
                    controller.updateRegion(overlay.getBounds());
                }
            }).start();

        } else {
            overlay.dispose();
            overlay = null;
            overlayBtn.setText("Show Overlay");
        }
    }

    // ================= CONTROL =================

    private void start() {
        System.out.println("Starting...");
        controller.start();
        running = true;

        statusLabel.setText("Running...");
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void stop() {
        controller.stop();
        running = false;

        statusLabel.setText("Stopped");
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    private void toggle() {
        if (running) stop();
        else start();
    }
}