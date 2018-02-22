package com.sandbox.game;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * Created by zliu on 2018-02-17.
 */

public class Tree extends Entity{

    public Tree(Vector3 pos, Box2DWorld box2D)
    {
        super();
        type = Enums.entityType.Tree;
        width = 8;
        height = 8;
        this.pos = pos;
        texture = Asset.tree;
        body = Box2DHelper.CreateBody(box2D.world, width/2, height/2, width/4, 0, pos, BodyDef.BodyType.StaticBody);
        sensor = Box2DHelper.CreateSensor(box2D.world, width, height*0.85f, 0, 0, pos, BodyDef.BodyType.DynamicBody);
        hashcode = sensor.getFixtureList().get(0).hashCode();
    }

    @Override
    public void Interact()
    {
        remove = true;
    }
}
