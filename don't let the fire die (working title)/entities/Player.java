package entities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.HashMap;
import core.main;
import core.LightSource;
import core.Recipe;
import java.awt.Image;
import items.*;
import java.util.ArrayList;

public class Player extends LivingEntity implements HasInventory, AlwaysTick {
    private boolean[] lastWS = {false,false}, lastAD = {false,false}, itemMoved = {false,false,false}, itemTransfered = new boolean[9];
    private HashMap<String, BufferedImage> art;
    private boolean attackDir = false, attemptInteract = false;
    private int sinceLastAttack = 180, colors[] = new int[6], darknessTime = 0, food = 32, foodtimer = 0, foodheal = 0;
    private LightSource light = null;
    private ItemSlot items[] = {new ItemSlot(),new ItemSlot(),new ItemSlot()}; // Main-hand, Off-hand, Belt.
    private ArrayList<Recipe> crafts = null;
    private int craftingPoint[] = {0,0}, craftsIndex = 0;
    private boolean mustBeNearCraftingPoint = false, crafting[] = {false,false,false,false};
    public boolean destroyCurrentWeapon = false;
    private byte hair, hurtsound = 1;
    public Entity accessing = null;
    
    protected double baseSpeed = 0.3;
    
    public Player(int x, int y, byte hair, byte hurt, Color[] colors) {
        super(x,y);
        this.hp = 16;
        this.maxHp = 16;
        //this.baseSpeed = 0.3;
        main.playerAccess = this;
        this.hurtsound = hurt;
        this.generateAssets(hair, colors);
    }
    protected Player(String[] in) {
        super(in);
        int i = 18;
        Color c[] = new Color[6];
        
        this.food = Integer.parseInt(in[i++]);
        this.foodtimer = Integer.parseInt(in[i++]);
        this.foodheal = Integer.parseInt(in[i++]);
        this.hair = Byte.parseByte(in[i++]);
        this.hurtsound = Byte.parseByte(in[i++]);
        c[0] = new Color(Integer.parseInt(in[i++]), true);
        c[1] = new Color(Integer.parseInt(in[i++]), true);
        c[2] = new Color(Integer.parseInt(in[i++]), true);
        c[3] = new Color(Integer.parseInt(in[i++]), true);
        c[4] = new Color(Integer.parseInt(in[i++]), true);
        c[5] = new Color(Integer.parseInt(in[i++]), true);
        this.items[0].item = Item.LOAD(in[i++]);
        this.items[1].item = Item.LOAD(in[i++]);
        this.items[2].item = Item.LOAD(in[i++]);
        this.items[0].count = Integer.parseInt(in[i++]);
        this.items[1].count = Integer.parseInt(in[i++]);
        this.items[2].count = Integer.parseInt(in[i++]);
        
        //this.baseSpeed = 0.4;
        main.playerAccess = this;
        this.generateAssets(this.hair, c);
    }
    public void tick() {
        super.tick();
        main.cam.x = this.X();
        main.cam.y = this.Y();
        this.recalculateReletivePos();
        
        if (this.hp > 0) if (this.foodtimer > 600) {
            this.foodheal = Math.max(Math.min(this.foodheal, this.maxHp - this.hp), 0);
            if (this.food > 0 && this.foodheal > 0) {
                this.hp = Math.min(this.hp + 1, this.maxHp);
                this.food--;
                this.foodheal--;
            } else if (this.food > 0 && Math.random() < 0.5) {
                this.hp = Math.min(this.hp + 1, this.maxHp);
                this.food--;
            } else if (this.food > 0) {
                this.food--;
            } else if (this.hp > 1) {
                this.hp--;
            } else if (this.hp == 1) this.damage(1, 1, null);
            this.foodtimer = 0;
        } else {
            if (Math.random() < 0.5) this.foodtimer++;
            if (this.hp < this.maxHp && this.food > 0 && Math.random() < 0.5) this.foodtimer++;
        }
        
        if ((this.crafting() && this.mustBeNearCraftingPoint && main.distanceFromEntity(this.craftingPoint[0], this.craftingPoint[1], this) > 24 + this.size()) || this.hp <= 0)
        this.crafts = null;
        if ((this.accessing != null && main.distanceFromEntity(this.X(), this.Y(), this.accessing) > 24 + this.size() + this.accessing.size()) || this.hp <= 0)
        this.accessing = null;
        
        if (this.lightRadius() > 0 && this.light == null) {
            this.light = new LightSource(0,0,this.lightRadius(),this.ID(),3);
            main.lights.add(this.light);
        } else if (this.lightRadius() > 0 && this.light != null) {
            this.light.setRadius(this.lightRadius());
        } else if (this.light != null) {
            this.light.douse();
            this.light = null;
        }
        
        if ((!this.isInLight() || main.lights.size() <= 0) && this.hp > 0) {
            if (Math.random() < 0.5) this.darknessTime++;
            if (this.darknessTime >= 200) {
                this.damage(1 + (int)(Math.random() * 3), 2.5, null);
                this.darknessTime -= (int)(Math.random() * 30);
            }
        } else if (this.darknessTime > 0) this.darknessTime--;
        
        int E_001 = this.items.length;
        for (int i = 0; i < E_001; i++) if (this.getItem(i) instanceof items.Ticking) {
            this.items[i].item.tick();
            if (this.items[i].item.destroy()) this.items[i] = new ItemSlot();
        }
        
        if (this.hp <= 0 && this.hurtTime == 1) for (int i = 0; i < E_001; i++) {
            while (this.getItem(i) != null) {
                main.spawnIn.add(new ItemEntity(this.X(), this.Y(), this.getItem(i), 4));
                this.addItemCount(-1, i);
            }
        }
    }
    protected void control() {
        double[] move = {0,0};
        double speed = this.baseSpeed;
        
        if (this.attackCooldown > 0) {
            this.sinceLastAttack = 0;
        } else if (this.sinceLastAttack < 180) this.sinceLastAttack++;
        if (main.pressed(main.Key.ENTER) && this.attackTimer <= 0 && this.attackCooldown <= 0 && !this.busy()) {
            this.attackCooldown = attackCooldown();
            try {if (this.getItem(0) != null) {this.items[0].item.attack(this);} else this.attack();} catch (Exception e) {}
        }
        
        if (this.attackCooldown > 0) speed *= 0.75;
        
        if (main.pressed(main.Key.UP) && lastWS[0] == false && !this.attacking()) {
            dir[1] = false;
            lastWS[0] = true;
        } else if (!main.pressed(main.Key.UP)) lastWS[0] = false;
        if (main.pressed(main.Key.DOWN) && lastWS[1] == false && !this.attacking()) {
            dir[1] = true;
            lastWS[1] = true;
        } else if (!main.pressed(main.Key.DOWN)) lastWS[1] = false;
        if (main.pressed(main.Key.LEFT) && lastAD[0] == false && !this.attacking()) {
            dir[0] = false;
            lastAD[0] = true;
        } else if (!main.pressed(main.Key.LEFT)) lastAD[0] = false;
        if (main.pressed(main.Key.RIGHT) && lastAD[1] == false && !this.attacking()) {
            dir[0] = true;
            lastAD[1] = true;
        } else if (!main.pressed(main.Key.RIGHT)) lastAD[1] = false;
        
        if (main.pressed(main.Key.UP) && !dir[1] && !this.attacking()) move[1] -= 1;
        if (main.pressed(main.Key.DOWN) && dir[1] && !this.attacking()) move[1] += 1;
        if (main.pressed(main.Key.LEFT) && !dir[0] && !this.attacking()) move[0] -= 1;
        if (main.pressed(main.Key.RIGHT) && dir[0] && !this.attacking()) move[0] += 1;
        
        double d = Math.hypot(move[0], move[1]);
        if (d != 0) {
            move[0] /= d;
            move[1] /= d;
        }
        
        if (Math.abs(move[0]) + Math.abs(move[1]) > 0) {
            if (++this.moveAnim > 18) this.moveAnim = 1;
        } else this.moveAnim = 0;
        
        this.velocity[0] += move[0] * speed;
        this.velocity[1] += move[1] * speed;
        
        if (main.pressed(main.Key.CRAFT) && this.crafting[0] == false && !this.attacking() && !this.busy()) {
            this.startCrafting("Basic", this.XY()[0], this.XY()[1]);
            this.crafting[0] = true;
        } else if (main.pressed(main.Key.CRAFT) && this.crafting[0] == false && !this.attacking() && this.crafting()) {
            this.crafts = null;
            this.crafting[0] = true;
            this.craftsIndex = 0;
        } else if (!main.pressed(main.Key.CRAFT)) this.crafting[0] = false;
        
        if (main.pressed(main.Key.CRAFT_LEFT) && this.crafting[1] == false && this.crafting()) {
            if (--this.craftsIndex < 0) this.craftsIndex = this.crafts.size() - 1;
            main.mainWindow.craftingOffset[0] = -5;
            this.crafting[1] = true;
        } else if (!main.pressed(main.Key.CRAFT_LEFT)) this.crafting[1] = false;
        if (main.pressed(main.Key.CRAFT_RIGHT) && this.crafting[2] == false && this.crafting()) {
            if (++this.craftsIndex >= this.crafts.size()) this.craftsIndex = 0;
            main.mainWindow.craftingOffset[0] = 5;
            this.crafting[2] = true;
        } else if (!main.pressed(main.Key.CRAFT_RIGHT)) this.crafting[2] = false;
        if (main.pressed(main.Key.ENTER) && this.crafting[3] == false && this.crafting()) {
            this.currentCraft().CRAFT(this.XY()[0], this.XY()[1]);
            main.mainWindow.craftingOffset[1] = 5;
            this.crafting[3] = true;
        } else if (!main.pressed(main.Key.ENTER)) this.crafting[3] = false;
        
        if (main.pressed(main.Key.INTERACT) && !this.attemptInteract && !this.attacking()) {
            this.interact();
            this.attemptInteract = true;
        } else if (!main.pressed(main.Key.INTERACT)) this.attemptInteract = false;
        if (main.pressed(main.Key.DROP) && !this.itemMoved[0] && this.getItem(0) != null && !main.pressed(main.Key.DROP_ALL) && !this.attacking()) {
            main.spawnIn.add(new ItemEntity(this.X(), this.Y(), this.getItem(0), 1));
            this.addItemCount(-1, 0);
            main.playSound("sfx_dropitem");
            this.itemMoved[0] = true;
        } else if (main.pressed(main.Key.DROP) && !this.itemMoved[0] && this.getItem(0) != null && main.pressed(main.Key.DROP_ALL) && !this.attacking()) {
            do {
                main.spawnIn.add(new ItemEntity(this.X(), this.Y(), this.getItem(0), 1));
                this.addItemCount(-1, 0);
            } while (this.getItem(0) != null);
            main.playSound("sfx_dropitems");
            this.itemMoved[0] = true;
        } else if (!main.pressed(main.Key.DROP)) this.itemMoved[0] = false;
        if (main.pressed(main.Key.SWAP) && !this.itemMoved[1] && !this.attacking()) {
            if (this.items[0].item == null && this.items[1].item == null) {
                this.swapItems(0,2);
            } else this.swapItems(0,1);
            this.itemMoved[1] = true;
        } else if (!main.pressed(main.Key.SWAP)) this.itemMoved[1] = false;
        if (main.pressed(main.Key.TOOL) && !this.itemMoved[2] && !this.attacking() && (this.getItem(0) == null || this.getItem(0) instanceof items.Toolbeltable)) {
            this.swapItems(0,2);
            this.itemMoved[2] = true;
        } else if (!main.pressed(main.Key.TOOL)) this.itemMoved[2] = false;
        
        if (this.accessing() && main.pressed(main.Key.SLOT1) && !this.itemTransfered[0] && !this.attacking()) this.transferItems(0,0);
        if (this.accessing() && main.pressed(main.Key.SLOT2) && !this.itemTransfered[1] && !this.attacking()) this.transferItems(0,1);
        if (this.accessing() && main.pressed(main.Key.SLOT3) && !this.itemTransfered[2] && !this.attacking()) this.transferItems(0,2);
        if (this.accessing() && main.pressed(main.Key.SLOT4) && !this.itemTransfered[3] && !this.attacking()) this.transferItems(0,3);
        if (this.accessing() && main.pressed(main.Key.SLOT5) && !this.itemTransfered[4] && !this.attacking()) this.transferItems(0,4);
        if (this.accessing() && main.pressed(main.Key.SLOT6) && !this.itemTransfered[5] && !this.attacking()) this.transferItems(0,5);
        if (this.accessing() && main.pressed(main.Key.SLOT7) && !this.itemTransfered[6] && !this.attacking()) this.transferItems(0,6);
        if (this.accessing() && main.pressed(main.Key.SLOT8) && !this.itemTransfered[7] && !this.attacking()) this.transferItems(0,7);
        if (this.accessing() && main.pressed(main.Key.SLOT9) && !this.itemTransfered[8] && !this.attacking()) this.transferItems(0,8);
        this.itemTransfered[0] = main.pressed(main.Key.SLOT1);
        this.itemTransfered[1] = main.pressed(main.Key.SLOT2);
        this.itemTransfered[2] = main.pressed(main.Key.SLOT3);
        this.itemTransfered[3] = main.pressed(main.Key.SLOT4);
        this.itemTransfered[4] = main.pressed(main.Key.SLOT5);
        this.itemTransfered[5] = main.pressed(main.Key.SLOT6);
        this.itemTransfered[6] = main.pressed(main.Key.SLOT7);
        this.itemTransfered[7] = main.pressed(main.Key.SLOT8);
        this.itemTransfered[8] = main.pressed(main.Key.SLOT9);
        
    }
    public void render(Graphics2D g) {
        if (!this.visible) return;
        StringBuilder i = new StringBuilder("idle");
        if (this.hp()[0] <= 0) {
            i = new StringBuilder("dead");
        } else if (this.attackTimer > 0) {
            i = new StringBuilder("attack");
            i.append(this.attackDir ? 0 : 1);
            i.append('_');
            if (this.attackTimer > 8) {
                i.append(0);
            } else if (this.attackTimer > 4) {
                i.append(1);
            } else i.append(2);
        } else if (this.moveAnim > 0 && this.moveAnim <= 12) {
            i = new StringBuilder("walk_");
            i.append(this.moveAnim <= 6 ? 0 : 1);
        }
        if (this.hurtTime > 0) i.append("_hurt");
        if (!this.dir[0]) i.append("_flipped");
        
        g.drawImage(this.art.get(i.toString()), this.XY()[0] - 11, this.XY()[1] - 15, null);
        //g.drawString(this.XY()[0] + ", " + this.XY()[1], this.XY()[0] - 11, this.XY()[1] - 15);
        //g.drawString(main.WORLD_SIZE() + "", this.XY()[0] - 11, this.XY()[1] - 15);
        
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.food);
        i.append(',');
        i.append(this.foodtimer);
        i.append(',');
        i.append(this.foodheal);
        i.append(',');
        i.append(this.hair);
        i.append(',');
        i.append(this.hurtsound);
        i.append(',');
        i.append(this.colors[0]);
        i.append(',');
        i.append(this.colors[1]);
        i.append(',');
        i.append(this.colors[2]);
        i.append(',');
        i.append(this.colors[3]);
        i.append(',');
        i.append(this.colors[4]);
        i.append(',');
        i.append(this.colors[5]);
        i.append(",[");
        if (this.items[0].item != null) {i.append(this.items[0].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[1].item != null) {i.append(this.items[1].item.SAVE());} else i.append("empty,");
        i.append("],[");
        if (this.items[2].item != null) {i.append(this.items[2].item.SAVE());} else i.append("empty,");
        i.append("],");
        i.append(this.items[0].count);
        i.append(',');
        i.append(this.items[1].count);
        i.append(',');
        i.append(this.items[2].count);
        i.append(',');
        
        return i.toString();
    }
    
    public double size() {return 3;}
    protected byte[] collision() {return new byte[] {2,1};}
    protected String hurtsound() {
        if (this.hurtsound == 0) return "sfx_playerhurtlow";
        if (this.hurtsound == 2) return "sfx_playerhurthigh";
        return "sfx_playerhurtmid";
    }
    private boolean attacking() {return this.attackTimer > 0 || main.pressed(main.Key.ENTER);}
    public boolean crafting() {return this.crafts != null;}
    public boolean accessing() {return this.accessing != null;}
    public boolean busy() {return this.crafting() || this.accessing();}
    public Recipe currentCraft() {return this.crafting() ? this.crafts.get(this.craftsIndex) : null;}
    public void attack() {
        boolean hit = false;
        for (Entity e : main.ticking) if (Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) <= 8 + this.size() + e.size() && e != this)
        if (e.damage(this.attackDamage(), 2, this) != null) hit = true;
        this.attackAnim();
        if (!hit) main.playSound("sfx_playerswing");
    }
    public int attackDamage() {return this.getItem(0) != null ? this.getItem(0).attack() : 1;}
    public int attackCooldown() {return Math.max(12 + (this.getItem(0) != null ? this.getItem(0).cooldown() : 4), 12);}
    public void attackAnim() {
        this.attackTimer = 12;
        if (this.sinceLastAttack >= 180) {
            this.attackDir = false;
        } else this.attackDir = !this.attackDir;
    }
    private int lightRadius() {
        int r = 0;
        if (this.getItem(0) != null) r = Math.max(r, this.getItem(0).light());
        if (this.getItem(1) != null) r = Math.max(r, this.getItem(1).light());
        if (this.getItem(2) != null) r = Math.max(r, this.getItem(2).light());
        return r;
    }
    private boolean isInLight() {
        //if (main.lights.size() <= 0) return false;
        double d = -1;
        for (LightSource l : main.lights) if (d == -1) {
            d = main.distanceFromLight(this.X(), this.Y(), l, true);
        } else d = Math.min(d, main.distanceFromLight(this.X(), this.Y(), l, true));
        return d <= 0;
    }
    public int food() {return this.food;}
    public void feed(int amount, int heal) {
        this.food = Math.min(Math.max(this.food + amount, 0), 32);
        if (heal > 0) this.foodheal += Math.max(heal - this.foodheal, 1);
        this.foodtimer = 0;
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
        this.repairItemCount(slot);
    }
    public int totalSlots() {return items.length;}
    public void addItemCount(int c, int slot) {
        if (slot >= items.length || slot < 0) return;
        this.items[slot].count += c;
        this.repairItemCount(slot);
    }
    public void swapItems(int slot1, int slot2) {
        if (slot1 >= items.length || slot1 < 0) return;
        if (slot2 >= items.length || slot2 < 0) return;
        ItemSlot tmp = new ItemSlot(this.items[slot1].item, this.items[slot1].count);
        this.items[slot1] = new ItemSlot(this.items[slot2].item, this.items[slot2].count);
        this.items[slot2] = new ItemSlot(tmp.item, tmp.count);
    }
    public void transferItems(int slot1, int slot2) {
        if (this.accessing == null || !(this.accessing instanceof HasInventory)) return;
        HasInventory e = (HasInventory) this.accessing;
        if (slot1 >= items.length || slot1 < 0) return;
        if (slot2 >= e.totalSlots() || slot2 < 0) return;
        ItemSlot tmp = new ItemSlot(this.items[slot1].item, this.items[slot1].count);
        this.items[slot1] = new ItemSlot(e.getItem(slot2), e.getItemCount(slot2));
        e.setItem(tmp.item, tmp.count, slot2);
    }
    
    private void pickupItem() {
        if (this.getItem(0) != null) if (this.getItemCount(0) >= this.getItem(0).stackSize()) return;
        ArrayList<ItemEntity> i = new ArrayList<ItemEntity>();
        for (Entity e : main.ticking) {
            if (!(e instanceof ItemEntity)) continue;
            ItemEntity e1 = (ItemEntity) e;
            if (Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) <= 8 + this.size()) if (this.getItem(0) == null) {
                i.add(e1);
            } else if (this.getItem(0).getClass() == e1.item.getClass()) i.add(e1);
        }
        if (i.size() <= 0) return;
        double d = -1;
        ItemEntity target = null;
        for (ItemEntity e : i) {
            if (d == -1 || Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) < d) {
                d = Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]);
                target = e;
            }
        }
        if (target == null) return;
        if (this.getItem(0) == null) {
            this.setItem(target.item, 1, 0);
        } else this.addItemCount(1, 0);
        main.playSound("sfx_pickupitem");
        target.BEGONE();
    }
    
    private void interact() {
        if (this.crafting()) {
            this.crafts = null;
            this.mustBeNearCraftingPoint = false;
            return;
        } else if (this.accessing()) {
            this.accessing = null;
            return;
        }
        Entity target = null;
        double d = 0;
        for (Entity e : main.ticking)
        if (Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) <= 8 + this.size() + e.size() && e instanceof Interactable && e != this) {
            if (target == null || Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) - e.size() < d) {
                target = e;
                d = Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) - e.size();
            }
        }
        if (target == null) return;// by getting of your butt and throwing some logs in the fire. i mean really, its not rocket science. chop some wood and set it in the fire. heck, if you dont want to use wood, just use that lasanga you made. no one is going to eat it anyway and you know? because its disgusting, thats why 
        /*
            The above comment was writen by a friend of mine, when I asked them to implement the ability to fuel campfires. I am keeping it in it's original form,
            without spelling or grammer correction, for as long as this method lasts!
            
            Thank you Kino.
            
            P.S. Also, yes, I am adding lasangas because of this comment, and yes, you can use them as fuel.
        */
        if (target instanceof Campfire) {
            Campfire t = (Campfire) target;
            if (this.getItem(0) instanceof items.CampfireFuel) {
                if (!t.canRefuel()) return;
                CampfireFuel f = (CampfireFuel) this.getItem(0);
                t.fuel(f.fuelAmount());
                this.addItemCount(-1, 0);
            } else if (this.getItem(0) instanceof items.UnlitTorchItem && !t.canRelight()) {
                this.setItem(new items.LitTorchItem(), 1, 0);
            } else if (this.getItem(0) instanceof items.LitTorchItem && t.canRelight()) {
                t.relight();
                this.items[0] = new ItemSlot();
            } else {
                this.accessing = t;
                main.mainWindow.craftingOffset[1] = 8;
            }
        } else if (target instanceof ItemEntity) {
            this.pickupItem();
        } else if (target instanceof Chest) {
            this.accessing = target;
            main.mainWindow.craftingOffset[1] = 8;
        }
    }
    
    private void startCrafting(String cat, int x, int y) {
        this.crafts = main.crafts.get(cat);
        this.craftingPoint[0] = x;
        this.craftingPoint[1] = y;
        this.craftsIndex = 0;
        this.mustBeNearCraftingPoint = true;
        main.mainWindow.craftingOffset[1] = 8;
    }
    
    
    
    
    
    
    
    public void generateAssets(Byte h, Color[] c) {
        final float shade = 0.6f;
        Color face, hair, shirt, gloves, pants, shoes;
        
        try {face = c[0];} catch (Exception e) {face = new Color(242,218,186);}
        try {hair = c[1];} catch (Exception e) {hair = new Color(94,40,0);}
        try {shirt = c[2];} catch (Exception e) {shirt = new Color(255,0,0);}
        try {gloves = c[3];} catch (Exception e) {gloves = new Color(242,218,186);}
        try {pants = c[4];} catch (Exception e) {pants = new Color(9,85,168);}
        try {shoes = c[5];} catch (Exception e) {shoes = new Color(127,127,127);}
        
        this.hair = h;
        this.colors[0] = face.getRGB();
        this.colors[1] = hair.getRGB();
        this.colors[2] = shirt.getRGB();
        this.colors[3] = gloves.getRGB();
        this.colors[4] = pants.getRGB();
        this.colors[5] = shoes.getRGB();
        
        BufferedImage frame = null;
        Graphics2D g = null;
        art = new HashMap<String, BufferedImage>();
        art.clear();
        for (String n : main.playerFrames) {
            frame = new BufferedImage(24, 16, BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage(main.art.get("player" + h + "." + n), 0, 0, null);
            g.dispose();
            for (int y = 0; y < 16; y++) for (int x = 0; x < 24; x++) {
                Color c1 = new Color(frame.getRGB(x,y), true);
                if ((int)c1.getAlpha() <= 0) continue;
                
                byte bin = 0b000;
                if (c1.getRed() > 0) bin += 0b100;
                if (c1.getGreen() > 0) bin += 0b010;
                if (c1.getBlue() > 0) bin += 0b001;
                float red, green, blue;
                Color tmp;
                
                switch (bin) {
                    case 0b111:
                    case 0b000:
                        continue;
                    case 0b011:
                        red = (float)face.getRed() / 255;
                        green = (float)face.getGreen() / 255;
                        blue = (float)face.getBlue() / 255;
                        if (c1.getGreen() <= 127) {
                            red *= shade;
                            green *= shade;
                            blue *= shade;
                        }
                        tmp = new Color(red, green, blue);
                        frame.setRGB(x, y, tmp.getRGB());
                        break;
                    case 0b110:
                        red = (float)hair.getRed() / 255;
                        green = (float)hair.getGreen() / 255;
                        blue = (float)hair.getBlue() / 255;
                        if (c1.getRed() <= 127) {
                            red *= shade;
                            green *= shade;
                            blue *= shade;
                        }
                        tmp = new Color(red, green, blue);
                        frame.setRGB(x, y, tmp.getRGB());
                        break;
                    case 0b100:
                        red = (float)shirt.getRed() / 255;
                        green = (float)shirt.getGreen() / 255;
                        blue = (float)shirt.getBlue() / 255;
                        if (c1.getRed() <= 127) {
                            red *= shade;
                            green *= shade;
                            blue *= shade;
                        }
                        tmp = new Color(red, green, blue);
                        frame.setRGB(x, y, tmp.getRGB());
                        break;
                    case 0b010:
                        red = (float)gloves.getRed() / 255;
                        green = (float)gloves.getGreen() / 255;
                        blue = (float)gloves.getBlue() / 255;
                        if (c1.getGreen() <= 127) {
                            red *= shade;
                            green *= shade;
                            blue *= shade;
                        }
                        tmp = new Color(red, green, blue);
                        frame.setRGB(x, y, tmp.getRGB());
                        break;
                    case 0b001:
                        red = (float)pants.getRed() / 255;
                        green = (float)pants.getGreen() / 255;
                        blue = (float)pants.getBlue() / 255;
                        if (c1.getBlue() <= 127) {
                            red *= shade;
                            green *= shade;
                            blue *= shade;
                        }
                        tmp = new Color(red, green, blue);
                        frame.setRGB(x, y, tmp.getRGB());
                        break;
                    case 0b101:
                        red = (float)shoes.getRed() / 255;
                        green = (float)shoes.getGreen() / 255;
                        blue = (float)shoes.getBlue() / 255;
                        if (c1.getBlue() <= 127) {
                            red *= shade;
                            green *= shade;
                            blue *= shade;
                        }
                        tmp = new Color(red, green, blue);
                        frame.setRGB(x, y, tmp.getRGB());
                        break;
                }
            }
            art.put(n, frame);
            
            frame = new BufferedImage(24, 16, BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage(main.art.get("player" + h + "." + n), 0, 0, null);
            g.dispose();
            for (int y = 0; y < 16; y++) for (int x = 0; x < 24; x++) {
                Color c1 = new Color(frame.getRGB(x,y), true);
                if ((int)c1.getAlpha() <= 0) continue;
                c1 = new Color(1f, 1f, 1f);
                frame.setRGB(x, y, c1.getRGB());
            }
            art.put(n + "_hurt", frame);
        }
        
        for (String n : main.playerFrames) {
            frame = new BufferedImage(24, 16, BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.translate(24,0);
            g.scale(-1,1);
            g.drawImage(this.art.get(n), 0, 0, null);
            g.dispose();
            art.put(n + "_flipped", frame);
            
            frame = new BufferedImage(24, 16, BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.translate(24,0);
            g.scale(-1,1);
            g.drawImage(this.art.get(n), 0, 0, null);
            g.dispose();
            for (int y = 0; y < 16; y++) for (int x = 0; x < 24; x++) {
                Color c1 = new Color(frame.getRGB(x,y), true);
                if ((int)c1.getAlpha() <= 0) continue;
                c1 = new Color(1f, 1f, 1f);
                frame.setRGB(x, y, c1.getRGB());
            }
            art.put(n + "_hurt_flipped", frame);
        }
    }
    private Image get(String s) {return (Image)art.get(s);}
}
