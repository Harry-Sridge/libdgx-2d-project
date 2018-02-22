package com.sandbox.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class MapGeneration {

    private Chunk map;
    private int chunkSize;
    private int tileSize;
    private int iterations;
    private int centerRow;
    private int centerCol;

    public MapGeneration(Chunk canvas, int chunkSize, int tileSize, int iterations)
    {
        map = canvas;
        this.chunkSize = chunkSize;
        this.tileSize = tileSize;
        this.iterations = iterations;
    }

    public Chunk SetupTiles()
    {
        //get reference positions
        centerRow = (int)(chunkSize / 2);
        centerCol = (int)(chunkSize / 2);

        // Loop through the chunk and add tiles
        for(int row = 0; row < chunkSize; row ++)
        {
            for(int col = 0; col < chunkSize; col ++)
            {
                //Water - 0
                //Grass - 1
                //default tile is water
                Tile tile = new Tile(col, row, tileSize, Enums.tileType.Water, GetRandomWaterTexture());
                map.tiles[row][col] = tile;
            }
        }

        //Store initial seeds
        List<Tile> seeds = new ArrayList<Tile>();

        //Manually add seeds here
        //We can think of a better way to add seeds
        seeds.add(map.tiles[centerRow][centerCol]);
        seeds.add(map.tiles[centerRow][centerCol+20]);
        seeds.add(map.tiles[centerRow][centerCol-20]);
        seeds.add(map.tiles[centerRow+20][centerCol]);
        seeds.add(map.tiles[centerRow-20][centerCol]);

        //Generate map using these seeds
        return(SmoothMap(map, seeds, iterations));
    }

    private Chunk SmoothMap (Chunk map, List<Tile> seeds, int iterations)
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
                        t.type = Enums.tileType.Grass;
                        t.texture = GetRandomGrassTexture();
                        map.tiles[t.row][t.col] = t;
                        //the new grass tile is then will become next batch of seeds.
                        tempSeeds.add(t);
                        spread++;
                    }
                }

                //If the current seed does not spread, keep that seed.
                if(spread == 0)
                    tempSeeds.add(nextSeeds.get(j));

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

        return map;
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
                    if(map.tiles[r+row][c].type == Enums.tileType.Water)
                        freeTiles.add(map.tiles[r+row][c]);
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
                    if(map.tiles[r][c+col].type == Enums.tileType.Water)
                        freeTiles.add(map.tiles[r][c+col]);
                }
            }
        }

        return freeTiles;
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

    public Tile GetCentreTile()
    {
        return map.tiles[centerRow][centerCol];
    }
}

