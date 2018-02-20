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

    //TODO: Find a better way to generate an island
    private int fillPercent;
    private int chunkSize;
    private int border;

    Tile centreTile;

    Chunk chunk;
    Chunk smoothedChunk;
    ArrayList<Entity> entities = new ArrayList<Entity>();

    //for mapping sprites
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

        //create a new chunk
        chunk = new Chunk(chunkSize, 8);
        smoothedChunk = new Chunk(chunkSize, 8);
        Reset(box2D);
    }

    public void Reset(Box2DWorld box2D)
    {
        entities.clear();
        box2D.Clear();
        SetupTiles();
        GenerateHitboxes(box2D);
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
        int centreTileRow = chunk.size / 2;
        int centreTileCol = chunk.size / 2;

        // Loop through the chunk and add tiles
        for(int row = 0; row < chunk.size; row ++)
        {
            for(int col = 0; col < chunk.size; col ++)
            {
                //Water - 0
                //Grass - 1

                // Create TILE
                Tile tile = new Tile(col, row, chunk.tileSize, tileType.Water, GetRandomWaterTexture());

                // Make a small island
                // If the current pos is within island boundary
                if(row>border&&row<(chunkSize-border)&&col>border&&col<(chunkSize-border))
                {
                    if(MathUtils.random(100)>(100-fillPercent))
                    {
                        tile.texture = GetRandomGrassTexture();
                        tile.type = tileType.Grass;
                    }
                }
                chunk.tiles[row][col] = tile;
            }
        }

        //Initialize smoothedChunk with water
        for(int i = 0; i < chunkSize; i++)
        {
            for(int j = 0; j < chunkSize; j++)
            {
                Tile tile = new Tile(i, j, 8, tileType.Water, GetRandomWaterTexture());
                smoothedChunk.tiles[i][j] = tile;
            }
        }

        //TODO: Smoothed island is not really smooth...
        SmoothMap(chunk);
        AssignTileCodes(smoothedChunk);
        //Set centre tile for camera positioning
        centreTile = chunk.GetTile(centreTileRow, centreTileCol);
    }

    private void SmoothMap (Chunk chunk)
    {
        //first loop
        for(int row = 1; row < chunk.size-1; row++)
        {
            for(int col = 1; col < chunk.size-1; col++)
            {
                //if current tile is not null
                if(chunk.tiles[row][col] != null)
                {
                    //Check its neighboring tiles
                    //At this point, smoothedChunk is still empty.
                    int neighboringTiles = GetSurroundingTiles(row, col, chunk);
                    System.out.println("Position: " + row + ", " + col);
                    System.out.println("Current tile type: " + chunk.GetTile(row, col).type);
                    System.out.println("Neighboring tiles: " + neighboringTiles);
                    System.out.println();

                    if (neighboringTiles > 4)
                    {
                        smoothedChunk.tiles[row][col].texture = GetRandomWaterTexture();
                        smoothedChunk.tiles[row][col].type = Enums.tileType.Water;
                    }
                    else if (neighboringTiles < 4)
                    {
                        smoothedChunk.tiles[row][col].texture = GetRandomGrassTexture();
                        smoothedChunk.tiles[row][col].type = Enums.tileType.Grass;
                    }
                }
            }
        }

        //copy
        for(int i = 0; i < chunkSize; i++)
        {
            for(int j = 0; j < chunkSize; j++)
            {
                //Copy tiles
                chunk.tiles[i][j] = smoothedChunk.tiles[i][j];
            }
        }
    }

    private int GetSurroundingTiles(int r, int c, Chunk chunk)
    {
        int surroundingTiles = 0;

        for(int x = c-1; x <= c+1; x++)
        {
            for(int y = r-1; y <= r+1; y++)
            {
                if(chunk.GetTile(x, y) != null)
                {
                    if (chunk.GetTileCode(x, y).equals("0"))
                    {
                        surroundingTiles++;
                    }
                }
            }
        }
        return surroundingTiles-1;
    }


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
        // TODO: Add array of textures per tile instead of a primary and secondary
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

    private void GenerateHitboxes(Box2DWorld box2D)
    {
        for(Tile[] tiles : chunk.tiles)
        {
            for(Tile tile : tiles)
            {
                if(!tile.passable() && !tile.isAllWater())
                {
                    Box2DHelper.CreateBody(box2D.world, chunk.tileSize, chunk.tileSize, 0, 0, tile.pos, BodyDef.BodyType.StaticBody);
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

    private void AssignTileCodes(Chunk chunk)
    {
        // Loop all tiles and set the initial code

        // 1 CHUNK ONLY ATM
        for(Tile[] tiles : smoothedChunk.tiles){
            for(Tile tile : tiles){
                // Check all surrounding tiles and set 1 for pass 0 for non pass
                // 0 0 0
                // 0 X 0
                // 0 0 0

                int[] rows = {1,0,-1};
                int[] cols = {-1,0,1};

                for(int r: rows){
                    for(int c: cols){
                        tile.code += smoothedChunk.GetTileCode(tile.row + r, tile.col + c);
                        UpdateImage(tile);
                    }
                }
            }
        }
    }
}
