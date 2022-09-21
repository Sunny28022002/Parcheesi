package com.company;
import java.util.Random;

public class Dice extends Game{
    // Xúc xắc
    private int steps = 0;

    // Giao xúc xắc
    public void thrown(){
        Random ramdom = new Random();
        steps = (ramdom.nextInt(6)+1);
    }

    public int getSteps(){return  steps;}
}
