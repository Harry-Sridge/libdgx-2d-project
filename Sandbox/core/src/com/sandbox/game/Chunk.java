package com.sandbox.game;

/**
 * Created by zliu on 2018-02-16.
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

<<<<<<< HEAD
<<<<<<< HEAD
    public Tile GetTile(int r, int c)
    {
<<<<<<< HEAD
        int row = (int) ((pos.y * tileSize/2) / size);
        int col = (int) ((pos.x * tileSize/2) / size);

        return tiles[1][1];
=======
        return tiles[r][c];
>>>>>>> 6be2c20bf83b12bde4aae6531938a238f2995d8f
=======
    public Tile GetTile(int r, int c)
    {
        return tiles[r][c];
>>>>>>> 6be2c20bf83b12bde4aae6531938a238f2995d8f
    }

=======
>>>>>>> parent of 05f4a68... Implemented basic npc logic
    public String GetTileCode(int r, int c)
    {
        if(r >= 0 && c >= 0)
        {
            if(r < size && c < size)
            {
                return tiles[r][c].isGrass()? "1" : "0";
            }
            else
            {
                return "0";
            }
        }
        else
        {
            return "0";
        }
    }
}
