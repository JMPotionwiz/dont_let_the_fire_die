package items;

import core.main;

public class CharcoalItem extends Item implements CampfireFuel {
    public CharcoalItem() {}
    protected CharcoalItem(String[] in) {}
    public Item copy() {return new CharcoalItem();}
    
    public int stackSize() {return 8;}
    public String item() {return "item.charcoal";}
    public String entity() {return "item_entity.charcoal";}
    
    public int fuelAmount() {return 12;}
}
