package entities;

/**
 * Created by zliu on 2018-02-16.
 */

import Box2D.*;
import com.sandbox.game.Asset;
import com.sandbox.game.Control;
import redundant.Inventory;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sandbox.game.Enums.entityType;

import java.util.ArrayList;

public class Player extends Entity {

    private ArrayList<Entity> interactEntities;

    public Player(Vector3 pos, Box2DWorld box2D)
    {
        //initialize entity
        type = entityType.Player;
        width = 5;
        height = 5;
        this.pos = pos;
        texture = Asset.player;
        speed = 20f;
<<<<<<< HEAD:Sandbox/core/src/entities/Player.java
        inventory = new Inventory();

=======
>>>>>>> parent of 0f0b0a5... inventory:Sandbox/core/src/com/sandbox/game/Player.java
        Reset(box2D, pos);
    }

    public void Reset(Box2DWorld box2D, Vector3 pos)
    {
        this.pos.set(pos);
        body = Box2DHelper.CreateBody(box2D.world, width, height/2, 0, 0, pos, BodyDef.BodyType.DynamicBody);
        hashcode = body.getFixtureList().get(0).hashCode();
        interactEntities = new ArrayList<Entity>();
    }
    public void update(Control control)
    {
        xDir = 0;
        yDir = 0;

        if(control.down)
            yDir = -1;
        
        if(control.up)
            yDir = 1;
        
        if(control.left)
            xDir = -1;
        
        if(control.right)
            xDir = 1;
        
        if(control.right && control.up) {
        	xDir = (float) Math.sqrt(0.5);
        	yDir = (float) Math.sqrt(0.5);
        }
        
        if(control.right && control.down) {
        	xDir = (float) Math.sqrt(0.5);
        	yDir = (float) - Math.sqrt(0.5);
        }
        
        if(control.left && control.up) {
        	xDir = (float) - Math.sqrt(0.5);
        	yDir = (float) Math.sqrt(0.5);
        }
        if(control.left && control.down) {
        	xDir = (float) - Math.sqrt(0.5);
        	yDir = (float) - Math.sqrt(0.5);
        }

        body.setLinearVelocity(xDir*speed, yDir*speed);
        pos.x = body.getPosition().x - width/2;
        pos.y = body.getPosition().y - height/4;

        //if interact key is pressed
        if(control.interact && interactEntities.size()>0)
        {
            interactEntities.get(0).Interact();
            control.interact = false;
        }

        control.interact = false;
    }

    @Override
    public void Collision(Entity e, boolean isInTrigger)
    {
        if(isInTrigger)
            interactEntities.add(e);
        else
            interactEntities.remove(e);
    }
}
