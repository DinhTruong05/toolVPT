package com.example.toolvpt.infrastructure.window;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.*;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class WindowScanner {

    public static class WindowInfo {
        private final HWND hwnd;
        private final String title;

        public WindowInfo(HWND hwnd, String title) {
            this.hwnd = hwnd;
            this.title = title;
        }

        public HWND getHwnd() {
            return hwnd;
        }

        public String getTitle() {
            return title;
        }

        @Override
        public String toString() {
            return title; // 🔥 chỉ hiển thị title thôi
        }
    }

    /**
     * 🔥 Lấy danh sách window hợp lệ
     */
    public List<WindowInfo> listWindows() {
        List<WindowInfo> list = new ArrayList<>();

        User32.INSTANCE.EnumWindows((hwnd, data) -> {

            // ❌ bỏ window ẩn
            if (!User32.INSTANCE.IsWindowVisible(hwnd)) {
                return true;
            }

            // ❌ bỏ window không có title
            String title = getWindowTitle(hwnd);
            if (title.isBlank()) {
                return true;
            }

            // ❌ bỏ window system/tool (optional nhưng nên)
            if (title.equals("Program Manager")) {
                return true;
            }

            list.add(new WindowInfo(hwnd, title));
            return true;

        }, Pointer.NULL);

        return list;
    }

    /**
     * 🔥 Lấy title chuẩn (fix null char)
     */
    private String getWindowTitle(HWND hwnd) {
        char[] buffer = new char[512];

        int len = User32.INSTANCE.GetWindowText(hwnd, buffer, 512);

        if (len == 0) return "";

        return new String(buffer, 0, len);
    }

    /**
     * 🔥 Tìm window theo keyword (quan trọng)
     */
    public WindowInfo findByTitle(String keyword) {

        return listWindows().stream()
                .filter(w -> w.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 🔥 Lấy rectangle chuẩn
     */
    public Rectangle getRectangle(HWND hwnd) {

        RECT rect = new RECT();
        User32.INSTANCE.GetWindowRect(hwnd, rect);

        int x = rect.left;
        int y = rect.top;
        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;

        return new Rectangle(x, y, width, height);
    }

    /**
     * 🔥 Focus window (rất cần cho bot)
     */
    public void focus(HWND hwnd) {
        User32.INSTANCE.SetForegroundWindow(hwnd);
    }
}