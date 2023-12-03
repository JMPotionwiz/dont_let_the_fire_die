package items;

import core.main;

public class LogItem extends Item implements CampfireFuel {
    public LogItem() {}
    protected LogItem(String[] in) {}
    
    public Item copy() {return new LogItem();}
    
    public int stackSize() {return 4;}
    public String item() {return "item.log";}
    public String entity() {return "item_entity.log";}
    
    public Item cooked() {return Math.random() < 0.75 ? new CharcoalItem() : null;}
    public int fuelAmount() {return 6;}
}
