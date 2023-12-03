package core;

import items.Item;
import entities.Entity;
import entities.ItemEntity;
import java.util.ArrayList;


public class Recipe {
    public final Item recipe[], output[];
    private boolean checked[];
    public Recipe(Item[] r, Item[] o) {
        this.recipe = r;
        this.output = o;
        this.checked = new boolean[this.recipe.length];
    }
    public void CRAFT(int x, int y) {
        for (Entity e : main.entities) if (e instanceof ItemEntity && Math.hypot(e.XY()[0] - x, e.XY()[1] - y) <= 24 + e.size()) {
            int E_001 = this.recipe.length;
            for (int i = 0; i < E_001; i++) {
                if (this.checked[i]) continue;
                ItemEntity i1 = (ItemEntity) e;
                if (this.recipe[i].getClass() == i1.item.getClass()) {
                    this.checked[i] = true;
                    break;
                }
            }
            boolean done = true;
            for (int i = 0; i < E_001; i++) if (!this.checked[i]) done = false;
            if (done) break;
        }
        boolean done = true;
        int E_001 = this.recipe.length;
        for (int i = 0; i < E_001; i++) if (!this.checked[i]) done = false;
        if (!done) {
            this.checked = new boolean[this.recipe.length];
            return;
        }
        for (int i = 0; i < E_001; i++) {
            this.consumeItem(x, y, this.recipe[i]);
        }
        E_001 = this.output.length;
        for (int i = 0; i < E_001; i++) {
            main.spawnIn.add(new ItemEntity(x, y, this.output[i].copy(), 2));
        }
        this.checked = new boolean[this.recipe.length];
        main.playSound("sfx_craft");
    }
    private void consumeItem(int x, int y, Item item) {
        ArrayList<ItemEntity> i = new ArrayList<ItemEntity>();
        for (Entity e : main.entities) {
            if (!(e instanceof ItemEntity) || e.delete()) continue;
            ItemEntity e1 = (ItemEntity) e;
            if (item.getClass() == e1.item.getClass()) i.add(e1);
        }
        if (i.size() <= 0) return;
        double d = -1;
        ItemEntity target = null;
        for (ItemEntity e : i) {
            if (d == -1 || Math.hypot(x - e.XY()[0], y - e.XY()[1]) < d) {
                d = Math.hypot(x - e.XY()[0], y - e.XY()[1]);
                target = e;
            }
        }
        if (target == null) return;
        target.BEGONE();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
