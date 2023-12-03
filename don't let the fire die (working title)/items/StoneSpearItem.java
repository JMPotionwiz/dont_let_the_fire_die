package items;

import core.main;
import entities.Entity;
import entities.Player;
import entities.Monster;
import entities.ItemEntity;
import java.util.ArrayList;

public class StoneSpearItem extends Item implements Toolbeltable {
    private int hp = 5;
    public StoneSpearItem() {}
    protected StoneSpearItem(String[] in) {
        this.hp = Integer.parseInt(in[1]);
    }
    public Item copy() {return new StoneSpearItem();}
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.hp);
        i.append(',');
        return i.toString();
    }
    public Entity[] attack(Player a) {
        ArrayList<Entity> t = new ArrayList<Entity>();
        for (Entity e : main.ticking) if (e instanceof Monster && Math.hypot(a.XY()[0] - e.XY()[0], a.XY()[1] - e.XY()[1]) <= 48 + a.size() + e.size() && e.hp()[0] > 0) t.add(e);
        if (t.size() <= 0) {
            a.attackAnim();
            main.playSound("sfx_playerswing");
            main.spawnIn.add(new ItemEntity(a.X(), a.Y(), this, 8));
            a.addItemCount(-1, 0);
            return new Entity[] {};
        }
        double d = -1;
        Entity target = null;
        for (Entity e : t) if (target == null || Math.hypot(a.XY()[0] - e.XY()[0], a.XY()[1] - e.XY()[1]) < d) {
            d = Math.hypot(a.XY()[0] - e.XY()[0], a.XY()[1] - e.XY()[1]);
            target = e;
        }
        if (target == null) {
            a.attackAnim();
            main.playSound("sfx_playerswing");
            main.spawnIn.add(new ItemEntity(a.X(), a.Y(), this, 8));
            a.addItemCount(-1, 0);
            return new Entity[] {};
        }
        target.damage(a.attackDamage(), 5, a);
        if (Math.random() < 0.5) this.hp--;
        if (this.hp <= 0) {
            this.hp = 10;
        } else {
            main.spawnIn.add(new ItemEntity(target.X(), target.Y(), this, 2.5));
        }
        a.addItemCount(-1, 0);
        return new Entity[] {target};
    }
    public int stackSize() {return 1;}
    public String item() {return "item.stone_spear";}
    public String entity() {return "item_entity.stone_spear";}
    public int attack() {return 7;}
    public int cooldown() {return 32;}
    public int[] hp() {return new int[] {this.hp,5};}
}
