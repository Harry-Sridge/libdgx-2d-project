package com.sandbox.game;

/**
 * Created by Southridge on 2018-02-16.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Asset {

    // TILES
    public static Texture grass_01, grass_02, grass_03, grass_04;
    public static Texture grass_left, grass_right;
    public static Texture grass_left_upper_edge, grass_right_upper_edge;
    public static Texture grass_top, grass_top_right, grass_top_left;
    public static Texture water_01, water_02, water_03, water_04;
    public static Texture cliff;

    //STUFF
    public static Texture tree;
    public static Texture house;

    // Player
    public static Texture player;

    public static void Load(){
        grass_01 = new Texture("8x8/grass/grass_01.png");
        grass_02 = new Texture("8x8/grass/grass_02.png");
        grass_03 = new Texture("8x8/grass/grass_03.png");
        grass_04 = new Texture("8x8/grass/grass_04.png");

        grass_left = new Texture("8x8/grass/right_grass_edge.png");
        grass_right = new Texture("8x8/grass/left_grass_edge.png");

        grass_left_upper_edge = new Texture("8x8/grass/left_upper_edge.png");
        grass_right_upper_edge = new Texture("8x8/grass/right_upper_edge.png");

        grass_top = new Texture("8x8/grass/top.png");
        grass_top_right = new Texture("8x8/grass/top_right.png");
        grass_top_left = new Texture("8x8/grass/top_left.png");

        water_01 = new Texture("8x8/water/water_01.png");
        water_02 = new Texture("8x8/water/water_02.png");
        water_03 = new Texture("8x8/water/water_03.png");
        water_04 = new Texture("8x8/water/water_04.png");
        cliff = new Texture(Gdx.files.internal("8x8/cliff.png"));

        tree = new Texture("8x8/tree.png");
        house = new Texture("8x8/house.png");
        player = new Texture("8x8/hero.png");
    }

    public void dispose(){
        grass_01.dispose();
        grass_02.dispose();
        grass_03.dispose();
        grass_04.dispose();
        grass_left.dispose();
        grass_right.dispose();
        grass_left_upper_edge.dispose();
        grass_right_upper_edge.dispose();
        grass_top.dispose();
        grass_top_right.dispose();
        grass_top_left.dispose();
        water_01.dispose();
        water_02.dispose();
        water_03.dispose();
        water_04.dispose();
        cliff.dispose();
        tree.dispose();
        player.dispose();
    }

}
