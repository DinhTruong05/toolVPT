package com.example.toolvpt.infrastructure.input;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.event.InputEvent;

@Component
public class InputController {

    private final Robot robot;

    public InputController() throws AWTException {
        this.robot = new Robot();
    }

    public void click(int x, int y) {
        robot.mouseMove(x, y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public void pressSpace() {
        robot.keyPress(java.awt.event.KeyEvent.VK_SPACE);
        robot.keyRelease(java.awt.event.KeyEvent.VK_SPACE);
    }
}