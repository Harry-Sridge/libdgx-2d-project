package entities;

import Box2D.Box2DHelper;
import Box2D.Box2DWorld;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.sandbox.game.Asset;
import map.Chunk;
import entities.Entity;
import com.sandbox.game.Enums;
import map.Tile;

public class Bird extends Entity{

    public Tile destinationTile;

    private float maxHeight;
    private TextureRegion textureRegion;

    public Bird(Vector3 pos, Box2DWorld box2D, Enums.entityState state)
    {
        super();
        maxHeight = GetHeight();
        type = Enums.entityType.Bird;
        speed = MathUtils.random(20)+5;
        width = 8;
        height = 8;
        texture = Asset.tree;
        shadow = Asset.birdShadow;
        this.pos.set(pos);
        body = Box2DHelper.CreateBody(box2D.world, width/2, height/2, width/4, 0, pos, BodyDef.BodyType.StaticBody);
        sensor = Box2DHelper.CreateSensor(box2D.world, width, height*.85f, width/2, height/3, pos, BodyDef.BodyType.DynamicBody);
        hashcode = sensor.getFixtureList().get(0).hashCode();
        this.state = state;
        ticks = true;
    }

    @Override
    public void Tick(float delta, Chunk chunk)
    {
        if(isHovering())
        {
            SetLanding();
        }
        else if(isLanding())
        {
            Land();
        }
        else if(NeedsDestination())
        {
            NewDestinationOrHover(delta, chunk);
        }
        else if(HasDestination())
        {
            MoveToDestination(delta);
            ClearDestination();
        }
        else if(isNotAirborne())
        {
            SetNewState(delta);
        }

        if(isFlying())
        {
            CheckFlyHeight();
            ToggleHitBoxes(false);
        }
    }

    @Override
    public void Draw(SpriteBatch batch)
    {
        SetTextureRegion();
        SetFlipped();

        batch.draw(Asset.birdShadow, pos.x, pos.y);
        if(textureRegion != null)
        {
            batch.draw(textureRegion, pos.x, pos.y + pos.z);
        }
    }

    @Override
    public void Interact(Entity entity)
    {
        if(entity.inventory != null)
        {
            entity.inventory.AddEntity(this);
            removed = true;
        }
    }

    private void ToggleHitBoxes(boolean state)
    {
        body.setActive(state);
        sensor.setActive(state);
    }

    private void SetNewState(float delta)
    {
        if (coolDown > 0)
        {
            coolDown -= delta;
            if (isWalking())
            {
                Walk(delta);
            }
        }
        else
        {
            if (MathUtils.randomBoolean(.2f))
            {
                state = Enums.entityState.Flying;
            }
            else if (MathUtils.randomBoolean(.5f))
            {
                state = Enums.entityState.Feeding;
                coolDown = .5f;
            }
            else if (MathUtils.randomBoolean(.3f))
            {
                state = Enums.entityState.Walking;
                coolDown = 1f;
            }
        }
    }

    private void ClearDestination()
    {
        if(atDestination())
        {
            destinationVector = null;
            destinationTile = null;
        }
    }

    private void UpdatePositions()
    {
        sensor.setTransform(body.getPosition(), 0);
        pos.x = body.getPosition().x - width / 2;
        pos.y = body.getPosition().y - height / 4;
    }

    private void SetTextureRegion()
    {
        if(isFlying() || isLanding())
        {
            textureRegion = Asset.birdFlyAnim.getKeyFrame(time, true);
        }
        else if(isWalking())
        {
            textureRegion = Asset.birdWalkAnim.getKeyFrame(time, true);
        }
        else if(isFeeding())
        {
            textureRegion = Asset.birdPeckAnim.getKeyFrame(time, true);
        }
        else if(isWalking())
        {
            textureRegion = Asset.birdWalkAnim.getKeyFrame(time, true);
        }
    }

    private void SetFlipped()
    {
        if(destinationVector != null)
        {
            if(destinationVector.x > 0 && !textureRegion.isFlipX())
            {
                textureRegion.flip(true, false);
            }
            else if(destinationVector.x < 0 && textureRegion.isFlipX())
            {
                textureRegion.flip(true, false);
            }
        }
    }

    private void MoveToDestination(float delta)
    {
        body.setTransform(body.getPosition().interpolate(
                new Vector2(destinationTile.pos.x + width, destinationTile.pos.y + height),
                delta * speed / 4, Interpolation.circle), 0);
        UpdatePositions();
    }

    private float GetHeight()
    {
        return MathUtils.random(10)+10;
    }

    private void CheckFlyHeight()
    {
        if (isNotHigh()) pos.z += 0.1;
        if (isTooHigh()) pos.z -= 0.1;
    }

    private void Land()
    {
        if(isAirBorne())
            pos.z -= 0.5f;
        if(pos.z <= 0)
        {
            pos.z = 0;
            state = Enums.entityState.None;
            ToggleHitBoxes(true);
        }
    }

    private void SetLanding()
    {
        if(MathUtils.randomBoolean(.2f))
            state = Enums.entityState.Landing;
    }

    private void NewDestinationOrHover(float delta, Chunk chunk)
    {
        if(MathUtils.randomBoolean(.85f) || currentTile.isWater())
        {
            SetDestination(delta, chunk);
            maxHeight = GetHeight();
        }
        else
        {
            state = Enums.entityState.Hovering;
        }
    }

    private void SetDestination(float delta, Chunk chunk)
    {
        for(Tile[] tiles : chunk.tiles)
        {
            if(destinationTile != null)
                break;

            //This could be a bit problematic as we want the bird to not fly around the ENTIRE map
            //Maybe find a way to only check tiles close to the bird?
            for(Tile tile : tiles)
            {
                if(tile.isGrass() && MathUtils.random(100)>99 && tile != currentTile)
                {
                    destinationTile = tile;
                    GetVector(destinationTile.pos);
                    break;
                }
            }
        }
    }

    private void Walk(float delta)
    {
        if(!currentTile.isPassable())
        {
            if(textureRegion.isFlipX())
            {
                body.setTransform(body.getPosition().x - speed / 4 * delta, body.getPosition().y,0);
            }
            else
            {
                body.setTransform(body.getPosition().x + speed / 4 * delta, body.getPosition().y,0);
            }
            UpdatePositions();
        }
    }

    private boolean HasDestination()
    {
        return destinationVector != null;
    }

    private boolean atDestination()
    {
        return currentTile.pos.epsilonEquals(destinationTile.pos, 20);
    }

    private boolean NeedsDestination()
    {
        return (destinationVector == null && isFlying());
    }

    private boolean isFlying()
    {
        return state == Enums.entityState.Flying;
    }

    private boolean isHovering()
    {
        return state == Enums.entityState.Hovering;
    }

    private boolean isLanding()
    {
        return state == Enums.entityState.Landing;
    }

    private boolean isAirBorne()
    {
        return pos.z > 0;
    }

    private boolean isNotAirborne()
    {
        return pos.z == 0;
    }

    public boolean isNotHigh()
    {
        return pos.z < maxHeight;
    }

    public boolean isTooHigh()
    {
        return pos.z > maxHeight;
    }

    private boolean isWalking()
    {
        return state == Enums.entityState.Walking;
    }

    private boolean isFeeding()
    {
        return state == Enums.entityState.Feeding;
    }
}
