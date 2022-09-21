package com.company;

public class GameMap extends Game{
    // Mô tả bản đồ trong game và luật di chuyển trên bản đồ

    /* 	0: Không có cá ngựa ở vị trí này
		1x: Vị trí này có cá ngựa BLUE, STT x (x = [0; 3])
		2x: Vị trí này có cá ngựa RED
		3x: Vị trí này có cá ngựa YELLOW
		4x: Vị trí này có cá ngựa GREEN
		5x: Gía trị tượng trưng phần thưởng x
	*/

    private Player player[];
    private final int map[] = new int[NUMBER_NODE];

    static final int NUMBER_NODE = 56;
    private final int NO_HORSE = 0;
    // Vị trí đích đến của các quân cờ
    private final int DES[] = {-1, 55, 13, 27, 41};
    private final int START[] = {-1, 0, 14, 28, 42};
    private int numberPlayer;
    GameMap(){
        numberPlayer = 4;
        player = new Player[numberPlayer + 1];

        for (int color = 1; color <= numberPlayer; color++){
            player[color] = new Player(color);
        }
    }

    public int getNumberPlayer() {
        return numberPlayer;
    }

    public int getMap(int index) {
        return map[index];
    }

    public Player[] getPlayer() {
        return player;
    }


    public boolean xuatQuan(int color){
        int idHorse = player[color].stable.getHorse();

        if(idHorse == Stable.NO_HORSE)
            return false;
        if(setMap(color, idHorse, START[color], 0)){
            player[color].stable.remove(idHorse);
            player[color].addHorse(idHorse);
            return true;
        }

        return false;
    }

    public void addPlayerListener(int color, int steps) {
        player[color].addMouseListener(this, steps);
    }

    public void removePlayerListener(int color) {
        player[color].removeMouseListener();
    }

    public boolean setMap(int color, int idHorse, int start, int steps){
        if (start == DES[color]) {
            if (player[color].destination.setDestination(Destination.NO_RANK, steps, player[color].horse[idHorse])) {
                player[color].horse[idHorse].toFinish();
                map[start] = NO_HORSE;
                return true;
            } else {
                return false;
            }
        }

        if (start < DES[color] && start + steps > DES[color]) {
            showError("Quân này không thể di chuyển, quá đích đến.");
            return false;
        }

        /* Kiểm tra trên đường di chuyển có quân cờ nào cản không */
        for (int step = 1; step < steps; step++) {
            if (map[(start + step) % NUMBER_NODE] != NO_HORSE) {
                showError("Quân này không thể di chuyển, có quân cản mặt.");
                return false;
            }
        }

        /* Tính toán vị trí đến */
        int pos = (start + steps) % NUMBER_NODE;

        if (map[pos] == NO_HORSE) {
            /* Trường hợp có thể di chuyển */
            map[start] = NO_HORSE;
            map[pos] = color * 10 + idHorse;
        } else if (map[pos] / 10 == color) {
            /* Trường hợp vị trí đến là quân cùng màu */
            showError("Quân này không thể di chuyển, đích đến là quân cùng màu.");
            return false;
        } else if (map[pos]  / 10 < 5) {
            /* Trường hợp ăn quân */
            int victimPlayer = map[pos]  / 10;
            int victimHorse = map[pos] % 10;

            player[victimPlayer].removeHorse(victimHorse);
            player[victimPlayer].stable.add(victimHorse);

            map[start] = NO_HORSE;
            map[pos] = color * 10 + idHorse;
        }

        return true;
    }

    public boolean isWin() {
        for (int i = 1; i <= numberPlayer; i++) {
            if(player[i].isWin()){
                showError("Player " + i + " is winner.");
                return true;
            }
        }

        return false;
    }
}
