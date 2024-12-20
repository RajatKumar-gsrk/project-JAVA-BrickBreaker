package main;

import java.util.ArrayList;

import java.io.File;

import javax.swing.JPanel;

import javax.imageio.ImageIO;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;//rectangles to make hitbox

import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.*;

import java.io.IOException;

public class GamePanel extends JPanel /* implements Runnable */{
    //JPanel is container for other objects in Frame, Runnable is important for Thread mechanism

    //fields
    private boolean running;
    private boolean screen_shake_active;
    private boolean game_pause;
    private BufferedImage main_image;
    private Graphics2D g_main;//main graphics handler
    private long display_start_time;
    private long screen_shake_timer;
    private int level, start_level = 1, max_level = 3;//to change levels

    //entities
    private theBall main_ball;
    private thePaddel main_Paddel;
    private mouseMovement main_mouse_movement;
    private mouseButtons main_mouse_buttons;
    private key_listener main_key_listener;
    private theMap main_map;
    private theHUD main_hud;
    private ArrayList<powerUps> powers;
    private ArrayList<brickSplosion> brickSplosions;

    //buttons
    private BufferedImage exit_icon, pause_icon, play_icon, restart_icon;
    private Rectangle exit_Rectangle, pause_Rectangle, play_Rectangle, restart_Rectangle;

    //constructor
    public GamePanel(){
        init(start_level);
    }

    //initializer
    public void init(int level_value){
        running = true;
        screen_shake_active = false;
        game_pause = false;
        screen_shake_timer = System.nanoTime();
        if(main_image == null){
            main_image = new BufferedImage(BBmain.WIDTH, BBmain.HEIGHT, BufferedImage.TYPE_INT_RGB);
            g_main = (Graphics2D) main_image.getGraphics();
            g_main.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        

        main_ball = new theBall((BBmain.WIDTH/2 - 10), (BBmain.HEIGHT/2 + 100), 15);

        main_Paddel = new thePaddel(80, 20);

        if(main_mouse_movement == null){
            main_mouse_movement = new mouseMovement();
            addMouseMotionListener(main_mouse_movement);//adds mouse motion detection to panel
        }

        if(main_mouse_buttons == null){
            main_mouse_buttons = new mouseButtons();//adds mouse buttons functionality to panel
            addMouseListener(main_mouse_buttons);
        }

        if(main_key_listener == null){
            main_key_listener = new key_listener();//adds key listener
           addKeyListener(main_key_listener);
           setFocusable(true); // Ensure the GamePanel can receive focus, without this keylistener is not working;
           requestFocusInWindow();
        }

        level = level_value;

        main_map = new theMap(level);

        main_hud = new theHUD();

        if(powers != null){
            powers.clear();
        }
        powers = new ArrayList<powerUps>();

        if(brickSplosions != null){
            brickSplosions.clear();
        }
        brickSplosions = new ArrayList<brickSplosion>();

        play_sound("file:./resources/mute.wav", 0);//path is relative to current working directory

        play_start_level_sound();
        reset_display_start_time();

        set_buttons();

    }

    /* @Override
    public void run() {//what everr is in this method will be run by Thread
        //game loop
    } */
    
    public void playing(){
        while(running){
            //update
            if(!game_pause){
                update();
            }
            
            //draw
            draw();

            //display
            repaint();//calls paintComponent automatically

            //wait
            try{
                Thread.sleep(20);
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public void update(){

        if((System.nanoTime() - display_start_time) / 1000000 > 5000){
            collision_check_paddel();
            collision_check_map();
            collision_check_power_paddel();
            main_ball.update();
            main_Paddel.update();

            if((System.nanoTime() - screen_shake_timer) / 1000000 > 400 && screen_shake_active){
                screen_shake_active = false;
            }

            for(powerUps power : powers){//updating each powerup
                power.update();
            }

            for(brickSplosion bs : brickSplosions){//updating each explosion
                bs.update();
            }
        }
    }

    public void draw(){
        if((System.nanoTime() - display_start_time) / 1000000 < 5000){
            draw_level_start();
        }else{
            //draws background
            g_main.setColor(Color.darkGray);
            g_main.fillRect(0, 0, BBmain.WIDTH, BBmain.HEIGHT);

            main_ball.draw(g_main);
            
            main_map.draw(g_main);
            main_hud.drawHUD(g_main);

            draw_buttons();

            for(powerUps power : powers){
                power.draw(g_main);
            }

            for(brickSplosion bs : brickSplosions){
                bs.draw(g_main);
            }

            main_Paddel.draw(g_main);

            if(main_map.win_condition()){//winner
                start_next_level();
            }

            if(main_ball.getY() > main_Paddel.getY() + main_Paddel.getHeight()){//loser
                draw_loser();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;//type casting
        int x = 0, y = 0;
        if(screen_shake_active){//move main image to shake screen
            x = (int)(Math.random() * 10 - 5);
            y = (int)(Math.random() * 10 - 5);
        }
        g2.drawImage(main_image, x, y, BBmain.WIDTH, BBmain.HEIGHT, null);

        g2.dispose();//otherwise out of memory, as g_main is FPS
    }

    @Override//to set JPanel size within the JFrame
    public Dimension getPreferredSize() {
        return new Dimension(BBmain.WIDTH, BBmain.HEIGHT);
    }

    private class mouseMovement implements MouseMotionListener{//captures mouse movement
        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            main_Paddel.movePaddel(e.getX());
        }
    }
    private class mouseButtons implements MouseListener{

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if(pause_Rectangle.contains(x, y)){
                pause_game();
            }

            if(restart_Rectangle != null && restart_Rectangle.contains(x, y)){//resets same level;
                restart_level();
            }

            if(exit_Rectangle != null && exit_Rectangle.contains(x,y)){//exits game
                exit_game();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
        }
        
    }

    private class key_listener implements KeyListener{

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == 32){
                pause_game();
            }

            if(e.getKeyCode() == 82){
                restart_level();
            }

            if(e.getKeyCode() == 27){
                exit_game();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

    public void collision_check_paddel(){
        Rectangle ball_rectangle = main_ball.get_collision_rectangle();
        Rectangle paddel_rectangle = main_Paddel.get_collision_rectangle();

        if(ball_rectangle.intersects(paddel_rectangle)){
            play_sound("file:./resources/ball_paddel_hit.wav", 0);
            double ball_speed_y = Math.abs(main_ball.getDY()) + 1;// > default ball_speed y
            double ball_speed_x = Math.abs(main_ball.getDX()) + 1;
            if(ball_rectangle.getMaxY() > paddel_rectangle.getMinY() + ball_speed_y){
                if(ball_rectangle.getMaxX() < paddel_rectangle.getMinX() + ball_speed_x + 1){//left collision
                    main_ball.setX(paddel_rectangle.getMinX() - main_ball.get_ball_size() - 1);
                    if(main_ball.getDY() < 0){
                        main_ball.setDX(main_ball.getDX() - 1);
                    }else{
                        main_ball.setDX(-main_ball.getDX());
                    }
                }else if(ball_rectangle.getMinX() > paddel_rectangle.getMaxX() - ball_speed_x - 1){//right collision
                    main_ball.setX(paddel_rectangle.getMaxX() + 1);
                    if(main_ball.getDY() > 0){
                        main_ball.setDX(main_ball.getDX() + 1);
                    }else{
                        main_ball.setDX(-main_ball.getDX());
                    }
                }else{//ball collided in middle
                    main_ball.setY(main_Paddel.getY() - main_ball.get_ball_size() - 1);
                    main_ball.setDY(-main_ball.getDY());
                }
            }else{
                main_ball.setY(main_Paddel.getY() - main_ball.get_ball_size() - 1);
                main_ball.setDY(-main_ball.getDY());

                double ball_speed_change_x = 0;
                if(main_ball.getX() < main_Paddel.getX() + main_Paddel.getWidth()/10){//left most
                    if(main_ball.getDX() < 0){
                        ball_speed_change_x = - 1.2;
                    }else{
                        ball_speed_change_x = - 0.5;
                    }
                }else if(main_ball.getX() < main_Paddel.getX() + (2 * main_Paddel.getWidth()/10)){//left mid
                    if(main_ball.getDX() < 0){
                        ball_speed_change_x = - 1;
                    }else{
                        ball_speed_change_x = - 0.3;
                    }
                }else if(main_ball.getX() < main_Paddel.getX() + (2 * main_Paddel.getWidth()/5)){//left least
                    if(main_ball.getDX() < 0){
                        ball_speed_change_x = - 0.8;
                    }
                }else if(main_ball.getX() > main_Paddel.getX() + main_Paddel.getWidth() - main_Paddel.getWidth()/10){//right most
                    if(main_ball.getDX() < 0){
                        ball_speed_change_x = + 0.5;
                    }else{
                        ball_speed_change_x = + 1.2;
                    }
                }else if(main_ball.getX() > main_Paddel.getX() + main_Paddel.getWidth() - (2 * main_Paddel.getWidth()/10)){//right mid
                    if(main_ball.getDX() < 0){
                        ball_speed_change_x = 0.3;
                    }else{
                        ball_speed_change_x = + 1;
                    }
                }else if(main_ball.getX() > main_Paddel.getX() + main_Paddel.getWidth() - (2 * main_Paddel.getWidth()/5)){//right least
                    if(main_ball.getDX() > 0){
                        ball_speed_change_x = 0.8;
                    }
                }

                main_ball.setDX(main_ball.getDX() + ball_speed_change_x);
            }
            //collision with left and right edge of paddle is not neccessary as it leads to loss condition
        }
    }

    public void collision_check_map(){
        Rectangle ball_hitbox = main_ball.get_collision_rectangle();

        int brick_height = main_map.get_brick_height(), brick_width = main_map.get_brick_width(), map_padding = main_map.getMapPadding();
        int brick_padding = main_map.get_brick_padding();
        int rows = main_map.getMap_rows(), cols = main_map.getMap_cols();
        double ball_speed_y = Math.abs(main_ball.getDY()) + 1;
        double ball_speed_x = Math.abs(main_ball.getDX()) + 1;

        map_collision_loop: for(int row = 0; row < rows; row += 1){//loop with label 
            for(int col = 0; col < cols; col += 1){
                int brick_value = main_map.getMap_value(row, col);
                int brick_x = col * brick_width + map_padding + col * brick_padding, brick_y = row * brick_height + map_padding + row * brick_padding;

                if(brick_value > 0){
                    Rectangle brick_hitbox = new Rectangle(brick_x, brick_y, brick_width, brick_height);

                    if(ball_hitbox.intersects(brick_hitbox)){
                        play_sound("file:./resources/ball_power_hit.wav", 0);
                        if(brick_value == 10){
                            //does nothing;
                        }else if(brick_value > 3){
                            powers.add(new powerUps(brick_x, brick_y, brick_value, brick_width, brick_height));
                            main_map.set_map_value(row, col, 0);
                            main_map.set_total_brick_strength(main_map.get_total_brick_strength() - brick_value);
                            brickSplosions.add(new brickSplosion(brick_x, brick_y, main_map, brick_value));//explosions to array
                            main_hud.setScore(main_hud.getScore() + 200);

                            collision_check_power_ball();//needs to be checked when ball collide with brick
                        }else{
                            main_map.set_map_value(row, col, brick_value - 1);
                            main_map.set_total_brick_strength(main_map.get_total_brick_strength() - 1);
                            brickSplosions.add(new brickSplosion(brick_x, brick_y, main_map, brick_value));//explosions to array
                            main_hud.setScore(main_hud.getScore() + 50);
                        }

                        if(main_map.getMap_value(row, col) == 0){
                            screen_shake_active = true;
                            screen_shake_timer = System.nanoTime();
                        }

                        

                        if(main_ball.get_ballbrick_collision()){

                            double ball_speed_change_x = 0;//changing balls direction based on where it hits the brick
                            if(ball_hitbox.getMaxX() < brick_x + brick_width / 4){
                                if(main_ball.getDX() < 0){
                                    ball_speed_change_x = - 1.1;
                                }else{
                                    ball_speed_change_x = - 0.4;
                                }
                            }else if(ball_hitbox.getMaxX() < brick_x + brick_width / 2){
                                if(main_ball.getDX() < 0){
                                    ball_speed_change_x = - 1;
                                }else{
                                    ball_speed_change_x = - 0.6;
                                }
                            }else if(ball_hitbox.getMinX() > brick_x + 3 * brick_width / 4){
                                if(main_ball.getDX() < 0){
                                    ball_speed_change_x = + 1.2;
                                }else{
                                    ball_speed_change_x = + 0.5;
                                }
                            }else if(ball_hitbox.getMinX() > brick_x + brick_width / 2){
                                if(main_ball.getDX() < 0){
                                    ball_speed_change_x = + 0.3;
                                }else{
                                    ball_speed_change_x = + 1.2;
                                }
                            }
                            main_ball.setDX(main_ball.getDX() + ball_speed_change_x);

                            if(ball_hitbox.getMaxX() < brick_x + ball_speed_x){//left
                                if(main_ball.getDX() > 0 ){
                                    main_ball.setDX(-main_ball.getDX());
                                }
                                main_ball.setX(brick_x - main_ball.get_ball_size() - 1);
                            }else if(ball_hitbox.getMinX() > brick_hitbox.getMaxX() - ball_speed_x){//right
                                if(main_ball.getDX() < 0 ){
                                    main_ball.setDX(-main_ball.getDX());
                                }
                                main_ball.setX(brick_hitbox.getMaxX() + 1);
                            }
                            //if collision break include this with else if
                            if(ball_hitbox.getMaxY() < brick_y + ball_speed_y){//up
                                if(main_ball.getDY() > 0){
                                    main_ball.setDY(-main_ball.getDY());
                                }
                                main_ball.setY(brick_y - main_ball.get_ball_size()- 1);
                            }else if(ball_hitbox.getMinY() > brick_hitbox.getMaxY() - ball_speed_y){//down
                                if(main_ball.getDY() < 0){
                                    main_ball.setDY(-main_ball.getDY());
                                }
                                main_ball.setY(brick_hitbox.getMaxY() + 1);
                            }
                        }

                        
                        break map_collision_loop;
                    }
                }
            }
        }
    }

    public void collision_check_power_paddel(){
        Rectangle paddel_rectangle = main_Paddel.get_collision_rectangle();
        for(int i = 0; i < powers.size(); i += 1){//power brick collision with paddle
            powerUps power = powers.get(i);
            Rectangle power_hitbox = power.getHitBox();

            if(power_hitbox.intersects(paddel_rectangle)){//power active here, deactive in respective class
                play_sound("file:./resources/power_up.wav", 0);//caused weird sound as I did not play it once
                if(power.getTYPE() == powerUps.WIDEPADDEL){
                    powerUps.reset_widepaddel_time();
                    if(powerUps.get_widepaddel_deactivate()){
                        main_Paddel.setWidth(main_Paddel.getWidth() * 2);//reset in paddel class.update
                        powerUps.set_widepaddel_deactivate(false);
                    }
                }

                if(power.getTYPE() == powerUps.GHOSTBALL && powerUps.get_fastball_deactivate()){
                    powerUps.reset_ghostball_time();
                    if(powerUps.get_ghostball_deactivate()){
                        main_ball.set_ballcolor(powerUps.GHOSTCOLOR);
                        main_ball.set_ballbrick_collision(false);
                        powerUps.set_ghostball_deactivate(false);
                    }
                }
            }
        }
    }

    public void collision_check_power_ball(){
        Rectangle ball_rectangle = main_ball.get_collision_rectangle(); 
        for(int i = 0; i < powers.size(); i += 1){//power brick collision with paddle
            powerUps power = powers.get(i);
            Rectangle power_hitbox = power.getHitBox();

            if(power_hitbox.intersects(ball_rectangle)){
                if(power.getTYPE() == powerUps.FASTBALL && powerUps.get_ghostball_deactivate()){
                    powerUps.reset_fastball_time();
                    if(powerUps.get_fastball_deactivate()){
                        play_sound("file:./resources/power_down.wav", 0);
                        main_ball.set_ballcolor(powerUps.FASTCOLOR);
                        main_ball.setDY(2 * main_ball.getDY());
                        powerUps.set_fastball_deactivate(false);
                    }
                }

                if(power.getTYPE() == powerUps.SHRINKPADDEL){
                    powerUps.reset_shrinkpaddel_time();
                    if(powerUps.get_shrinkpaddel_deactivate()){
                        play_sound("file:./resources/power_down.wav", 0);
                        main_Paddel.setWidth(main_Paddel.getWidth() / 2);
                        powerUps.set_shrinkpaddel_deactivate(false);
                    }
                }
            }
        }
    }

    public void play_sound(String sound_file, int repeat){//whole sound system
        try {
            @SuppressWarnings("deprecation")
            URL sound_path_url = new URL(sound_file);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(sound_path_url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
           clip.loop(repeat);
        } catch (MalformedURLException me) {
            me.printStackTrace();
        } catch(UnsupportedAudioFileException uafe){
            uafe.printStackTrace();
        } catch(IOException ioe){
            ioe.printStackTrace();
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
        }

        
        
    }

    public void start_next_level(){
        if(level >= max_level){
            play_sound("file:./resources/winner.wav", 0);
            g_main.setFont(new Font("Roboto", Font.BOLD, 50));
            FontMetrics f_matrics = g_main.getFontMetrics();//including size of font
            int x = (BBmain.WIDTH - f_matrics.stringWidth("WINNER!!"))/2;
            int y = (BBmain.HEIGHT - f_matrics.getHeight())/2;
            g_main.setColor(Color.WHITE);
            g_main.fillRect(x, y, f_matrics.stringWidth("WINNER!!"), f_matrics.getHeight());
            g_main.setColor(Color.RED);
            g_main.drawString("WINNER!!", x, y + f_matrics.getHeight() - 5);
            running = false;
        }else{
            play_sound("file:./resources/next_level.wav", 0);
            //wait
            try{
                Thread.sleep(1000);
            }catch(Exception e){
                e.printStackTrace();
            }
            init(level + 1);
        }
    }

    public void draw_loser(){
        if(!game_pause){
            play_sound("file:./resources/loser.wav", 0);
        }

        try {//exit button
            if(exit_icon == null){
                File exit_icon_file = new File("./resources/buttons/exit_icon.png");
                exit_icon = ImageIO.read(exit_icon_file);
            }
            exit_Rectangle = new Rectangle(1125, 705, exit_icon.getWidth(), exit_icon.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {//restart button
            if(restart_icon == null){
                File restart_icon_file = new File("./resources/buttons/restart_icon.png");
                restart_icon = ImageIO.read(restart_icon_file);
            }
            restart_Rectangle = new Rectangle(725, 705, restart_icon.getWidth(), restart_icon.getHeight());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        g_main.setFont(new Font("Roboto", Font.BOLD, 50));
        FontMetrics f_matrics = g_main.getFontMetrics();//including size of font
        int x = (BBmain.WIDTH - f_matrics.stringWidth("LOSER!!"))/2;
        int y = (BBmain.HEIGHT - f_matrics.getHeight())/2;
        g_main.setColor(Color.WHITE);
        g_main.fillRect(350, 150, BBmain.WIDTH - 700, BBmain.HEIGHT - 300 /* f_matrics.stringWidth("LOSER!!"), f_matrics.getHeight() */);
        g_main.setColor(Color.RED);
        g_main.drawString("LOSER!!", x, y + f_matrics.getHeight() - 5 );

        g_main.drawImage(restart_icon, restart_Rectangle.x, restart_Rectangle.y, null);
        g_main.drawImage(exit_icon, exit_Rectangle.x, exit_Rectangle.y, null);

        game_pause = true;
    }

    public void draw_level_start(){//draws starting of a level
            g_main.setColor(Color.WHITE);
            g_main.fillRect(0, 0, BBmain.WIDTH, BBmain.HEIGHT);

            g_main.setFont(new Font("Roboto", Font.BOLD, 100));
            FontMetrics f_matrics = g_main.getFontMetrics();//including size of font
            String start_level_display = String.format("LEVEL %d STARTING IN %d", level, 5 - (System.nanoTime()- display_start_time)/ 1000000000);
            int x = (BBmain.WIDTH - f_matrics.stringWidth(start_level_display))/2;
            int y = (BBmain.HEIGHT - f_matrics.getHeight())/2;
            g_main.setColor(Color.BLACK);
            g_main.drawString(start_level_display, x, y + f_matrics.getHeight() - 5);

            g_main.setColor(Color.RED);
            g_main.setFont(new Font("Roboto", Font.BOLD, 25));
            g_main.drawString("By Rajat Kumar", x + 5, y + (f_matrics.getHeight()) + 35);

    }

    private void reset_display_start_time(){
        display_start_time = System.nanoTime();
    }

    private void play_start_level_sound(){
        play_sound("file:./resources/level_start.wav", 0);
    }

    private void set_buttons(){

        exit_Rectangle = null;
        restart_Rectangle = null;

        try {
            if(pause_icon == null){
                File pause_icon_file = new File("./resources/buttons/pause_icon.png");
                pause_icon = ImageIO.read(pause_icon_file);
            }
            if(pause_Rectangle == null){
                pause_Rectangle = new Rectangle(150, 1, pause_icon.getWidth(), (int)(1.5 * pause_icon.getHeight()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(play_icon == null){
                File play_icon_file = new File("./resources/buttons/play_icon.png");
                play_icon = ImageIO.read(play_icon_file);
            }
            if(play_Rectangle == null){
                play_Rectangle = new Rectangle(150, 1, play_icon.getWidth(), (int)(1.5 * play_icon.getHeight()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void draw_buttons(){
        if(game_pause){
            g_main.drawImage(play_icon, play_Rectangle.x, play_Rectangle.y, null);
        }else{
            g_main.drawImage(pause_icon, pause_Rectangle.x, pause_Rectangle.y, null);
        }
        
    }

    private void pause_game(){
        game_pause = !game_pause;
    }

    private void restart_level(){
        init(level);
    }

    private void exit_game(){
        System.exit(0);
    }
}
