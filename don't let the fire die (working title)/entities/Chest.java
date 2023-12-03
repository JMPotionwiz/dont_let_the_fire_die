package entities;

import java.awt.Graphics2D;
import core.main;
import items.ItemSlot;
import items.Item;
import items.ChestItem;

public class Chest extends Entity implements Interactable, HasInventory {
    private ItemSlot items[] = {new ItemSlot(),new ItemSlot(),new ItemSlot(),new ItemSlot(),new ItemSlot(),new ItemSlot(),new ItemSlot(),new ItemSlot(),new ItemSlot()};
    
    public Chest(int x, int y) {
        super(x,y);
        this.hp = 8;
        this.maxHp = 8;
        this.dir[0] = Math.random() < 0.5;
    }
    protected Chest(String[] in) {
        super(in);
        int i = 8;
        
        this.items[0].item = Item.LOAD(in[i++]);
        this.items[1].item = Item.LOAD(in[i++]);
        this.items[2].item = Item.LOAD(in[i++]);
        this.items[3].item = Item.LOAD(in[i++]);
        this.items[4].item = Item.LOAD(in[i++]);
        this.items[5].item = Item.LOAD(in[i++]);
        this.items[6].item = Item.LOAD(in[i++]);
        this.items[7].item = Item.LOAD(in[i++]);
        this.items[8].item = Item.LOAD(in[i++]);
        this.items[0].count = Integer.parseInt(in[i++]);
        this.items[1].count = Integer.parseInt(in[i++]);
        this.items[2].count = Integer.parseInt(in[i++]);
        this.items[3].count = Integer.parseInt(in[i++]);
        this.items[4].count = Integer.parseInt(in[i++]);
        this.items[5].count = Integer.parseInt(in[i++]);
        this.items[6].count = Integer.parseInt(in[i++]);
        this.items[7].count = Integer.parseInt(in[i++]);
        this.items[8].count = Integer.parseInt(in[i++]);
        
    }
    public void tick() {
        super.tick();
        if (this.hp > 0 && this.hp < this.maxHp && Math.random() * 300 < 1) this.hp++;
        if (this.hp <= 0 && this.hurtTime <= 1) {
            main.spawnIn.add(new ItemEntity(this.X(), this.Y(), new ChestItem(), 2.5));
            
            this.BEGONE();
        }
        
        int E_001 = this.items.length;
        for (int i = 0; i < E_001; i++) if (this.getItem(i) instanceof items.Ticking) {
            this.items[i].item.tick();
            if (this.items[i].item.destroy()) this.items[i] = new ItemSlot();
        }
        
        if (this.hp <= 0 && this.hurtTime <= 1) for (int i = 0; i < E_001; i++) {
            while (this.getItem(i) != null) {
                main.spawnIn.add(new ItemEntity(this.X(), this.Y(), this.getItem(i), 4));
                this.addItemCount(-1, i);
            }
        }
    }
    public void render(Graphics2D g) {
        StringBuilder i = new StringBuilder("chest.");
        
        if (main.playerAccess != null && main.playerAccess.accessing == this) {
            i.append("opened");
        } else i.append("closed");
        
        if (this.hurtTime > 0) i.append("_hurt");
        if (!this.dir[0]) i.append("_flipped");
        
        g.drawImage(main.art.get(i.toString()), this.XY()[0] - 5, this.XY()[1] - 9, null);
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append('[');
        if (this.items[0].item != null) {i.append(this.items[0].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[1].item != null) {i.append(this.items[1].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[2].item != null) {i.append(this.items[2].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[3].item != null) {i.append(this.items[3].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[4].item != null) {i.append(this.items[4].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[5].item != null) {i.append(this.items[5].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[6].item != null) {i.append(this.items[6].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[7].item != null) {i.append(this.items[7].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[8].item != null) {i.append(this.items[8].item.SAVE());} else i.append("empty,");
        i.append("],");
        i.append(this.items[0].count);
        i.append(',');
        i.append(this.items[1].count);
        i.append(',');
        i.append(this.items[2].count);
        i.append(',');
        i.append(this.items[3].count);
        i.append(',');
        i.append(this.items[4].count);
        i.append(',');
        i.append(this.items[5].count);
        i.append(',');
        i.append(this.items[6].count);
        i.append(',');
        i.append(this.items[7].count);
        i.append(',');
        i.append(this.items[8].count);
        i.append(',');
        
        return i.toString();
    }
    
    public void repairItemCount(int slot) {
        if (slot >= items.length || slot < 0) return;
        if (this.items[slot].item == null) {
            this.items[slot].count = 0;
        } else if (this.items[slot].count > this.items[slot].item.stackSize()) {
            this.items[slot].count = this.items[slot].item.stackSize();
        } else if (this.items[slot].count < 1) this.items[slot] = new ItemSlot();
    }
    public Item getItem(int slot) {
        if (slot >= items.length || slot < 0) return null;
        return this.items[slot].item;
    }
    public int getItemCount(int slot) {
        if (slot >= items.length || slot < 0) return -1;
        return this.items[slot].count;
    }
    public void setItem(Item i, int c, int slot) {
        if (slot >= items.length || slot < 0) return;
        this.items[slot] = new ItemSlot(i, c);
    }
    public void setItemCount(int c, int slot) {
        if (slot >= items.length || slot < 0) return;
        this.items[slot].count = c;
    }
    public int totalSlots() {return items.length;}
    public void addItemCount(int c, int slot) {
        if (slot >= items.length || slot < 0) return;
        this.items[slot].count += c;
        this.repairItemCount(slot);
    }
    
    public double size() {return 5;}
    protected byte[] collision() {return new byte[] {2,0};}
    protected String hurtsound() {return "sfx_treehurt";}
}
