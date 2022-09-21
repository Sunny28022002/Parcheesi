package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class GameSession extends Game {

    private final int ONE_BONUS = -1;
    private final int NO_BONUS = 0;

    private int turn = 1, turnBonus = 0;
    private GameMap map;
    private boolean endGameFlag;
    private GameGraphic graphic;
    private Dice dice;

    GameSession() {
        setTurn();
        map = new GameMap();
        dice = new Dice();
        graphic = new GameGraphic();
        endGameFlag = false;
        graphic.drawMap(map);
        graphic.drawControl(dice);
    }

    public void drawBackGround(){
        final JFrame frame = new JFrame();
        frame.setSize(910,710);
        JPanel background;
        Image image = new ImageIcon(GameGraphic.getImage("Background.png")).getImage();
        background = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, -20, 910 , 710 , this); //ve nen
            }
        };
        background.setPreferredSize(new Dimension(910, 710));
        background.setLayout(null);

        frame.setSize(910, 710);
        frame.setBackground(Color.black);
        frame.setTitle("Cờ Cá Ngựa");
        frame.setIconImage(new ImageIcon(GameGraphic.getImage("H2.png")).getImage());
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        URL url = GameGraphic.getImage("PlayButton.png");
        Image play = Toolkit.getDefaultToolkit().createImage(url);

        play = play.getScaledInstance(300, 150, Image.SCALE_SMOOTH);
        JButton playButton = new JButton();
        playButton.setIcon(new ImageIcon(play));
        playButton.setBounds(300,450, 300, 150);
        playButton.setBorder(new EmptyBorder(0,0,0,0));
        playButton.setContentAreaFilled(false);

        background.add(playButton);
        frame.add(background);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);

        playButton.addActionListener(e -> {
            frame.setVisible(false);
        });
    }
    
    public void setTurn() {
        while (true) {
            String strTurn = JOptionPane.showInputDialog(null, "Nhập player đi trước (1/ 2/ 3/ 4): ", JOptionPane.INFORMATION_MESSAGE);

            if (strTurn.matches("[1234]")) {
                turn =  Integer.parseInt(strTurn);
                break;
            } else {
                showError("Bạn nhập sai player. Xin mời nhập lại.");
            }
        }
    }

    public void playGame() {
        while (!endGameFlag) {
            int color = turn;
            turnBonus = NO_BONUS;
            graphic.drawTurnLabel(color);
            try {
                diePhaseSema.acquire();
            } catch (InterruptedException e) {
                System.out.println(e);
            }
            horsePhaseFlag = true;

            int steps = dice.getSteps();
            if (steps == 6 || steps == 1) {
                turnBonus = ONE_BONUS;
                graphic.drawXuatQuanButton(map, color);
            }

            map.addPlayerListener(color, steps);
            try {
                horsePhaseSema.acquire();
            } catch (InterruptedException exc) {
                System.out.println(exc);
            }

            map.removePlayerListener(color);
            graphic.removeXuatQuanButton();
            horsePhaseFlag = false;
            graphic.drawMap(map);

            diePhaseFlag = true;
            turn = (turn + turnBonus) % map.getNumberPlayer() + 1;

            if (map.isWin()) {
                endGameFlag = true;
            }

        }
    }
}
