package Box2D;

/**
 * Created by zliu on 2018-02-16.
 */

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Box2DHelper {

    public static Body CreateBody(World world, float w, float h, float xOffset, float yOffset, Vector3 pos, BodyDef.BodyType type)
    {
        Body body;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((pos.x+w/2)+xOffset, (pos.y+h/2)+yOffset);
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;
        bodyDef.type = type;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);

        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.4f;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    public static Body CreateSensor(World world, float w, float h, float xOffset, float yOffset, Vector3 pos, BodyDef.BodyType type)
    {
        Body body;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set((pos.x+w/2)+xOffset, (pos.y+h/2)+yOffset);
        bodyDef.angle = 0;
        bodyDef.fixedRotation = true;
        bodyDef.type = type;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(w/2, h/2);

        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }
}
