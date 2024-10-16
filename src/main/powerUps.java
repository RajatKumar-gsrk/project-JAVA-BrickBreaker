package main;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class powerUps {
    private int x, y, dy, type, width, height;
    private boolean isOnScreen;
    private Color color;

    public final static int WIDEPADDEL = 4;
    public static final Color WIDECOLOR = new Color(199, 112, 255);//purple
    private static boolean widepaddel_deactive = true;
    private static long widepaddel_time = 0;

    public final static int FASTBALL = 5;
    public final static Color FASTCOLOR = new Color(250, 60, 60);//red
    private static boolean fastball_deactive = true;
    private static long fastball_time = 0;

    public final static int SHRINKPADDEL = 6;
    public final static Color SHRINKCOLOR = new Color(255, 165, 0);//orange
    private static boolean shrinkpaddel_deactivate = true;
    private static long shrinkpaddel_time = 0;

    public static final int GHOSTBALL = 7;
    public final static Color GHOSTCOLOR = new Color(135, 230, 135);//green
    private static boolean ghostball_deactivate = true;
    private static long ghostball_time = 0;


    public powerUps(int x_value, int y_value, int type_value, int width_value, int height_value){
        x = x_value;
        y = y_value;
        type = type_value;
        width = width_value;
        height = height_value;

        dy = (int)(Math.random() * 5 + 5 );

        switch(type){
            case 4:{
                color = WIDECOLOR;
                break;
            }
            case 5:{
                color = FASTCOLOR;
                break;
            }case 6:{
                color = SHRINKCOLOR;
                break;
            }
            case 7:{
                color = GHOSTCOLOR;
                break;
            }
        }
    }

    public void draw(Graphics2D g){
        if(type != FASTBALL && type != SHRINKPADDEL){
            g.setColor(color);
            g.fillRect(x, y, width, height);
        }
    }

    public void update(){
        if(type != FASTBALL && type != SHRINKPADDEL){
            y += dy;
            if(y > BBmain.HEIGHT){
                isOnScreen = false;
            }
        }
    }

    public Rectangle getHitBox(){
        return new Rectangle(x, y, width, height);
    }

    public void setX(int x_value){
        x = x_value;
    }

    public int getX(){
        return x;
    }

    public void setY(int y_value){
        y = y_value;
    }

    public int getY(){
        return y;
    }

    public void setDY(int dy_value){
        dy = dy_value;
    }

    public int getDY(){
        return dy;
    }

    public void setTYPE(int type_value){
        type = type_value;
    }

    public int getTYPE(){
        return type;
    }

    public void setISONSCREEN(boolean value){
        isOnScreen = value;
    }

    public boolean getISONSCREEN(){
        return isOnScreen;
    }

    public static boolean get_widepaddel_deactivate(){
        return widepaddel_deactive;
    }

    public static void set_widepaddel_deactivate(boolean value){
        widepaddel_deactive = value;
    }

    public static boolean get_fastball_deactivate(){
        return fastball_deactive;
    }

    public static void set_fastball_deactivate(boolean value){
        fastball_deactive = value;
    }
    
    public static boolean get_shrinkpaddel_deactivate(){
        return shrinkpaddel_deactivate;
    }

    public static void set_shrinkpaddel_deactivate(boolean value){
        shrinkpaddel_deactivate = value;
    }

    public static boolean get_ghostball_deactivate(){
        return ghostball_deactivate;
    }

    public static void set_ghostball_deactivate(boolean value){
        ghostball_deactivate = value;
    }

    public static void reset_widepaddel_time(){
        widepaddel_time = System.nanoTime();
    }

    public static long get_widepaddel_time(){
        return widepaddel_time;
    }

    public static void reset_fastball_time(){
        fastball_time = System.nanoTime();
    }

    public static long get_fastball_time(){
        return fastball_time;
    }

    public static void reset_shrinkpaddel_time(){
        shrinkpaddel_time = System.nanoTime();
    }

    public static long get_shrinkpaddel_time(){
        return shrinkpaddel_time;
    }

    public static void reset_ghostball_time(){
        ghostball_time = System.nanoTime();
    }

    public static long get_ghostball_time(){
        return ghostball_time;
    }
}
