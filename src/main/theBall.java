package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class theBall {

    //feilds
    private double x, y, dx, dy;
    private int ball_size;
    private boolean ballbrick_collision;
    private Color color;

    public theBall(double x_value, double y_value, double dx_value, double dy_value, int ball_size_value){
        x = x_value;
        y = y_value;
        dx = dx_value;
        dy = dy_value;
        ball_size = ball_size_value;
        ballbrick_collision = true;//used for ghost ball
        color = Color.WHITE;
    }

    //update ball
    public void update(){
        if(!powerUps.get_fastball_deactivate() && (System.nanoTime() - powerUps.get_fastball_time()) / 1000000 > 10000){
            if(getDY() < 0){//reset in ball class.update
                setDY(getDY() + 10);
            }else{
                setDY(getDY() - 10);
            }
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
        }
        if(y <= 0){
            dy = -dy;
        }
        if(x >= (BBmain.WIDTH - ball_size)){//ball size is calculated from top-right corner and not from the center
            dx = -dx;
        }
        if(y >= (BBmain.HEIGHT - ball_size)){
            dy = -dy;
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
