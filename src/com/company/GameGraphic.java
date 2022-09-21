package com.company;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class GameGraphic extends Game{
    /* 	Vẽ các đối tượng đồ họa trong game.
		Tất cả các tọa độ của các đối tượng trên màn hình
		sẽ là địa chỉ tương đối với các mốc được chọn sắn.
	*/

    // Kích thước cửa sổ
    static final int W_FRAME = 910;
    static final int H_FRAME = 710;

    // Tọa độ của position 0
    static final int x0_position = 287;
    static final int y0_position = 12;

    /* Các tọa độ cơ sở của chuồng, đích đến */
    static final Coordinate baseStableCoor[] = {null};
    static final Coordinate baseDestinationCoor[] = {null, new Coordinate (x0_position + DISTANCE, y0_position + DISTANCE),
            new Coordinate (x0_position - 5 * DISTANCE, y0_position + 7 * DISTANCE),
            new Coordinate (x0_position + 1 * DISTANCE, y0_position + 13 * DISTANCE),
            new Coordinate (x0_position + 7 * DISTANCE, y0_position + 7 * DISTANCE)};

    private JPanel mapPanel;
    private JPanel controlPanel;
    private JFrame mainFrame;
    private JButton xuatQuanButton, dropButton;

    private Icon iconDie[], iconHorse[];
    private Image imMap;
    private Image imControl;
    private JLabel labelDie, turnLabel;

    static URL getImage(String imageName){
        return GameGraphic.class.getClassLoader().getResource("img/"+ imageName);
    }

    private Image scaleImage(Image image, int w, int h) {
        Image scaled = image.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return scaled;
    }


    void prepareDie(){

        final int numberSide = 7;
        iconDie = new ImageIcon[numberSide];
        for (int i = 0; i < numberSide; i++){
            ImageIcon imageIcon = new ImageIcon(getImage(String.format("D%d.jpg", i)));
            Image scale = scaleImage(imageIcon.getImage(), 50, 50);
            iconDie[i] = new ImageIcon(scale);
        }
    }

    void prepareHorse() {
        iconHorse = new ImageIcon[Player.NUMBER_HORSE + 1];
        for (int i = 1; i <= Player.NUMBER_HORSE; i++) {
            ImageIcon imageIcon = new ImageIcon(getImage(String.format("H%d.png", i)));
            Image scale = scaleImage(imageIcon.getImage(), 35, 35);
            iconHorse[i] = new ImageIcon(scale);
        }
    }

    void prepareMap() {
        imMap = new ImageIcon(getImage("BanCo.png")).getImage();
    }


    GameGraphic() {
        mainFrame = new JFrame();
        mainFrame.setSize(W_FRAME, H_FRAME);
        mainFrame.setBackground(Color.black);
        mainFrame.setTitle("Cờ Cá Ngựa");
        mainFrame.setIconImage(new ImageIcon(getImage("H2.png")).getImage());
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        prepareMap();
        prepareHorse();
        prepareDie();
    }

    private final int point[] = {0, 6, 12, 14, 20, 26, 28, 34, 40, 42, 48, 54, 56};// Vị trí các điểm mốc trên bàn cờ
    private final int sign[] = { 1, 1, -1, 1, 1, 1, 1, -1, 1, -1, -1, -1, -1};// Dấu trừ thể hiện đi ngược chiều trục tọa độ

    /* Ánh xạ từ độ trong map ra tọa độ trên màn hình */
    public Coordinate getCoordinate(HorseSea horse) {
        Coordinate coor = new Coordinate(x0_position, y0_position);
        int position = horse.getPosition();

        /* Tính toán tọa độ khi quân cờ đang ở vị trí đích đến cuối cùng */
        if (position == HorseSea.FINISH_POSITION) {
            int color = horse.getColor();
            coor.x = baseDestinationCoor[color].x;
            coor.y = baseDestinationCoor[color].y;

            if (color == Yellow) {
                coor.y += DISTANCE * horse.getRank();
            } else if (color == Green) {
                coor.x += DISTANCE * horse.getRank();
            } else if (color == Blue) {
                coor.y -= DISTANCE * horse.getRank();
            } else if (color == Red) {
                coor.x -= DISTANCE * horse.getRank();
            }
            return coor;
        }

        /* Tính toán tọa độ của quân cờ dựa vào tọa độ cơ sở x0, y0 */
        for (int i = 1; i < point.length; i++) {
            boolean oddFlag = (i % 2 != 0);

            if (position < point[i]) {
                if (oddFlag) {
                    coor.y += sign[i] * DISTANCE * (position - point[i - 1]);
                } else {
                    coor.x += sign[i] * DISTANCE * (position - point[i - 1]);
                }
                return coor;
            }

            if (oddFlag) {
                coor.y += sign[i] * DISTANCE * (point[i] - point[i - 1]);
            } else {
                coor.x += sign[i] * DISTANCE * (point[i] - point[i - 1]);
            }
        }
        return coor;
    }

    public void drawHorse(HorseSea horse){
        Coordinate coor = getCoordinate(horse); //vị trí cờ trên màn hình

        horse.setIcon(iconHorse);
        horse.getLabel().setBounds(coor.x, coor.y, 35, 35);
        mapPanel.add(horse.getLabel());
        mainFrame.setVisible(true);
    }

    public void drawDie() {
        labelDie = new JLabel(iconDie[0]);
        labelDie.setBackground(Color.black);
        controlPanel.add(labelDie);
        mainFrame.setVisible(true);
    }

    public void drawThrowButton(Dice dice){
        class AnimationDie implements Runnable{
            Thread thread = null;

            AnimationDie(){
                thread = new Thread(this);
                thread.start();
            }

            public void run(){
                // Tạo hiệu ướng tung xúc xắc
                for (int i = 1; i < 15; i++){
                    dice.thrown();
                    labelDie.setBackground(Color.black);
                    labelDie.setIcon(iconDie[dice.getSteps()]);
                    sleep(100);
                }

                mainFrame.setVisible(true);
                diePhaseSema.release();
            }
        }

        JButton throwButton = new JButton("Đổ");

        throwButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (diePhaseFlag) {
                    diePhaseFlag = false;
                    new AnimationDie();
                }
            }
        });

        controlPanel.add(throwButton);
        mainFrame.setVisible(true);
    }

    public void drawDropButton(){
        dropButton = new JButton("Bỏ lược");
        dropButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (horsePhaseFlag) {
                    horsePhaseFlag = false;
                    horsePhaseSema.release();
                }
            }
        });

        controlPanel.add(dropButton);
        mainFrame.setVisible(true);
    }

    public void drawTurnLabel(int color) {
        turnLabel.setIcon(iconHorse[color]);
    }

    public void drawXuatQuanButton(GameMap map, int color) {
        xuatQuanButton = new JButton("Xuất quân");
        xuatQuanButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (horsePhaseFlag) {
                    if (map.xuatQuan(color)) {
                        horsePhaseFlag = false;
                        horsePhaseSema.release();
                    }
                }
            }
        });
        controlPanel.add(xuatQuanButton);
        mainFrame.setVisible(true);
    }

    public void removeXuatQuanButton() {
        if (xuatQuanButton != null) {
            xuatQuanButton.setVisible(false);
        }
    }

    public void drawTurnLabel() {
        turnLabel = new JLabel("");
        turnLabel.setBackground(Color.black);
        turnLabel.setOpaque(true);
        controlPanel.add(turnLabel);
    }

    public void drawControl(Dice dice) {
        controlPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imControl, 0, 100, W_FRAME - (W_FRAME - 215), H_FRAME - 15 - 100 + 35 - 8, this); //ve nen
            }
        };
        controlPanel.setBackground(Color.black);
        mainFrame.add(controlPanel);

        drawTurnLabel();
        drawDie();
        drawThrowButton(dice);
        drawDropButton();

        mainFrame.setVisible(true);
    }

    public void drawMap(GameMap map) {
        mapPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(imMap, 0, -20, W_FRAME - 215, H_FRAME - 15, this); //ve nen
            }
        };
        mapPanel.setPreferredSize(new Dimension(W_FRAME - 215, H_FRAME - 15));
        mapPanel.setLayout(null);

        int num = map.getNumberPlayer();
        for (int i = 1; i <= num; i++) {
            for (int j = 0; j < Player.NUMBER_HORSE; j++) {
                if (map.getPlayer()[i].horse[j] != null) {
                    drawHorse(map.getPlayer()[i].horse[j]);
                }
            }
        }

        mainFrame.add(mapPanel, BorderLayout.WEST);
        mainFrame.setVisible(true);
    }
}
