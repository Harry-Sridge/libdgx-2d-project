package com.sandbox.game;

/**
 * Created by zliu on 2018-02-16.
 */

import java.util.*;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sandbox.game.Enums.tileType;
import com.badlogic.gdx.math.MathUtils;

public class Island {

    //TODO: Continue generation until it reaches a set size
    private int chunkSize;

    Tile centreTile;
    Chunk chunk;
    private MapGeneration mapGen;

    ArrayList<Entity> entities = new ArrayList<Entity>();

    //Constructor
    public Island(Box2DWorld box2D, int chunkSize, int iterations)
    {
        this.chunkSize = chunkSize;

        //create new empty chunk
        chunk = new Chunk(chunkSize, 8);
        mapGen = new MapGeneration(chunk, chunkSize, chunk.tileSize, iterations);

        //initialize island
        Reset(box2D);
    }

    //Regen
    public void Reset(Box2DWorld box2D)
    {
        //clear entities and collision data
        entities.clear();
        box2D.Clear();

        //Generate chunk
        chunk = mapGen.SetupTiles();
        centreTile = mapGen.GetCentreTile();

        //Post generation procedures
        AddSecondaryTextures();
        AssignTileCodes();
        GenerateColliders(box2D);
        AddEntities(box2D);
    }

    public Vector3 GetPlayerSpawnPos()
    {
        //currently is in the middle of chunk
        return centreTile.pos;
    }

    public void ClearRemovedEntities(Box2DWorld box2D)
    {
        Iterator<Entity> it = entities.iterator();
        while (it.hasNext())
        {
            Entity e = it.next();
            if(e.removed)
            {
                e.RemoveBodies(box2D);
                box2D.RemoveEntityFromMap(e);
                it.remove();
            }
        }
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
                if(!tile.isPassable())
                {
                    if(!tile.isAllWater())
                        Box2DHelper.CreateBody(box2D.world, chunk.tileSize, chunk.tileSize, 0, 0, tile.pos, BodyDef.BodyType.StaticBody);
                }
            }
        }
        System.out.println();
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
