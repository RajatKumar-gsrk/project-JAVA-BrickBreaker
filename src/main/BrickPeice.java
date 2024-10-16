package main;

import java.awt.Color;
import java.awt.Graphics2D;

public class BrickPeice {//particles of explosion
    private double x, y, dx, dy, gravity;
    private Color color;
    private int size;
    private theMap map;

    public BrickPeice(double x_value, double y_value, theMap map_obj, int brick_value){
        map = map_obj;
        x = x_value + map.get_brick_width()/2;//center of brick
        y = y_value + map.get_brick_height()/2;
        gravity = 0.8;
        dx = Math.random()*30 - 15;
        dy = Math.random()*30 - 15;
        size = (int) (Math.random()*15 + 2);

        switch(brick_value){//color according to brick value, if we don't set a value then it will show last value used
            case 1:{
                color = new Color(0, 200, 200);
                break;
            }

            case 2:{
                color = new Color(0, 150, 150);
                break;
            }

            case 3:{
                color = new Color(0, 100, 100);
                break;
            }

            case 4:{
                color = powerUps.WIDECOLOR;
                break;
            }

            case 5:{
                color = powerUps.FASTCOLOR;
                break;
            }

            case 6:{
                color = powerUps.SHRINKCOLOR;
                break;
            }

            case 7:{
                color = powerUps.GHOSTCOLOR;
                break;
            }
        }
    }

    public void update(){
        x += dx;
        y += dy;
        dy += gravity;
    }

    public void draw(Graphics2D g){
        g.setColor(color);
        g.fillRect((int)x, (int)y, size, size);
    }
}
