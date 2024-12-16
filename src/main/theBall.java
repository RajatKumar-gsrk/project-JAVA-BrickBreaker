package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public class theBall {

    //feilds
    private double x, y, dx, dy;
    private int ball_size;
    private boolean ballbrick_collision;
    private Color color;

    public theBall(double x_value, double y_value, int ball_size_value){
        
        x = x_value;
        y = y_value;

        double[] dx_value = {-3, -2, 2, 3};
        int dx_value_indx = new Random().nextInt(dx_value.length);//select random int from array size
        dx = dx_value[dx_value_indx];
        
        dy = 8;

        ball_size = ball_size_value;
        
        ballbrick_collision = true;//used for ghost ball

        color = Color.WHITE;
    }

    //update ball
    public void update(){
        if(!powerUps.get_fastball_deactivate() && (System.nanoTime() - powerUps.get_fastball_time()) / 1000000 > 10000){
            setDY(getDY() / 2);//resets in update 
            set_ballcolor(Color.WHITE);
            powerUps.set_fastball_deactivate(true);
        }

        if(!powerUps.get_ghostball_deactivate() && (System.nanoTime() - powerUps.get_ghostball_time()) / 1000000 > 10000){
            set_ballbrick_collision(true);
            set_ballcolor(Color.WHITE);
            powerUps.set_ghostball_deactivate(true);
        }
        setPosition();
    }

    public void setPosition(){
        x += dx;
        y += dy;

        if(x <= 0){
            dx = -dx;
            setX(1);
        }
        if(y <= 0){
            dy = -dy;
            setY(1);
        }
        if(x >= (BBmain.WIDTH - ball_size)){//ball size is calculated from top-right corner and not from the center
            dx = -dx;
            setX(BBmain.WIDTH - ball_size - 1);
        }
    }

    public void draw(Graphics2D g){

        g.setColor(color);//set color of drawing
        g.setStroke(new BasicStroke(4));//stroke width = 4
        g.drawOval((int)x, (int)y, ball_size, ball_size);
    }

    public Rectangle get_collision_rectangle(){//ball hitbox
        return new Rectangle((int)x, (int)y, ball_size, ball_size);
    }

    public double getDY(){
        return dy;
    }

    public void setDY(double new_value){
        dy = new_value;
    }

    public double getY(){
        return y;
    }

    public void setY(double y_value){
        y = y_value;
    }

    public double getDX(){
        return dx;
    }

    public void setDX(double new_value){
        dx = new_value;
    }

    public double getX(){
        return x;
    }

    public void setX(double new_value){
        x = new_value;
    }

    public int get_ball_size(){
        return ball_size;
    }

    public void set_ballbrick_collision(boolean value){
        ballbrick_collision = value;
    }

    public boolean get_ballbrick_collision(){
        return ballbrick_collision;
    }

    public void set_ballcolor(Color value){
        color = value;
    }
}
