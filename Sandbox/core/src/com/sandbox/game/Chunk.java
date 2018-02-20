package com.sandbox.game;

/**
 * Created by Southridge on 2018-02-16.
 */

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

    public Tile GetTile(int r, int c)
    {
        if(r>=0&&r<tiles.length&&c>=0&&c<tiles[0].length)
        {
            return tiles[r][c];
        }
        return null;
    }

    public String GetTileCode(int r, int c)
    {
        if(r>=0&&r<tiles.length&&c>=0&&c<tiles[0].length)
        {
            return tiles[r][c].isGrass()?"1":"0";
        }
        return "0";
    }
}
