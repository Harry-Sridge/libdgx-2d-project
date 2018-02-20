package com.sandbox.game;

import Box2D.Box2DWorld;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Collections;

public class Sandbox extends ApplicationAdapter {

    private SpriteBatch batch;
	private OrthographicCamera camera;
	private Control control;
	private Box2DWorld box2D;

	private int displayW;
	private int displayH;

	Island island;
	Player player;

	@Override
	public void create () {

		batch = new SpriteBatch();
        box2D = new Box2DWorld();

        //Set window
		displayW = Gdx.graphics.getWidth();
		displayH = Gdx.graphics.getHeight();

		int h = (int)(displayH/Math.floor(displayH/160));
		int w = (int)(displayW/(displayH/(displayH/Math.floor(displayH/160))));

		camera = new OrthographicCamera(w,h);
		camera.zoom = 1f;

		//Set controller
		control = new Control(displayW, displayH, camera);
		Gdx.input.setInputProcessor(control);

        Asset.Load();

        //Initialize basic world objects
        island = new Island(box2D, 30, 5, 50);
        player = new Player(island.centreTile.pos, box2D);
        island.entities.add(player);

        //Add entities to hash map for collisions
        box2D.PopulateEntityMap(island.entities);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//Pre render
        if(control.reset)
        {
            island = new Island(box2D, 30, 5, 50);
            player.Reset(box2D, island.GetCentreTilePos());
            island.entities.add(player);
            box2D.PopulateEntityMap(island.entities);
            control.reset = false;
        }

		player.update(control);
        camera.position.lerp(player.pos, 0.1f);
		camera.update();
        Collections.sort(island.entities);

        batch.setProjectionMatrix(camera.combined);
		batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		//Render
		batch.begin();
        // Draw all tiles in the chunk / chunk rows
        for(Tile[] tiles : island.chunk.tiles)
        {
            for(Tile tile : tiles)
            {
                batch.draw(tile.texture, tile.pos.x, tile.pos.y, tile.size, tile.size);
                if (tile.secondaryTextures != null)
				{
				    for(Texture texture : tile.secondaryTextures)
						batch.draw(texture, tile.pos.x, tile.pos.y, tile.size, tile.size);
				}
            }
        }
        //Draw entities on island
        for(Entity e : island.entities)
            e.Draw(batch);

		batch.end();

		//Post render
		box2D.tick(camera, control);
		island.ClearRemovedEntities(box2D);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
