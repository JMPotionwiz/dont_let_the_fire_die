package items;

import core.main;

public class LitTorchItem extends Item implements Ticking {
    private int hp = 3600;
    
    public LitTorchItem() {}
    protected LitTorchItem(String[] in) {
        this.hp = Integer.parseInt(in[1]);
    }
    public Item copy() {return new LitTorchItem();}
    
    public void tick() {
        if (this.hp <= 0) this.destroy = true;
        if (Math.random() < 0.75) this.hp--;
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.hp);
        i.append(',');
        return i.toString();
    }
    public int stackSize() {return 1;}
    public String item() {return "item.lit_torch";}
    public String entity() {return "item_entity.lit_torch";}
    public int attack() {return 2;}
    public int light() {return 24;}
    public int[] hp() {return new int[] {this.hp,3600};}
}
