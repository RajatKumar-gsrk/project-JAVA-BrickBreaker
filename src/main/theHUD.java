package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class theHUD {
    private int score;

    public theHUD(){
        score = 0;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int value){
        score = value;
    }

    public void drawHUD(Graphics2D g){
        g.setColor(new Color(233, 190, 214));
        g.setFont(new Font("Roboto", Font.BOLD, 20));
        g.drawString("Score: " + score, 20, 20);
    }
}
