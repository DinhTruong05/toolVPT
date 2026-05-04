package com.example.toolvpt.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OverlayFrame extends JWindow {

    private Point dragStart;

    public OverlayFrame(int x, int y, int width, int height) {

        setBounds(x, y, width, height);
        setAlwaysOnTop(true);
        setBackground(new Color(0, 0, 0, 0));
        setFocusableWindowState(false);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                // khung
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

                // text
                g2.setFont(new Font("Arial", Font.BOLD, 14));
                g2.setColor(Color.GREEN);

                g2.drawString("SCAN ZONE", 10, 20);
                g2.drawString("X=" + getX() + " Y=" + getY(), 10, 40);
                g2.drawString("W=" + getWidth() + " H=" + getHeight(), 10, 60);
            }
        };

        panel.setOpaque(false);
        setContentPane(panel);

        // ===== DRAG =====
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int newX = getX() + e.getX() - dragStart.x;
                int newY = getY() + e.getY() - dragStart.y;

                setLocation(newX, newY);
            }
        });

        // 🔥 ESC để đóng nhanh
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("ESCAPE"), "close");

        panel.getActionMap().put("close", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }
}