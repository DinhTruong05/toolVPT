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

    // 🔥 giữ reference để stop
    private Timer overlayTimer;

    public MainUI(BotController controller, ToolVptProperties config) {
        this.controller = controller;
        this.config = config;

        initUI();
        bindEvents();
    }

    // ================= INIT =================

    private void initUI() {
        setTitle("Tool Auto VPT");
        setSize(420, 280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));

        JPanel targetPanel = new JPanel();
        targetPanel.add(new JLabel("Target:"));
        targetPanel.add(targetBox);

        JLabel hotkeyLabel = new JLabel("Hotkey: F6 Start/Stop", SwingConstants.CENTER);

        centerPanel.add(targetPanel);
        centerPanel.add(hotkeyLabel);
        centerPanel.add(overlayBtn);

        add(centerPanel, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.add(startBtn);
        panel.add(stopBtn);

        add(panel, BorderLayout.SOUTH);

        stopBtn.setEnabled(false);
    }

    private void bindEvents() {

        startBtn.addActionListener(e -> start());
        stopBtn.addActionListener(e -> stop());

        overlayBtn.addActionListener(e -> toggleOverlay());

        // 🔥 hotkey
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED &&
                            e.getKeyCode() == KeyEvent.VK_F6) {

                        SwingUtilities.invokeLater(this::toggle);
                        return true;
                    }
                    return false;
                });

        // 🔥 target change
        targetBox.addActionListener(e -> {
            String selected = (String) targetBox.getSelectedItem();
            controller.setTarget(selected);
        });
    }

    // ================= OVERLAY =================

    private void toggleOverlay() {

        if (overlay == null) {
            showOverlay();
        } else {
            hideOverlay();
        }
    }

    private void showOverlay() {

        overlay = new OverlayFrame(
                config.getWindowX(),
                config.getWindowY(),
                config.getRegionWidth(),
                config.getRegionHeight()
        );

        overlay.setVisible(true);
        overlayBtn.setText("Hide Overlay");

        // sync lần đầu
        controller.updateRegion(overlay.getBounds());

        // 🔥 timer có kiểm soát
        overlayTimer = new Timer(200, e -> {
            if (overlay != null) {
                controller.updateRegion(overlay.getBounds());
            }
        });

        overlayTimer.start();
    }

    private void hideOverlay() {

        if (overlayTimer != null) {
            overlayTimer.stop();
            overlayTimer = null;
        }

        if (overlay != null) {
            overlay.dispose();
            overlay = null;
        }

        overlayBtn.setText("Show Overlay");
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