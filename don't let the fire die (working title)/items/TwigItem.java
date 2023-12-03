package items;

import core.main;

public class TwigItem extends Item implements CampfireFuel {
    public TwigItem() {}
    protected TwigItem(String[] in) {}
    public Item copy() {return new TwigItem();}
    
    public int stackSize() {return 10;}
    public String item() {return "item.twig";}
    public String entity() {return "item_entity.twig";}
    
    public int fuelAmount() {return 2;}
}
