package items;

import core.main;
import entities.Entity;
import entities.Player;

public class XfVenisonItem extends Item {
    public XfVenisonItem() {}
    protected XfVenisonItem(String[] in) {}
    public Item copy() {return new XfVenisonItem();}
    
    public int stackSize() {return 5;}
    public String item() {return "item.x_formidilosa_venison";}
    public String entity() {return "item_entity.x_formidilosa_venison";}
    public int cooldown() {return 78;}
    public Item cooked() {return Math.random() < 0.95 ? new XfSteakItem() : null;}
    public Entity[] attack(Player a) {
        a.feed(1,0);
        a.addItemCount(-1, 0);
        return new Entity[] {};
    }
}
