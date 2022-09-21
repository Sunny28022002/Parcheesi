package com.company;



public class Player extends Game{
    int color;
    static final int NUMBER_HORSE = 4;
    Stable stable = new Stable(color);
    Destination destination = new Destination(color);
    HorseSea horse[] = new HorseSea[NUMBER_HORSE];

    Player(int color){
        this.color = color;
    }

    public void removeHorse(int id) {horse[id] = null;}

    public void addHorse(int id) {horse[id] = new HorseSea(color, id);}

    public void addMouseListener(GameMap map, int steps){
        for(int i = 0; i < NUMBER_HORSE; i++){
            if(horse[i] != null){
                horse[i].addMouseListener(map, steps);
            }
        }
    }

    public void removeMouseListener(){
        for(int i = 0; i < NUMBER_HORSE; i++){
            if(horse[i] != null){
                horse[i].removeMouseListener();
            }
        }
    }

    public boolean isWin(){
        return destination.isWin();
    }
}
