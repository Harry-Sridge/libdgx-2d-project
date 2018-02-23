package com.sandbox.game;

/**
 * Created by zliu on 2018-02-16.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Control extends InputAdapter implements InputProcessor {

    OrthographicCamera camera;

    public boolean up;
    public boolean down;
    public boolean left;
    public boolean right;

    public boolean LMB;
    public boolean RMB;
    public boolean processedClick;
    public Vector2 mouseClickPos = new Vector2();
    public Vector2 mapClickPos = new Vector2();

    public boolean debug;
    public boolean reset;
    public boolean interact;
    public boolean inventory;

    int screenWidth;
    int screenHeight;

    public Control(int screenWidth, int screenHeight, OrthographicCamera camera)
    {
        this.camera = camera;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    private void SetMouseClickPos(int x, int y)
    {
        mouseClickPos.set(x, screenHeight - y);
        mapClickPos.set(GetMapCoordinate(mouseClickPos));
    }

    public Vector2 GetMapCoordinate(Vector2 mousePos)
    {
        Vector3 vector3 = new Vector3(mousePos.x, screenHeight-mousePos.y, 0);
        this.camera.unproject(vector3);
        return new Vector2(vector3.x, vector3.y);
    }

    @Override
    public boolean keyDown(int keycode)
    {
        switch (keycode) {
            case Keys.DOWN:
                down = true;
                break;
            case Keys.UP:
                up = true;
                break;
            case Keys.LEFT:
                left = true;
                break;
            case Keys.RIGHT:
                right = true;
                break;
            case Keys.W:
                up = true;
                break;
            case Keys.A:
                left = true;
                break;
            case Keys.S:
                down = true;
                break;
            case Keys.D:
                right = true;
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        switch (keycode) {
            case Keys.DOWN:
                down = false;
                break;
            case Keys.UP:
                up = false;
                break;
            case Keys.LEFT:
                left = false;
                break;
            case Keys.RIGHT:
                right = false;
                break;
            case Keys.W:
                up = false;
                break;
            case Keys.A:
                left = false;
                break;
            case Keys.S:
                down = false;
                break;
            case Keys.D:
                right = false;
                break;
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Keys.BACKSPACE:
                debug = !debug;
                break;
            case Keys.R:
                reset = true;
                break;
            case Keys.E:
                interact = true;
                break;
            case Keys.I:
                inventory = true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char c)
    {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button)
    {
        if(pointer == 0 && button == 0){
            LMB = true;
        } else if (pointer == 0 && button == 0){
            RMB = true;
        }

        SetMouseClickPos(x, y);
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if(pointer == 0 && button == 0){
            LMB = false;
            processedClick = false;
        } else if (pointer == 0 && button == 0){
            RMB = false;
        }

        SetMouseClickPos(x, y);
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        SetMouseClickPos(x, y);
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
