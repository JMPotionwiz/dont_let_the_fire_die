package entities;

import core.main;

public abstract class MovingEntity extends Entity {
    protected double velocity[] = {0,0}, baseSpeed;
    protected int moveAnim = 0;
    private final double drift = 0.75;
    
    public MovingEntity(int x, int y) {
        super(x,y);
    }
    protected MovingEntity(String[] in) {
        super(in);
        this.velocity[0] = Double.parseDouble(in[8]);
        this.velocity[1] = Double.parseDouble(in[9]);
    }
    public void tick() {
        super.tick();
    }
    protected final void move() {
        if (this.velocity[0] == 0 && this.velocity[1] == 0) return;
        int moved[] = {this.X(), this.Y()};
        this.pos[0] += this.velocity[0];
        this.pos[1] += this.velocity[1];
        
        this.velocity[0] *= this.drift;
        this.velocity[1] *= this.drift;
        
        for (Entity e : main.ticking) if (e != this && Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) < this.size() + e.size()) {
            if (this.collision()[1] > e.collision()[0]) continue;
            double d = Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]);
            double x = this.XY()[0] - e.XY()[0], y = this.XY()[1] - e.XY()[1];
            if (d != 0) {
                x /= d;
                y /= d;
            }
            d = Math.hypot(this.X() - Math.round(moved[0]), this.Y() - Math.round(moved[1]));
            this.pos[0] += x * d;
            this.pos[1] += y * d;
        }
        
        boolean recalc = false;
        if (this.pos[0] < 0) {
            this.pos[0] += main.WORLD_SIZE;
            recalc = true;
        } else if (this.pos[0] >= main.WORLD_SIZE) {
            this.pos[0] -= main.WORLD_SIZE;
            recalc = true;
        }
        if (this.pos[1] < 0) {
            this.pos[1] += main.WORLD_SIZE;
            recalc = true;
        } else if (this.pos[1] >= main.WORLD_SIZE) {
            this.pos[1] -= main.WORLD_SIZE;
            recalc = true;
        }
        if ((this.X() != moved[0] || this.Y() != moved[1] || recalc) && !(this instanceof Player)) this.recalculateReletivePos();
        if (Math.hypot(this.velocity[0], this.velocity[1]) < this.baseSpeed * 0.01) {
            this.velocity[0] = 0;
            this.velocity[1] = 0;
        }
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(Math.round(this.velocity[0] * 1000) / 1000);
        i.append(',');
        i.append(Math.round(this.velocity[1] * 1000) / 1000);
        i.append(',');
        return i.toString();
    }
    
}
