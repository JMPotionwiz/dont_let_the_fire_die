package items;

import core.main;
import entities.Entity;
import entities.Player;

public class XfSteakItem extends Item {
    public XfSteakItem() {}
    protected XfSteakItem(String[] in) {}
    public Item copy() {return new XfSteakItem();}
    
    public int stackSize() {return 5;}
    public String item() {return "item.x_formidilosa_steak";}
    public String entity() {return "item_entity.x_formidilosa_steak";}
    public int cooldown() {return 108;}
    public Entity[] attack(Player a) {
        a.feed(6,4);
        a.addItemCount(-1, 0);
        return new Entity[] {};
    }
}
