package map;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by zliu on 2018-02-16.
 */

//Stores an array of tiles
public class Chunk {

    private int size;
    int tileSize;
    public Tile[][] tiles;

    public Chunk(int size, int tileSize)
    {
        this.size = size;
        this.tileSize = tileSize;
        tiles = new Tile[size][size];
    }

    //I do not know why this is a thing
    public Tile GetTile(Vector2 pos)
    {
        int row = (int) ((pos.y * tileSize/2) / size);
        int col = (int) ((pos.x * tileSize/2) / size);

        return tiles[row][col];
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
