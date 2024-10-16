package main;

import java.util.ArrayList;
import java.awt.Graphics2D;

public class brickSplosion {//explosion, with certainnumber of particles
    private ArrayList<BrickPeice> peices;
    private double x, y;
    private theMap map;
    private long start_time;
    private boolean is_active;

    public brickSplosion(double x_value, double y_value, theMap map_obj, int brick_value){
        x = x_value;
        y = y_value;
        map = map_obj;
        peices = new ArrayList<BrickPeice>();
        is_active = true;
        start_time = System.nanoTime();

        init(brick_value);
    }

    public void init(int brick_value){
        int peice_number = (int)Math.random()*20 + 5;
        for(int i = 0; i < peice_number; i += 1){
            peices.add(new BrickPeice(x, y, map, brick_value));
        }
    }

    public void update(){
        for(BrickPeice bp : peices){
            bp.update();
        }

        if((System.nanoTime() - start_time)/1000000 > 2500 && is_active){
            is_active = false;
        }
    }

    public void draw(Graphics2D g){
        if(is_active){
            for(BrickPeice bp : peices){
                bp.draw(g);
            }
        }
    }
}
