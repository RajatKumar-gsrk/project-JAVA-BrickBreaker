package main;

import javax.swing.JFrame;

public class BBmain{
    //feilds, don't change
    public static final int WIDTH = 1280, HEIGHT = 720;

    public static void main(String[] args) {

        JFrame main_frame = new JFrame("Brick Breaker");

        GamePanel game_panel = new GamePanel();//part of pkg main

        /* Thread main_thread = new Thread(game_panel);//create a thread, useful in high end games */

        
        
       // main_frame.setSize(WIDTH, HEIGHT);//window open size
        main_frame.setResizable(false);//window resizeable?
        main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//action on close button
        main_frame.setVisible(true);//is vindow visible?

        main_frame.add(game_panel);//adds elements to main frame, main_panel here

        //to pack everything properly inside JFrame
        main_frame.pack();
        main_frame.setLocationRelativeTo(null);//on screen location relative to which element

        game_panel.playing();
        /* main_thread.start();//start the thread */

    }
}
