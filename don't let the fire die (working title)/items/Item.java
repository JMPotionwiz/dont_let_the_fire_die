package items;

import core.main;
import entities.Entity;
import entities.Player;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Item {
    protected boolean destroy = false;
    
    public Item() {}
    protected Item(String[] in) {}
    public void tick() {}
    public abstract Item copy();
    
    public String SAVE() {
        return this.getClass().getName() + ",";
    }
    public static final Item LOAD(String in) {
        String data[] = main.SPLIT_DATA(in);
        Item out = null;
        switch (data[0]) {
            case "items.LogItem" -> out = new LogItem(data);
            case "items.StoneAxeItem" -> out = new StoneAxeItem(data);
            case "items.UnlitTorchItem" -> out = new UnlitTorchItem(data);
            case "items.LitTorchItem" -> out = new LitTorchItem(data);
            case "items.CharcoalItem" -> out = new CharcoalItem(data);
            case "items.StoneItem" -> out = new StoneItem(data);
            case "items.TwigItem" -> out = new TwigItem(data);
            case "items.XfVenisonItem" -> out = new XfVenisonItem(data);
            case "items.XfSteakItem" -> out = new XfSteakItem(data);
            case "items.StoneSpearItem" -> out = new StoneSpearItem(data);
            case "items.ChestItem" -> out = new ChestItem(data);
            case "items.WoodenClubItem" -> out = new WoodenClubItem(data);
            
        }
        return out;
    }
    
    public int stackSize() {return 1;}
    public abstract String item();
    public abstract String entity();
    public int attack() {return 1;}
    public int cooldown() {return 4;}
    public int light() {return 0;}
    public int[] hp() {return new int[] {0,0};}
    public final boolean destroy() {return this.destroy;}
    public Item cooked() {return null;}
    public Entity[] attack(Player a) {
        ArrayList<Entity> list = new ArrayList<Entity>();
        for (Entity e : main.ticking) if (Math.hypot(a.XY()[0] - e.XY()[0], a.XY()[1] - e.XY()[1]) <= 8 + a.size() + e.size() && e != a)
        list.add(e.damage(a.attackDamage(), 2, a));
        a.attackAnim();
        if (this.listToOut(list).length <= 0) main.playSound("sfx_playerswing");
        
        return this.listToOut(list);
    }
    protected final Entity[] listToOut(ArrayList<Entity> list) {
        Iterator<Entity> IT = list.iterator();
        while(IT.hasNext()) if (IT.next() == null) IT.remove();
        Entity out[] = new Entity[list.size()];
        int E_001 = list.size();
        for (int i = 0; i < E_001; i++) out[i] = list.get(i);
        return out;
    }
}
