package redundant;

import java.util.HashMap;

import com.sandbox.game.Entity;

/**
 * Created by zliu on 2018-02-22.
 */
public class Inventory {

    HashMap<Integer, Entity> entities;

    public Inventory()
    {
        Reset();
    }

    public int GetInventorySize()
    {
        return entities.size();
    }

    public void AddEntity(Entity entity)
    {
        entities.put(GetInventorySize(), entity);
    }

    public HashMap<Integer, Entity> GetInventory()
    {
        return entities;
    }

    public void Print()
    {
        System.out.println("----- Inventory -----");
        for(int i = 0; i < entities.size(); i++)
        {
            Entity e = entities.get(i);
            System.out.println("[" + i + "] " + e.type.toString());
        }
        System.out.println("---------------------");
    }

    public void Reset()
    {
        entities = new HashMap<Integer, Entity>();
    }

}
