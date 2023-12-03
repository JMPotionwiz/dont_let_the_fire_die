package core;

import entities.Entity;

public class LightSource {
    private int pos[], radius = 1, targetRadius;
    private long targetID;
    private byte limiter = 0;
    
    public static double flickerIntensity[] = {0,0};
    
    public LightSource(int x, int y, int r, long id, int l) {
        this.pos = new int[] {x,y};
        this.targetRadius = r;
        this.targetID = id;
        this.limiter = (byte)Math.max(5 - l, 0);
    }
    public void tick() {
        if (this.targetID != -1) if (Entity.getByID(this.targetID) != null) {
            Entity e = Entity.getByID(this.targetID);
            this.pos[0] = e.X();
            this.pos[1] = e.Y();
        } else this.targetRadius = 0;
        this.light();
        if (this.radius != this.targetRadius) this.radius = (int)main.between(this.radius, this.targetRadius, 0.2);
    }
    private void light() {
        if (this.radius <= 0) return;
        int x = this.XY()[0] - (int)main.cam.x + (int)(main.mainWindow.gameSize * 0.5);
        int y = this.XY()[1] - (int)main.cam.y + (int)(main.mainWindow.gameSize * 0.5);
        int E_001 = main.lightmap.size(), E_002 = main.lightmap.get(0).size();
        double r0 = this.radius;
        double r1 = (this.radius * 0.5) + r0;
        double r2 = (this.radius * 0.25) + r1;
        double r3 = (this.radius * 0.125) + r2;
        double r4 = (this.radius * 0.0625) + r3;
        
        for (int i = 0; i < E_001; i++) {
            double f = this.flicker();
            for (int j = 0; j < E_002; j++) {
            double d = Math.hypot((j - x) * f, (i - y) * f);
            byte l = main.lightmap.get(i).get(j);
            if (d > r4 || l >= 5) continue;
            if (d <= r0 && l < 5 - this.limiter) {
                main.lightmap.get(i).set(j, (byte)(5 - this.limiter));
            } else if (d <= r1 && l < 4 - this.limiter) {
                main.lightmap.get(i).set(j, (byte)(4 - this.limiter));
            } else if (d <= r2 && l < 3 - this.limiter) {
                main.lightmap.get(i).set(j, (byte)(3 - this.limiter));
            } else if (d <= r3 && l < 2 - this.limiter) {
                main.lightmap.get(i).set(j, (byte)(2 - this.limiter));
            } else if (d <= r4 && l < 1 - this.limiter) {
                main.lightmap.get(i).set(j, (byte)(1 - this.limiter));
            }
        }}
    }
    public int X() {return this.pos[0];}
    public int Y() {return this.pos[1];}
    public int radius() {return this.radius;}
    public void setRadius(int r) {this.targetRadius = r;}
    public void douse() {this.targetRadius = 0;}
    public boolean delete() {
        return radius <= 0;
    }
    private double flicker() {return Math.random() < flickerIntensity[0] ? (0.975 + (Math.random() * flickerIntensity[1])) : 1;}
    public final int[] XY() {
        int out[] = {0,0};
        
        for (int y = this.Y() - main.WORLD_SIZE; y <= this.Y() + main.WORLD_SIZE; y += main.WORLD_SIZE)
        for (int x = this.X() - main.WORLD_SIZE; x <= this.X() + main.WORLD_SIZE; x += main.WORLD_SIZE) {
            if (Math.hypot(x - main.cam.x, y - main.cam.y) == main.distanceFromLight(main.cam.x, main.cam.y, this, false)) {
                out = new int[] {x,y};
            }
        }
        
        return out;
    }
}
