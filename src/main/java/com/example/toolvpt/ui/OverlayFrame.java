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

        // 🔥 Drag di chuyển khung
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int newX = getX() + e.getX() - dragStart.x;
                int newY = getY() + e.getY() - dragStart.y;

                setLocation(newX, newY);
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        // ===== Khung =====
        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        // ===== Info debug =====
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.setColor(Color.GREEN);

        g2.drawString("SCAN ZONE", 10, 20);
        g2.drawString("X=" + getX() + " Y=" + getY(), 10, 40);
        g2.drawString("W=" + getWidth() + " H=" + getHeight(), 10, 60);
    }
}