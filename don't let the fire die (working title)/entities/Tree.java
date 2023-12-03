package entities;

import java.awt.Graphics2D;
import core.main;
import items.Item;
import items.LogItem;
import items.TwigItem;

public class Tree extends Entity {
    private final byte varient;
    private int waver = (int)(Math.random() * 360);
    
    public Tree(int x, int y) {
        super(x,y);
        this.hp = 64;
        this.maxHp = 64;
        this.dir[0] = Math.random() < 0.5;
        this.varient = (byte)(Math.random() * 2);
    }
    protected Tree(String[] in) {
        super(in);
        this.varient = Byte.parseByte(in[8]);
    }
    public void tick() {
        super.tick();
        if (this.hp > 0 && this.hp < 64 && Math.random() * 300 < 1) this.hp++;
        if (Math.random() < 0.2) if (++this.waver >= 360) this.waver = 0;
        if (this.hp <= 0 && this.hurtTime <= 1) {
            for (int i = (int)(Math.random() * 5) + 4; i > 0; i--) main.spawnIn.add(new ItemEntity(this.X(), this.Y(), new LogItem(), 2.5));
            for (int i = (int)(Math.random() * 3); i > 0; i--) main.spawnIn.add(new ItemEntity(this.X(), this.Y(), new TwigItem(), 2.5));
            this.BEGONE();
        }
    }
    public void render(Graphics2D g) {
        StringBuilder i = new StringBuilder("tree.");
        i.append(this.varient);
        if (this.hurtTime > 0) i.append("_hurt");
        if (!this.dir[0]) i.append("_flipped");
        
        g.drawImage(main.art.get(i.toString()), this.XY()[0] - 15 + (int)Math.round(Math.sin(Math.toRadians(this.waver))), this.XY()[1] - 30, null);
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.varient);
        i.append(',');
        return i.toString();
    }
    
    public double size() {return 4;}
    protected byte[] collision() {return new byte[] {1,0};}
    protected String hurtsound() {return "sfx_treehurt";}
}
