package com.sandbox.game;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 * Created by Southridge on 2018-02-17.
 */
public class House extends Entity{

    public House(Vector3 pos, Box2DWorld box2D)
    {
        super();
        type = Enums.entityType.House;
        width = 7;
        height = 7;
        this.pos = pos;
        texture = Asset.house;
        body = Box2DHelper.CreateBody(box2D.world, width/1.5f, height/1.5f, width/4, 0, pos, BodyDef.BodyType.StaticBody);
        //no sensor
    }
}
