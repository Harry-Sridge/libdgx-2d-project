package com.sandbox.game;

/**
 * Created by zliu on 2018-02-16.
 */

public class Enums {

    public enum tileType
    {
        Grass, Water, Cliff
    }

    public enum entityType
    {
        Player, Tree, House, Bird
    }

    public enum entityState
    {
        None, Idle, Feeding, Walking, Flying, Hovering, Landing
    }
}
