package com.example.toolvpt.ui;

import com.example.toolvpt.application.BotController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MainUI extends JFrame {

    private final JLabel statusLabel = new JLabel("STOP", SwingConstants.CENTER);
    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn = new JButton("Stop");

    private final JComboBox<String> targetBox =
            new JComboBox<>(new String[]{"Orc", "Boss", "All"});

    private boolean running = false;

    public MainUI(BotController controller) {

        setTitle("Tool Auto VPT");
        setSize(420, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ===== STATUS =====
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(statusLabel, BorderLayout.NORTH);

        // ===== CENTER (target select) =====
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));

        JPanel targetPanel = new JPanel();
        targetPanel.add(new JLabel("Target:"));
        targetPanel.add(targetBox);

        centerPanel.add(targetPanel);

        JLabel hotkeyLabel = new JLabel("Hotkey: F6 Start/Stop", SwingConstants.CENTER);
        centerPanel.add(hotkeyLabel);

        add(centerPanel, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel panel = new JPanel();
        panel.add(startBtn);
        panel.add(stopBtn);

        add(panel, BorderLayout.SOUTH);

        stopBtn.setEnabled(false);

        // ===== ACTION =====
        startBtn.addActionListener(e -> start(controller));
        stopBtn.addActionListener(e -> stop(controller));

        // ===== HOTKEY (F6) =====
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(e -> {
                    if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F6) {
                        toggle(controller);
                        return true;
                    }
                    return false;
                });

        // ===== TARGET CHANGE =====
        targetBox.addActionListener(e -> {
            String selected = (String) targetBox.getSelectedItem();
            controller.setTarget(selected);
            System.out.println("🎯 Target selected: " + selected);
        });
    }

    private void start(BotController controller) {
        controller.start();
        running = true;

        statusLabel.setText("Running...");
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void stop(BotController controller) {
        controller.stop();
        running = false;

        statusLabel.setText("Stopped");
        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }

    private void toggle(BotController controller) {
        if (running) {
            stop(controller);
        } else {
            start(controller);
        }
    }
}