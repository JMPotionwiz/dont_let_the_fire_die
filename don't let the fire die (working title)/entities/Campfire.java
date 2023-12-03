package entities;

import java.awt.Graphics2D;
import core.main;
import core.LightSource;
import items.ItemSlot;
import items.Item;

public class Campfire extends Entity implements PlayerNonAttackable, Interactable, AlwaysTick, HasInventory {
    private LightSource fire;
    private byte anim = 0;
    private int cooking = 0;
    private ItemSlot items[] = {new ItemSlot()};
    
    public Campfire(int x, int y) {
        super(x,y);
        this.hp = 96;
        this.maxHp = 128;
        fire = new LightSource(x, y, this.radius(), this.ID(), 5);
        main.lights.add(fire);
        main.campfire = this;
    }
    protected Campfire(String[] in) {
        super(in);
        int i = 8;
        
        this.cooking = Integer.parseInt(in[i++]);
        this.items[0].item = Item.LOAD(in[i++]);
        this.items[0].count = Integer.parseInt(in[i++]);
        
        fire = new LightSource(this.X(), this.Y(), this.radius(), this.ID(), 5);
        main.lights.add(fire);
        main.campfire = this;
    }
    public void tick() {
        super.tick();
        if (this.hp > 0 && Math.random() * 75 < 0.25) this.hp--;
        if (this.fire != null) this.fire.setRadius(this.radius());
        if (++this.anim >= 21) this.anim = 0;
        
        if ((getItem(0) != null || getItem(1) != null || getItem(2) != null) && this.hp > 0) {
            if (Math.random() < 0.5) this.cooking++;
            if (this.cooking >= 600) {
                int slot = 0;
                while (this.getItem(slot) == null && slot < 3) slot++;
                this.cookItem(slot);
                this.cooking = (int)(Math.random() * 100);
            }
        } else if (this.cooking > 0) this.cooking--;
        
        int E_001 = this.items.length;
        for (int i = 0; i < E_001; i++) if (this.getItem(i) instanceof items.Ticking) {
            this.items[i].item.tick();
            if (this.items[i].item.destroy()) this.items[i] = new ItemSlot();
        }
        
    }
    public void render(Graphics2D g) {
        StringBuilder i = new StringBuilder("campfire.");
        if (this.hp <= 0) {
            i.append("out");
        } else {
            i.append(this.anim / 3);
        }
        if (this.hurtTime > 0) i.append("_hurt");
        
        g.drawImage(main.art.get(i.toString()), this.XY()[0] - 7, this.XY()[1] - 28, null);
        //g.drawString(this.hp + "", this.XY()[0] - 7, this.XY()[1] + 12);
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.cooking);
        i.append(",[");
        if (this.items[0].item != null) {i.append(this.items[0].item.SAVE());} else i.append("empty,");
        i.append("],");
        i.append(this.items[0].count);
        i.append(',');
        
        return i.toString();
    }
    
    public void fuel(int amount) {
        if (this.hp <= 0) return;
        this.hp += amount;
    }
    public void relight() {
        if (this.hp > 0) return;
        this.hp = 32;
        this.fire = new LightSource(0, 0, this.radius(), this.ID(), 5);
        main.lights.add(this.fire);
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
    private void cookItem(int slot) {
        if (slot >= items.length || slot < 0) return;
        Item output = this.items[slot].item.cooked();
        if (output != null) main.spawnIn.add(new ItemEntity(this.X(), this.Y(), output, 3.5));
        this.addItemCount(-1, slot);
    }
    
    public double size() {return 8;}
    protected byte[] collision() {return new byte[] {10,0};}
    protected String hurtsound() {return "sfx_treehurt";}
    private int radius() {return this.hp > 0 ? (int)Math.round(16 + ((double)Math.min(this.hp, 128) * 0.25)) : 0;} //hp = 128 : r = 48
    public boolean canRefuel() {return this.hp > 0 && this.hp < this.maxHp;}
    public boolean canRelight() {return this.hp <= 0;}
}
