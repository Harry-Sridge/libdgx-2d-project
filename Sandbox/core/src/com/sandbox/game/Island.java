package com.sandbox.game;

/**
 * Created by zliu on 2018-02-16.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sandbox.game.Enums.tileType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class Island {

    //TODO: Maybe find a better way to generate an island
    private int chunkSize;
    private int iterations;

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

    public Island(Box2DWorld box2D, int chunkSize, int iterations)
    {
        this.chunkSize = chunkSize;
        this.iterations = iterations;

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
        AddSecondaryTextures();
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
        int centerRow = chunkSize / 2;
        int centerCol = chunkSize / 2;

        // Loop through the chunk and add tiles
        for(int row = 0; row < chunkSize; row ++)
        {
            for(int col = 0; col < chunkSize; col ++)
            {
                //Water - 0
                //Grass - 1
                //default tile is water
                Tile tile = new Tile(col, row, chunk.tileSize, tileType.Water, GetRandomWaterTexture());
                chunk.tiles[row][col] = tile;
            }
        }

        //Store initial seeds
        List<Tile> seeds = new ArrayList<Tile>();

        //Manually add seeds here
        //You can think of a better way to add seeds
        Tile seed = new Tile(centerRow, centerCol, chunk.tileSize, tileType.Grass, GetRandomGrassTexture());
        chunk.tiles[centerRow][centerCol] = seed;
        seeds.add(seed);

        SmoothMap(chunk, seeds, iterations);

        //Set centre tile for camera positioning
        centreTile = chunk.GetTile(centerRow, centerCol);
    }

    //TODO: SmoothMap() still causes diagonal anomalies, maybe write into a fresh 2D array?
    private void SmoothMap (Chunk chunk, List<Tile> seeds, int iterations)
    {
        //Store the newest seeds (the ones that had just been generated)
        //to avoid repeatedly checking older tiles
        List<Tile> nextSeeds = new ArrayList<Tile>();
        List<Tile> tempSeeds = new ArrayList<Tile>();
        //Add first seed to list
        for(Tile t : seeds)
            nextSeeds.add(t);

        for(int i = 0; i < iterations; i++)
        {
            System.out.println("Iteration " + i);
            for(int j = 0; j < nextSeeds.size(); j++)
            {
                //current seed
                Tile seed = nextSeeds.get(j);
                //get max possible spread from current seed
                List<Tile> freeTiles = GetFreeTiles(seed.row, seed.col);

                //Debug
                System.out.println("Seed " + j + " pos: [" + seed.row + ", " + seed.col + "] Free tiles: " + freeTiles.size());

                int spread = 0;
                for(Tile t : freeTiles)
                {
                    //Each free water tile surrounding the current seed tile
                    //will have a 50% of becoming a grass tile.
                    if(Math.random() > 0.5f)
                    {
                        t.type = tileType.Grass;
                        t.texture = GetRandomGrassTexture();
                        chunk.tiles[t.row][t.col] = t;
                        //the new grass tile is then will become next batch of seeds.
                        tempSeeds.add(t);
                        spread++;
                    }

                }
                System.out.println("Spread from current seed: " + spread);
                System.out.println("Total seeds: " + tempSeeds.size());
                System.out.println();
            }
            //Update seed queue
            nextSeeds.clear();
            for(Tile t : tempSeeds)
                nextSeeds.add(t);
            tempSeeds.clear();
        }
    }

    private List<Tile> GetFreeTiles(int r, int c)
    {
        int[] adjacentRows = {-1, 0, 1};
        int[] adjacentCols = {-1, 0, 1};
        List<Tile> freeTiles = new ArrayList<Tile>();

        //check adjacent row and column separately; we do not want seeds to spread diagonally.
        //first check up and down
        //are the tiles above and below free?
        for(int row : adjacentRows)
        {
            if(r+row >= 0)
            {
                if(r+row < chunkSize)
                {
                    if(chunk.tiles[r+row][c].type == tileType.Water)
                        freeTiles.add(chunk.tiles[r+row][c]);
                }
            }
        }

        //Check left and right
        //are the tiles on both sides free?
        for(int col : adjacentCols)
        {
            if(c+col >= 0)
            {
                if(c+col < chunkSize)
                {
                    if(chunk.tiles[r][c+col].type == tileType.Water)
                        freeTiles.add(chunk.tiles[r][c+col]);
                }
            }
        }

        return freeTiles;
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

    //TODO: Probably not the best way to add secondary textures?
    private void AddSecondaryTextures()
    {
        for(int r = 1; r < chunkSize-1; r++)
        {
            for(int c = 1; c < chunkSize-1; c++)
            {
                if(chunk.tiles[r][c].type == tileType.Water)
                {
                    //bottom
                    if(chunk.tiles[r+1][c].type == tileType.Grass)
                    {
                        if(chunk.tiles[r-1][c].type == tileType.Grass)
                            chunk.tiles[r][c].secondaryTextures.add(Asset.cliff);
                        else
                            chunk.tiles[r][c].secondaryTextures.add(Asset.cliff_bottom);
                        chunk.tiles[r][c].type = tileType.Cliff;
                    }
                    //if top tile is grass
                    if(chunk.tiles[r+1][c].type == tileType.Grass)
                    {
                        //and if right tile is grass
                        if(chunk.tiles[r][c+1].type == tileType.Grass)
                        {
                            //and if left tile is grass
                            if(chunk.tiles[r][c-1].type == tileType.Grass)
                            {
                                //and if bottom tile is grass
                                if(chunk.tiles[r-1][c].type == tileType.Grass)
                                    chunk.tiles[r][c].secondaryTextures.add(Asset.grass_hole); //it's a hole
                                //and if bottom tile is water
                                else if(chunk.tiles[r-1][c].type == tileType.Water)
                                    chunk.tiles[r][c].secondaryTextures.add(Asset.grass_inlet); //it's an inlet
                            }
                            //but left tile is not grass
                            else
                                chunk.tiles[r][c].secondaryTextures.add(Asset.grass_inside_right); //it's a inside right corner
                        }
                        //but right tile is not grass, and if left tile is grass
                        else if(chunk.tiles[r][c-1].type == tileType.Grass)
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_inside_left); //it's a inside left turn.
                    }
                    //top left edge
                    if(chunk.tiles[r-1][c+1].type == tileType.Grass)
                    {
                        if(chunk.tiles[r][c+1].type == tileType.Water)
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_top_left);
                    }
                    //top right edge
                    if(chunk.tiles[r-1][c-1].type == tileType.Grass)
                    {
                        if(chunk.tiles[r][c-1].type == tileType.Water)
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_top_right);
                    }
                    //left edge
                    if(chunk.tiles[r][c+1].type == tileType.Grass)
                    {
                        if(chunk.tiles[r][c].type == tileType.Cliff)
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_left);
                        else
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_most_left);
                    }
                    //right edge
                    if(chunk.tiles[r][c-1].type == tileType.Grass)
                    {
                        if(chunk.tiles[r][c].type == tileType.Cliff)
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_right);
                        else
                            chunk.tiles[r][c].secondaryTextures.add(Asset.grass_most_right);
                    }
                    //top edge
                    if(chunk.tiles[r-1][c].type == tileType.Grass)
                    {
                        chunk.tiles[r][c].secondaryTextures.add(Asset.grass_top);
                    }

                    //bottom left edge
                    if(chunk.tiles[r+1][c-1].type == tileType.Grass)
                    {
                        if(chunk.tiles[r][c-1].type == tileType.Cliff)
                        {
                            if(chunk.tiles[r+1][c].type != tileType.Grass)
                                chunk.tiles[r][c].secondaryTextures.add(Asset.grass_left_upper_edge);
                        }
                    }

                    //bottom right edge
                    if(chunk.tiles[r+1][c+1].type == tileType.Grass)
                    {
                        if(chunk.tiles[r][c+1].type == tileType.Water)
                        {
                            if(chunk.tiles[r+1][c].type != tileType.Grass)
                                chunk.tiles[r][c].secondaryTextures.add(Asset.grass_right_upper_edge);
                        }
                    }
                }
            }
        }
    }

    private void GenerateColliders(Box2DWorld box2D)
    {
        for(Tile[] tiles : chunk.tiles)
        {
            for(Tile tile : tiles)
            {
                //there's no need to generate hit boxes for grass tiles.
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
        System.out.println();
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

                int[] rows = {-1, 0, 1};
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
