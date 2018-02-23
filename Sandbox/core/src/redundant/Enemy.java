package redundant;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sandbox.game.Entity;
import com.sandbox.game.Enums;
import com.sandbox.game.Enums.entityType;

public class Enemy extends Entity{

    String enemyName;
    int health;

    public Enemy(Enums.entityType type, Vector3 pos, Box2DWorld box2D)
    {
        this.type = type;
        width = 5;
        height = 5;
        this.pos = pos;
        speed = 10f;
        //assign texture
        body = Box2DHelper.CreateBody(box2D.world, width, height/2, width, 0, pos, BodyDef.BodyType.DynamicBody);
        sensor = Box2DHelper.CreateSensor(box2D.world, width, height*0.85f, 0, 0, pos, BodyDef.BodyType.DynamicBody);
        hashcode = sensor.getFixtureList().get(0).hashCode();
    }
}
