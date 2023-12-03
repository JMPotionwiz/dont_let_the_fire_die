package items;

import core.main;

public class StoneItem extends Item {
    public StoneItem() {}
    protected StoneItem(String[] in) {}
    public Item copy() {return new StoneItem();}
    
    public int stackSize() {return 3;}
    public String item() {return "item.stone";}
    public String entity() {return "item_entity.stone";}
}
