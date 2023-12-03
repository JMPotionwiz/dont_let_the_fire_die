package items;

import core.main;

public class UnlitTorchItem extends Item {
    public UnlitTorchItem() {}
    protected UnlitTorchItem(String[] in) {}
    public Item copy() {return new UnlitTorchItem();}
    
    public int stackSize() {return 1;}
    public String item() {return "item.unlit_torch";}
    public String entity() {return "item_entity.unlit_torch";}
}
