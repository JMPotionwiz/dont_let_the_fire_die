package entities;

import java.awt.Graphics2D;
import core.main;
import java.util.ArrayList;

public abstract class Entity {
    protected double pos[];
    protected int hp = 0, maxHp = 0;
    protected byte hurtTime = 0;
    protected boolean dir[] = {true,true};
    private boolean delete = false;
    private int reletivePos[];
    private byte hurtsoundCooldown = 0;
    
    private final long ID;
    
    public Entity(int x, int y) {
        this.pos = new double[] {x, y};
        this.reletivePos = new int[] {x, y};
        this.recalculateReletivePos();
        this.ID = main.id++;
    }
    protected Entity(String[] in) {
        this.ID = Long.parseLong(in[1]);
        this.pos = new double[2];
        this.pos[0] = Double.parseDouble(in[2]);
        this.pos[1] = Double.parseDouble(in[3]);
        this.hp = Integer.parseInt(in[4]);
        this.maxHp = Integer.parseInt(in[5]);
        this.dir[0] = Boolean.parseBoolean(in[6]);
        this.dir[1] = Boolean.parseBoolean(in[7]);
        this.recalculateReletivePos();
    }
    public void pretick() {
        if (this.hurtTime > 0) this.hurtTime--;
        if (this.hurtsoundCooldown > 0) this.hurtsoundCooldown--;
    }
    public void tick() {
        
    }
    public abstract void render(Graphics2D g);
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(this.getClass().getName());
        i.append(',');
        i.append(this.ID());
        i.append(',');
        i.append(Math.round(this.pos[0] * 1000) / 1000);
        i.append(',');
        i.append(Math.round(this.pos[1] * 1000) / 1000);
        i.append(',');
        i.append(this.hp);
        i.append(',');
        i.append(this.maxHp);
        i.append(',');
        i.append(this.dir[0]);
        i.append(',');
        i.append(this.dir[1]);
        i.append(',');
        return i.toString();
    }
    public static final void LOAD(String in) {
        String data[] = main.SPLIT_DATA(in);
        switch (data[0]) {
            case "entities.Player" -> main.entities.add(new Player(data));
            case "entities.Tree" -> main.entities.add(new Tree(data));
            case "entities.Campfire" -> main.entities.add(new Campfire(data));
            case "entities.ItemEntity" -> main.entities.add(new ItemEntity(data));
            case "entities.Xformidilosa" -> main.entities.add(new Xformidilosa(data));
            case "entities.Chest" -> main.entities.add(new Chest(data));
            case "entities.Svirosum" -> main.entities.add(new Svirosum(data));
            
        }
    }
    
    public long ID() {return this.ID;}
    public int X() {return (int)(Math.round(this.pos[0]));}
    public int Y() {return (int)(Math.round(this.pos[1]));}
    public int[] hp() {return new int[] {this.hp,this.maxHp};}
    public double size() {return 0;}
    protected byte[] collision() {return new byte[] {0,0};}
    public final int distanceUntilUnrendered() {return (int)this.size() + 128;}
    public final int[] XY() {return this.reletivePos != null ? this.reletivePos : new int[] {0,0};}
    protected String hurtsound() {return "";}
    protected void whenDamaged(Entity e) {}
    public final Entity damage(int d, double k, Entity e) {
        if (this instanceof NonAttackable || (this instanceof PlayerNonAttackable && e instanceof Player) || this.hp <= 0) return null;
        if (this instanceof Tree && e instanceof Player) {
            Player p = (Player) e;
            if (p.getItem(0) instanceof items.StrongAgainstTrees) d *= 4;
        }
        this.hurtTime = 4;
        this.hp = Math.max(this.hp - d, 0);
        if (k > 0 && this instanceof MovingEntity) if (e != null) {
            double d1 = Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]);
            double x = this.XY()[0] - e.XY()[0], y = this.XY()[1] - e.XY()[1];
            if (d1 != 0) {
                x /= d1;
                y /= d1;
            }
            MovingEntity m = (MovingEntity) this;
            m.velocity[0] += x * k;
            m.velocity[1] += y * k;
        } else {
            double r = Math.toRadians((int)(Math.random() * 360));
            double x = Math.cos(r), y = Math.sin(r);
            MovingEntity m = (MovingEntity) this;
            m.velocity[0] += x * k;
            m.velocity[1] += y * k;
        }
        if (this.hurtsoundCooldown <= 0) {
            main.playSound(this.hurtsound());
            this.hurtsoundCooldown = 10;
        }
        this.whenDamaged(e);
        return this;
    }
    public final void BEGONE() {this.delete = true;}
    public final boolean delete() {return this.delete;}
    public final void recalculateReletivePos() {
        for (int y = this.Y() - main.WORLD_SIZE(); y <= this.Y() + main.WORLD_SIZE(); y += main.WORLD_SIZE())
        for (int x = this.X() - main.WORLD_SIZE(); x <= this.X() + main.WORLD_SIZE(); x += main.WORLD_SIZE()) {
            if (Math.hypot(x - main.cam.x, y - main.cam.y) == main.distanceFromEntity(main.cam.x, main.cam.y, this)) {
                reletivePos = new int[] {x,y};
                return;
            }
        }
    }
    
    public static Entity getByID(long i) {
        for (Entity e : main.entities) if (e.ID() == i) return e;
        for (Entity e : main.spawnIn) if (e.ID() == i) return e;
        return null;
    }
    
}
