package items;

import core.main;
import entities.Entity;
import entities.Player;

public class StoneAxeItem extends Item implements StrongAgainstTrees, Toolbeltable {
    private int hp = 64;
    public StoneAxeItem() {}
    protected StoneAxeItem(String[] in) {
        this.hp = Integer.parseInt(in[1]);
    }
    public Item copy() {return new StoneAxeItem();}
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.hp);
        i.append(',');
        return i.toString();
    }
    public Entity[] attack(Player a) {
        Entity out[] = super.attack(a);
        int E_001 = out.length;
        for (int i = 0; i < E_001; i++) if (Math.random() < 0.5) this.hp--;
        if (this.hp <= 0) {
            this.hp = 64;
            a.addItemCount(-1, 0);
        }
        return out;
    }
    public int stackSize() {return 1;}
    public String item() {return "item.stone_axe";}
    public String entity() {return "item_entity.stone_axe";}
    public int attack() {return 2;}
    public int cooldown() {return 16;}
    public int[] hp() {return new int[] {this.hp,64};}
}
