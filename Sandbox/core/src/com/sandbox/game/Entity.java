package com.sandbox.game;

/**
 * Created by Southridge on 2018-02-16.
 */

import Box2D.Box2DWorld;
import redundant.Inventory;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.sandbox.game.Enums.*;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Entity implements Comparable<Entity> {

    public Body body;
    public int hashcode;
    public Body sensor;
    public boolean removed;
    public entityType type;
    public Vector3 pos;
    public Texture texture;
    public float width;
    public float height;

    public Inventory inventory;

    float xDir = 0;
    float yDir = 0;
    float speed = 0;

    //Entity logic
    public Texture shadow;
    public Enums.entityState state;
    public Boolean ticks;
    public float time;
    public Vector3 destinationVector;
    public Tile currentTile;
    public float coolDown;

    public Entity()
    {
        pos = new Vector3();
    }

    //Draw a specific entity
    public void Draw(SpriteBatch batch)
    {
        if(texture != null)
            batch.draw(texture, pos.x, pos.y, width, height);
        if(shadow != null)
            batch.draw(shadow, pos.x, pos.y, width, height);
    }

    //Z sorting
    public int compareTo(Entity entity)
    {
        float tempY = entity.pos.y;
        float compareY = pos.y;
        return (tempY < compareY) ? -1 : (tempY > compareY) ? 1 : 0;
    }

    public void Collision(Entity e, boolean isInTrigger)
    {

    }

    public void Interact(Entity entity)
    {

    }

    public void RemoveBodies(Box2DWorld box2D)
    {
        if (sensor != null)
            box2D.world.destroyBody(sensor);
        if (body != null)
            box2D.world.destroyBody(body);
    }

    public void Tick(float delta)
    {
        time += delta;
    }

    public void Tick(float delta, Chunk chunk)
    {

    }

    public void GetVector(Vector3 destination)
    {
        float dx = destination.x - pos.x;
        float dy = destination.y - pos.y;
        double h = Math.sqrt(dx * dx + dy * dy);
        float dn = (float)(h / Math.sqrt(2));

        this.destinationVector = new Vector3(dx / dn, dy / dn, 0);
    }
}
