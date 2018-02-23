package Box2D;

/**
 * Created by zliu on 2018-02-16.
 */

import com.badlogic.gdx.physics.box2d.*;
import com.sandbox.game.Control;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.sandbox.game.Entity;

import java.util.ArrayList;
import java.util.HashMap;


public class Box2DWorld {

    public World world;
    private Box2DDebugRenderer debugRenderer;
    private HashMap<Integer, Entity> entityHashMap;

    public Box2DWorld()
    {
        world = new World(new Vector2(.0f, .0f), true);
        debugRenderer = new Box2DDebugRenderer();
        entityHashMap = new HashMap<Integer, Entity>();

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();
                ProcessCollisions(a, b, true);
            }

            @Override
            public void endContact(Contact contact) {
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();
                ProcessCollisions(a, b, false);
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {

            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {

            }
        });
    }

    public void tick(OrthographicCamera camera, Control control)
    {
        if(control.debug)
            debugRenderer.render(world, camera.combined);
        world.step(Gdx.app.getGraphics().getDeltaTime(), 6, 2);
        world.clearForces();
    }

    public void Clear()
    {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for(Body b : bodies)
        {
            world.destroyBody(b);
        }
    }

    public void PopulateEntityMap(ArrayList<Entity> entities)
    {
        entityHashMap.clear();
        for(Entity e : entities)
        {
            AddEntityToMap(e);
        }
    }

    public void AddEntityToMap(Entity entity){
        entityHashMap.put(entity.hashcode, entity);
    }

    public void RemoveEntityFromMap(Entity entity){
        entityHashMap.remove(entity.hashcode);
    }

    private void ProcessCollisions(Fixture a, Fixture b, boolean isInTrigger)
    {
        Entity entityA = entityHashMap.get(a.hashCode());
        Entity entityB = entityHashMap.get(b.hashCode());

        if(entityA != null && entityB != null)
        {
            if(a.isSensor() && !b.isSensor())
                entityB.Collision(entityA, isInTrigger);
            else if(b.isSensor() && !a.isSensor())
                entityA.Collision(entityB, isInTrigger);
        }
    }
}
