package com.example.toolvpt.ui;

import com.example.toolvpt.application.BotController;
import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.window.WindowScanner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainUI extends JFrame {

    private final JLabel statusLabel = new JLabel("STOP");
    private final JLabel selectedWindowLabel = new JLabel("No window selected");

    private final JButton startBtn = new JButton("Start");
    private final JButton stopBtn = new JButton("Stop");
    private final JButton refreshBtn = new JButton("⟳");

    private final JComboBox<String> targetBox =
            new JComboBox<>(new String[]{"Orc", "Boss", "All"});

    private final JComboBox<WindowScanner.WindowInfo> windowBox =
            new JComboBox<>();

    private final BotController controller;
    private final ToolVptProperties config;
    private final WindowScanner scanner;

    public MainUI(BotController controller,
                  ToolVptProperties config,
                  WindowScanner scanner) {

        this.controller = controller;
        this.config = config;
        this.scanner = scanner;

        initUI();
        loadWindows();
        bindEvents();
    }

    // ================= UI =================

    private void initUI() {

        setTitle("🔥 Tool Auto VPT");
        setSize(520, 340);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // ===== STATUS =====
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(244, 67, 54));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(new EmptyBorder(10, 0, 10, 0));

        root.add(statusLabel, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(Color.WHITE);

        center.add(createField("🎯 Target", targetBox));
        center.add(Box.createVerticalStrut(12));
        center.add(createField("🪟 Window", createWindowPanel()));
        center.add(Box.createVerticalStrut(12));
        center.add(createField("📌 Selected", selectedWindowLabel));

        root.add(center, BorderLayout.CENTER);

        // ===== BUTTON =====
        JPanel bottom = new JPanel(new GridLayout(1, 2, 12, 0));
        bottom.setBackground(Color.WHITE);

        styleButton(startBtn, new Color(76, 175, 80));
        styleButton(stopBtn, new Color(244, 67, 54));

        stopBtn.setEnabled(false);

        bottom.add(startBtn);
        bottom.add(stopBtn);

        root.add(bottom, BorderLayout.SOUTH);
    }

    private JPanel createField(String labelText, JComponent comp) {

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setBackground(new Color(250, 250, 250));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(new Color(60, 60, 60));

        panel.add(label, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 8, 8, 8)
        ));

        return panel;
    }

    private JPanel createWindowPanel() {

        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);

        styleCombo(windowBox);

        refreshBtn.setFocusPainted(false);
        refreshBtn.setBackground(new Color(33, 150, 243));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setPreferredSize(new Dimension(40, 30));

        panel.add(windowBox, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.EAST);

        return panel;
    }

    // 🔥 combo xịn hơn
    private void styleCombo(JComboBox<?> box) {

        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setBackground(Color.WHITE);

        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus
                );

                label.setBorder(new EmptyBorder(5, 10, 5, 10));

                if (value != null) {
                    label.setText("🪟 " + value.toString());
                }

                if (isSelected) {
                    label.setBackground(new Color(33, 150, 243));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(new Color(40, 40, 40));
                }

                return label;
            }
        });
    }

    private void styleButton(JButton btn, Color color) {
        btn.setFocusPainted(false);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    // ================= LOGIC =================

    private void loadWindows() {
        windowBox.removeAllItems();
        for (var w : scanner.listWindows()) {
            windowBox.addItem(w);
        }
    }

    private void bindEvents() {

        refreshBtn.addActionListener(e -> loadWindows());

        startBtn.addActionListener(e -> start());
        stopBtn.addActionListener(e -> stop());

        targetBox.addActionListener(e ->
                controller.setTarget((String) targetBox.getSelectedItem())
        );

        windowBox.addActionListener(e -> selectWindow());
    }

    private void selectWindow() {
        var selected = (WindowScanner.WindowInfo) windowBox.getSelectedItem();
        if (selected == null) return;

        Rectangle rect = scanner.getRectangle(selected.getHwnd());

        config.setWindowX(rect.x);
        config.setWindowY(rect.y);
        config.setWindowWidth(rect.width);
        config.setWindowHeight(rect.height);

        scanner.focus(selected.getHwnd());

        selectedWindowLabel.setText("✅ " + selected.getTitle());
    }

    private void start() {

        if (config.getWindowWidth() <= 0) {
            JOptionPane.showMessageDialog(this, "Select window first!");
            return;
        }

        controller.start();

        statusLabel.setText("RUNNING");
        statusLabel.setBackground(new Color(76, 175, 80));

        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
    }

    private void stop() {
        controller.stop();

        statusLabel.setText("STOP");
        statusLabel.setBackground(new Color(244, 67, 54));

        startBtn.setEnabled(true);
        stopBtn.setEnabled(false);
    }
}