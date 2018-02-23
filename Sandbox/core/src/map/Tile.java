package map;

/**
 * Created by zliu on 2018-02-16.
 */

import com.sandbox.game.Enums.tileType;
import entities.Entity;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class Tile extends Entity{

    //tile size
    public int size;

    //tile position in array
    public int row;
    public int col;

    //tile information
    public String code = "";
    public Texture texture;
    public List<Texture> secondaryTextures = new ArrayList<Texture>();
    public tileType type;

    boolean occupied;

    public Tile(float row, float col, int size, tileType type, Texture texture)
    {
        super();

        pos.x = row*size;
        pos.y = col*size;

        this.size = size;
        this.type = type;
        this.texture = texture;

        this.col = (int)row;
        this.row = (int)col;
    }

    public String DebugTile()
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

    public boolean isPassable() {
        return (!isWater() && !isCliff());
    }

    public boolean isOccupied()
    {
        return occupied;
    }
}
