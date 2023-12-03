package items;

import core.main;
import entities.Entity;
import entities.Player;
import entities.Chest;

public class ChestItem extends Item {
    public ChestItem() {}
    protected ChestItem(String[] in) {}
    public Item copy() {return new ChestItem();}
    
    public int stackSize() {return 1;}
    public String item() {return "item.chest";}
    public String entity() {return "item_entity.chest";}
    public int cooldown() {return 48;}
    public Entity[] attack(Player a) {
        main.spawnIn.add(new Chest(a.X(), a.Y()));
        main.playSound("sfx_treehurt");
        a.addItemCount(-1, 0);
        return new Entity[] {};
    }
}
