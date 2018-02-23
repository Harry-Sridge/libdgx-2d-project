package com.sandbox.game;

/**
 * Created by Southridge on 2018-02-16.
 */

import com.sandbox.game.Enums.tileType;
import com.sandbox.game.Entity;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class Tile extends Entity{

    public int size;
    public int row;
    public int col;
    public String code;
    public Texture texture;
    public List<Texture> secondaryTextures = new ArrayList<Texture>();
    public tileType type;
    public boolean occupied;

    public Tile(float x, float y, int size, tileType type, Texture texture)
    {
        super();
        pos.x = x*size;
        pos.y = y*size;
        this.size = size;
        this.texture = texture;
        this.col = (int)x;
        this.row = (int)y;
        this.type = type;
        this.code = "";
    }

    public String details()
    {
        return "x: " + pos.x + " y: " + pos.y + " row: " + row + " col: " + col + " code: " + code + " type: " + type.toString();
    }

    public boolean isGrass() {
        return type == tileType.Grass;
    }

    public boolean isWater() {
        return type == tileType.Water;
    }

    public boolean isCliff() {
        return type == tileType.Cliff;
    }

    public boolean isAllWater()
    {
        return (code.equals("000000000"));
    }

    public boolean passable() {
        return (!isWater() && !isCliff());
    }
}
