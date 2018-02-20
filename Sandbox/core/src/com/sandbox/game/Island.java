package com.sandbox.game;

/**
 * Created by Southridge on 2018-02-16.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sandbox.game.Enums.tileType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Island {

    //TODO: Maybe find a better way to generate an island
    private int fillPercent;
    private int chunkSize;
    private int border;

    Tile centreTile;

    Chunk chunk;
    Chunk smoothedChunk;

    ArrayList<Entity> entities = new ArrayList<Entity>();

    //for mapping sprites
    //Obsolete for now
    private String[] a_grass_left = {"001001001", "001001000", "000001001"};
    /*
    0 0 1   0 0 1   0 0 0
    0 0 1   0 0 1   0 0 1
    0 0 1   0 0 0   0 0 1
     */
    private String[] a_grass_right = {"100100100","100100000","000100100"};
    /*
    1 0 0   1 0 0   0 0 0
    1 0 0   1 0 0   1 0 0
    1 0 0   0 0 0   1 0 0
     */
    private String[] a_grass_r_end = {"100000000"};
    /*
    1 0 0
    0 0 0
    0 0 0
     */
    private String[] a_grass_l_end = {"001000000"};
    /*
    0 0 1
    0 0 0
    0 0 0
     */
    private String[] a_grass_top = {"000000111", "000000011","000000110"};
    /*
    0 0 0   0 0 0   0 0 0
    0 0 0   0 0 0   0 0 0
    1 1 1   0 1 1   1 1 0
     */
    private String[] a_grass_top_right = {"000000100"};
    /*
    0 0 0
    0 0 0
    1 0 0
     */
    private String[] a_grass_top_left = {"000000001"};
    /*
    0 0 0
    0 0 0
    0 0 1
     */

    public Island(Box2DWorld box2D, int chunkSize, int border, int fillPercent)
    {
        this.chunkSize = chunkSize;
        this.border = border;
        this.fillPercent = fillPercent;

        //create new chunks
        chunk = new Chunk(chunkSize, 8);
        smoothedChunk = new Chunk(chunkSize, 8);

        //initialize island
        Reset(box2D);
    }

    public void Reset(Box2DWorld box2D)
    {
        entities.clear();
        box2D.Clear();
        SetupTiles();
        AssignTileCodes();
        GenerateColliders(box2D);
        AddEntities(box2D);
    }

    public Vector3 GetCentreTilePos()
    {
        return centreTile.pos;
    }

    public void ClearRemovedEntities(Box2DWorld box2D)
    {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext())
        {
            Entity e = it.next();
            if(e.remove)
            {
                e.RemoveBodies(box2D);
                box2D.RemoveEntityFromMap(e);
                it.remove();
            }
        }
    }

    private void SetupTiles()
    {
        //get reference positions
        int centerRow = chunk.size / 2;
        int centerCol = chunk.size / 2;

        // Loop through the chunk and add tiles
        for(int row = 0; row < chunk.size; row ++)
        {
            for(int col = 0; col < chunk.size; col ++)
            {
                //Water - 0
                //Grass - 1

                //Create TILE
                //default tile is water
                Tile tile = new Tile(row, col, chunk.tileSize, tileType.Water, GetRandomWaterTexture());
                //since the island is smaller than chunk,
                //check if current index is within island size.

                if(row > border && row < (chunkSize-border) && col > border && col < (chunkSize-border))
                {
                    //Randomly assign tiles type
                    //TODO: This random system is not very good...
                    if(MathUtils.random(100)>20)
                    {
                        tile.texture = GetRandomGrassTexture();
                        tile.type = tileType.Grass;
                        tile.code = "1";
                    }
                }

                //add tile to chunk.
                chunk.tiles[row][col] = tile;
            }
        }

        //Initialize smoothedChunk with water
        //At this point, chunk is randomly filled with grass, and smoothedChunk is just water.
        for(int i = 0; i < chunkSize; i++)
        {
            for(int j = 0; j < chunkSize; j++)
            {
                Tile tile = new Tile(i, j, 8, tileType.Water, GetRandomWaterTexture());
                smoothedChunk.tiles[i][j] = tile;
            }
        }

        //TODO: Smoothed island is not really smooth...
        //Smooth out chunk
        SmoothMap();
        //Set centre tile for camera positioning
        centreTile = chunk.GetTile(centerRow, centerCol);
    }

    private void SmoothMap ()
    {
        //first loop, get surrounding tiles for each tile in chunk
        //we are excluding the outer most edge
        for(int r = 1; r < chunkSize-1; r++)
        {
            for(int c = 1; c < chunkSize-1; c++)
            {
                //if current tile is not null
                if(chunk.tiles[r][c] != null)
                {
                    //Get its surrounding tiles.
                    //At this point, smoothedChunk is still empty.
                    chunk.tiles[r][c].surroundingTiles = GetSurroundingTiles(r, c, chunk);
                    //Debug info
                    //System.out.println("Position: " + chunk.tiles[r][c].row + ", " + chunk.tiles[r][c].col);
                    //System.out.println("Current tile type: " + chunk.GetTileCode(r, c));
                    //System.out.println("Neighboring tiles: " + chunk.tiles[r][c].surroundingTiles);
                    //System.out.println();
                }
            }
        }

        //second loop, smooth tiles according to their surrounding tiles
        for(int r = 1; r < chunkSize-1; r++)
        {
            for(int c = 1; c < chunkSize-1; c++)
            {
                //Write into smoothedChunk
                //can the threshold be a variable?
                if (chunk.tiles[r][c].surroundingTiles >= 4)
                {
                    smoothedChunk.tiles[r][c].texture = GetRandomWaterTexture();
                    smoothedChunk.tiles[r][c].type = Enums.tileType.Water;
                }
                else if (chunk.tiles[r][c].surroundingTiles < 4)
                {
                    smoothedChunk.tiles[r][c].texture = GetRandomGrassTexture();
                    smoothedChunk.tiles[r][c].type = Enums.tileType.Grass;
                }
            }
        }

        //copy tiles over from smoothedChunk to chunk,
        //because we still want to use chunk for everything.
        for(int i = 0; i < chunkSize; i++)
        {
            for(int j = 0; j < chunkSize; j++)
            {
                chunk.tiles[i][j] = smoothedChunk.tiles[i][j];
            }
        }
        //don't know the difference between the loop above and
        //chunk = smoothedChunk;

    }

    private int GetSurroundingTiles(int r, int c, Chunk chunk)
    {
        int surroundingTiles = 0;

        int[] adjacentRows = {1, 0, -1};
        int[] adjacentCols = {-1, 0, 1};

        for(int row : adjacentRows)
        {
            for(int col : adjacentCols)
            {
                //check all tiles surrounding current tile
                if(chunk.tiles[r+row][c+col].type == tileType.Water)
                {
                    surroundingTiles++;
                }
            }
        }
        return surroundingTiles-1;
    }


    //TODO: Maybe add a more elaborate way to populate the island?
    private void AddEntities(Box2DWorld box2D)
    {
        // ADD TREES
        for(Tile[] tiles: chunk.tiles)
        {
            for(Tile tile : tiles)
            {
                if(tile.isGrass())
                {
                    if(MathUtils.random(100) > 90)
                    {
                        if(!tile.occupied)
                        {
                            entities.add(new Tree(tile.pos, box2D));
                            tile.occupied = true;
                        }
                    }
                }
            }
        }

        // ADD HOUSES
        for(Tile[] tiles : chunk.tiles)
        {
            for(Tile tile : tiles)
            {
                if(tile.isGrass())
                {
                    if(MathUtils.random(100) > 95)
                    {
                        if(!tile.occupied)
                        {
                            entities.add(new House(tile.pos, box2D));
                            tile.occupied = true;
                        }
                    }
                }
            }
        }
    }

    private void UpdateImage(Tile tile)
    {
        // Secondary Texture is to add edges to tiles
        // TODO: Some textures missing for certain tile placements
        if(Arrays.asList(a_grass_left).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_left);
        } else if(Arrays.asList(a_grass_right).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_right);
        } else if(Arrays.asList(a_grass_r_end).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_left_upper_edge);
        } else if(Arrays.asList(a_grass_l_end).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_right_upper_edge);
        } else if(Arrays.asList(a_grass_top).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_top);
        } else if(Arrays.asList(a_grass_top_right).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_top_right);
        } else if(Arrays.asList(a_grass_top_left).contains(tile.code)){
            tile.secondaryTextures.add(Asset.grass_top_left);
        }
    }

    //TODO: Collider gen is broken
    private void GenerateColliders(Box2DWorld box2D)
    {
        for(Tile[] tiles : chunk.tiles)
        {
            for(Tile tile : tiles)
            {
                //there's no need to generate hitboxes for grass tiles.
                if(!tile.passable())
                {
                    if(!tile.isAllWater())
                    {
                        System.out.println("Tile [" + tile.row + ", " + tile.col + "] is a collider.");
                        System.out.println(tile.code);
                        Box2DHelper.CreateBody(box2D.world, chunk.tileSize, chunk.tileSize, 0, 0, tile.pos, BodyDef.BodyType.StaticBody);
                    }
                }
            }
        }
    }

    private Texture GetRandomGrassTexture()
    {
        Texture grass;

        int tile = MathUtils.random(20);
        switch (tile) {
            case 1:  grass = Asset.grass_01;
                break;
            case 2:  grass = Asset.grass_02;
                break;
            case 3:  grass = Asset.grass_03;
                break;
            case 4:  grass = Asset.grass_04;
                break;
            default: grass = Asset.grass_01;
                break;
        }

        return grass;
    }

    private Texture GetRandomWaterTexture()
    {
        Texture water;

        int tile = MathUtils.random(20);
        switch (tile) {
            case 1:  water = Asset.water_01;
                break;
            case 2:  water = Asset.water_02;
                break;
            case 3:  water = Asset.water_03;
                break;
            case 4:  water = Asset.water_04;
                break;
            default: water = Asset.water_01;
                break;
        }

        return water;
    }

    private void AssignTileCodes()
    {
        for(Tile[] tiles : chunk.tiles){
            for(Tile tile : tiles){

                // Check all surrounding tiles and set 1 for pass 0 for non pass
                // 0 0 0
                // 0 X 0
                // 0 0 0

                int[] rows = {1, 0, -1};
                int[] cols = {-1, 0, 1};

                for(int r: rows){
                    for(int c: cols){
                        tile.code += chunk.GetTileCode(tile.row + r, tile.col + c);
                        //UpdateImage(tile);
                    }
                }
            }
        }
    }
}
