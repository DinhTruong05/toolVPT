package com.example.toolvpt.application;

import com.example.toolvpt.config.ToolVptProperties;
import com.example.toolvpt.infrastructure.window.WindowScanner;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.List;
import java.util.Scanner;

@Component
public class WindowSelector {

    private final WindowScanner scanner;
    private final ToolVptProperties config;

    public WindowSelector(WindowScanner scanner, ToolVptProperties config) {
        this.scanner = scanner;
        this.config = config;
    }

    public void select() {

        List<WindowScanner.WindowInfo> windows = scanner.listWindows();

        if (windows.isEmpty()) {
            System.out.println("❌ No window found");
            return;
        }

        System.out.println("==== SELECT WINDOW ====");

        for (int i = 0; i < windows.size(); i++) {
            System.out.println(i + ": " + windows.get(i).getTitle());
        }

        int index = readIndex(windows.size());

        WindowScanner.WindowInfo selected = windows.get(index);

        // 🔥 dùng Rectangle (clean hơn)
        Rectangle rect = scanner.getRectangle(selected.getHwnd());

        // 🔥 save config
        config.setWindowX(rect.x);
        config.setWindowY(rect.y);
        config.setWindowWidth(rect.width);
        config.setWindowHeight(rect.height);

        // 🔥 focus window (rất quan trọng)
        scanner.focus(selected.getHwnd());

        System.out.println("✅ Selected: " + selected.getTitle());
        System.out.println("📐 " + rect.x + "," + rect.y + " | " + rect.width + "x" + rect.height);
    }

    // ================= SAFE INPUT =================

    private int readIndex(int max) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.print("Choose index: ");
                int index = Integer.parseInt(sc.nextLine());

                if (index >= 0 && index < max) {
                    return index;
                }

                System.out.println("⚠️ Invalid index, try again");

            } catch (Exception e) {
                System.out.println("⚠️ Please enter a number");
            }
        }
    }
}