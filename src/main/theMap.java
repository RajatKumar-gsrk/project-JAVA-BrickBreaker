package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class theMap {
    private int[][] map;
    private int rows, cols;
    private int brick_height, brick_width;
    private int map_padding;
    private int total_brick_strength;
    private int brick_padding;

    public theMap(int file_number){
        init(file_number);//for map
        map_padding = 50;
        brick_padding = 5;
        brick_height = (BBmain.HEIGHT/2 - map_padding) / rows - brick_padding;
        brick_width = (BBmain.WIDTH - 2 * map_padding) / cols - brick_padding;
    }

    public void  init(int file_number){//mportant to learn how to read files
        String file_path = String.format(".\\resources\\levels\\level%d.txt", file_number);//custome file path with %d
        try {
            Scanner sc = new Scanner(new File(file_path));//opening the file
            String[] reader = sc.nextLine().trim().split("," + " ");//reading firstline in reader = row, col
            rows = Integer.parseInt(reader[0]);
            cols = Integer.parseInt(reader[1]);

            map = new int[rows][cols];
            for(int i = 0; i < rows; i += 1){
                reader = sc.nextLine().trim().split("," + " ");
                for(int j = 0; j < cols; j += 1){
                    int brick_level = Integer.parseInt(reader[j]);
                    map[i][j] = brick_level;
                    if(brick_level != 10){
                        total_brick_strength += brick_level;
                    }
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g){
        for(int i = 0; i < map.length; i += 1){
            for(int j = 0; j < map[0].length; j += 1){
                int brick_value = map[i][j];
                if(brick_value <= 0){
                    continue;
                }
                switch(brick_value){//drawing different level bricks
                    case 1:{
                        g.setColor(Color.decode("#00c8c8"));
                        break;
                    }

                    case 2:{
                        g.setColor(Color.decode("#009696"));
                        break;
                    }

                    case 3:{
                        g.setColor(Color.decode("#006464"));
                        break;
                    }

                    case 4:{
                        g.setColor(powerUps.WIDECOLOR);
                        break;
                    }

                    case 5:{
                        g.setColor(powerUps.FASTCOLOR);
                        break;
                    }

                    case 6:{
                        g.setColor(powerUps.SHRINKCOLOR);
                        break;
                    }

                    case 7:{
                        g.setColor(powerUps.GHOSTCOLOR);
                        break;
                    }

                    case 10:{
                        g.setColor(Color.BLACK);
                        break;
                    }

                }
                g.fillRect(j * brick_width + map_padding + j * brick_padding, i * brick_height + map_padding + i * brick_padding, brick_width, brick_height);
                g.setColor(Color.black);
                g.setStroke(new BasicStroke(2));
                g.setColor(Color.WHITE);
                g.drawRect(j * brick_width + map_padding + j * brick_padding, i * brick_height + map_padding + i * brick_padding, brick_width, brick_height);
                
            }
        }
    }

    public boolean win_condition(){
        return total_brick_strength == 0;
    }

    public int getMap_rows(){
        return rows;
    }

    public int getMap_cols(){
        return cols;
    }

    public int getMap_value(int row, int col){
        return map[row][col];
    }

    public int getMapPadding(){
        return map_padding;
    }

    public int get_brick_width(){
        return brick_width;
    }

    public int get_brick_height(){
        return brick_height;
    }

    public void set_map_value(int row, int col, int value){
        map[row][col] = value;
    }

    public int get_total_brick_strength(){
        return total_brick_strength;
    }

    public void set_total_brick_strength(int value){
        total_brick_strength = value;
    }

    public int get_brick_padding(){
        return brick_padding;
    }
}
