package com.sandbox.game;

/**
 * Created by zliu on 2018-02-16.
 */

//Stores an array of tiles
public class Chunk {

    int size;
    int tileSize;
    public Tile[][] tiles;

    public Chunk(int size, int tileSize)
    {
        this.size = size;
        this.tileSize = tileSize;
        tiles = new Tile[size][size];
    }

    public String GetTileCode(int r, int c)
    {
        if(r >= 0 && c >= 0)
        {
            if(r < size && c < size)
                return tiles[r][c].isGrass()? "1" : "0";
            else
                return "0";
        }
        else
            return "0";
    }
}
