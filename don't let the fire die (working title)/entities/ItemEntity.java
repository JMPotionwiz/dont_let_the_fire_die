package entities;

import java.awt.Graphics2D;
import core.main;
import items.Item;
import core.LightSource;

public class ItemEntity extends MovingEntity implements NonAttackable, Interactable {
    public final Item item;
    private LightSource light = null;
    
    public ItemEntity(int x, int y, Item i, double thrown) {
        super(x,y);
        this.item = i;
        this.baseSpeed = 0.1;
        
        
        if (thrown > 0) {
            double r = Math.toRadians((int)(Math.random() * 360));
            double x1 = Math.cos(r), y1 = Math.sin(r);
            this.velocity[0] += x1 * thrown;
            this.velocity[1] += y1 * thrown;
        }
    }
    protected ItemEntity(String[] in) {
        super(in);
        this.item = Item.LOAD(in[10]);
    }
    public void tick() {
        if (item == null) {
            this.BEGONE();
            return;
        }
        if (item.light() > 0 && this.light == null) {
            this.light = new LightSource(0,0,this.item.light(),this.ID(),3);
            main.lights.add(this.light);
        }
        if (item instanceof items.Ticking) {
            item.tick();
            if (item.destroy()) this.BEGONE();
        }
        this.move();
    }
    public void render(Graphics2D g) {
        if (item == null) return;
        g.drawImage(main.art.get(this.item.entity()), this.XY()[0] - 3, this.XY()[1] - 7, null);
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append('[');
        i.append(item.SAVE());
        i.append(']');
        i.append(',');
        return i.toString();
    }
    
    public double size() {return 2;}
    protected byte[] collision() {return new byte[] {0,2};}
    
}
