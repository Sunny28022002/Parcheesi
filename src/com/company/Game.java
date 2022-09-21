package com.company;

import javax.swing.*;
import java.util.concurrent.Semaphore;

public class Game {
    static final int Blue = 3;
    static final int Yellow = 1;
    static final int Green = 2;
    static final int Red = 4;
    static final int DISTANCE = 43; // Khoảng cách của 2 nốt

    // Các giai đoạn trong game
    static boolean diePhaseFlag = true;// Giai đoạn tung xúc xắc
    static boolean horsePhaseFlag = false; // Giai đoạn đi
    static Semaphore diePhaseSema = new Semaphore(0);
    static Semaphore horsePhaseSema = new Semaphore(0);

    public void showError(String error) {
        JOptionPane.showMessageDialog(null, error);
    }

    public void sleep(int miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
