package main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.FontMetrics;

public class thePaddel {
    //feilds
    private double x, target_x, paddel_height, paddel_width, Y;


    //mthods
    public thePaddel(double width_value, double height_value){
        paddel_height = height_value;
        paddel_width = width_value;
        x = BBmain.WIDTH/2 - paddel_width/2;
        target_x = BBmain.WIDTH/2 - paddel_width/2;
        Y = BBmain.HEIGHT - 30 - paddel_height;
    }

    public void update(){
        if((System.nanoTime() - powerUps.get_widepaddel_time())/1000000 > 10000 && !powerUps.get_widepaddel_deactivate()){
            setWidth(getWidth()/2);
            powerUps.set_widepaddel_deactivate(true);
        }

        if((System.nanoTime() - powerUps.get_shrinkpaddel_time())/1000000 > 10000 && !powerUps.get_shrinkpaddel_deactivate()){
            setWidth(getWidth() * 2);
            powerUps.set_shrinkpaddel_deactivate(true);
        }

        x += ((target_x - x) * 0.2);//takes time to reach mouse position
    }

    public void draw(Graphics2D g){
        g.setColor(Color.WHITE);
        g.fillRect((int)x, (int)(Y), (int)paddel_width, (int)paddel_height);
        if(!powerUps.get_widepaddel_deactivate()){
            g.setColor(powerUps.WIDECOLOR);
            g.setFont(new Font("Roboto", Font.BOLD, 20));
            g.drawString(""+(10 - (System.nanoTime() - powerUps.get_widepaddel_time())/1000000000), (int)x, (int)Y + 20);
        }
        if(!powerUps.get_shrinkpaddel_deactivate()){
            g.setColor(powerUps.SHRINKCOLOR);
            g.setFont(new Font("Roboto", Font.BOLD, 20));
            FontMetrics f_matrics = g.getFontMetrics();
            double x_font = (x + paddel_width - f_matrics.stringWidth(""+(10 - (System.nanoTime() - powerUps.get_shrinkpaddel_time())/1000000000)));
            g.drawString(""+(10 - (System.nanoTime() - powerUps.get_shrinkpaddel_time())/1000000000), (int)x_font, (int)Y + 20);
        }
    }

    public void movePaddel(int xMovement){
        target_x = xMovement - paddel_width/2;
        if(target_x > BBmain.WIDTH - paddel_width){
            target_x = BBmain.WIDTH - paddel_width;
        }
        if(target_x < 0){
            target_x = 0;
        }
    }

    public Rectangle get_collision_rectangle(){//paddelhitbox;
        return new Rectangle((int)x, (int)Y, (int)paddel_width, (int)paddel_height);
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return Y;
    }

    public double getWidth(){
        return paddel_width;
    }

    public void setWidth(double value){
        paddel_width = value;
    }

    public double getHeight(){
        return paddel_height;
    }
}
