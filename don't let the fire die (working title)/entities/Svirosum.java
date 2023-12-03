package entities;

import java.awt.Graphics2D;
import core.main;
import items.*;

public class Svirosum extends LivingEntity implements AlwaysTick, Monster {
    protected double baseSpeed = 0.025;
    private double targetXY[] = null;
    private Entity target = null, primeTarget = null;
    private byte behavior = 0;
    private long targetID = -1, primeTargetID = -1;
    
    private static final Item loot[] = {new TwigItem(),new LogItem(),new StoneItem(),new CharcoalItem()};
    
    public Svirosum(int x, int y, Entity target) {
        super(x,y);
        this.hp = 9;
        this.maxHp = 9;
        
        this.primeTarget = target;
        
        main.monsters.add(this);
    }
    protected Svirosum(String[] in) {
        super(in);
        int i = 18;
        
        this.behavior = Byte.parseByte(in[i++]);
        this.targetID = Long.parseLong(in[i++]);
        this.primeTargetID = Long.parseLong(in[i++]);
        
        main.monsters.add(this);
    }
    public void tick() {
        if (this.targetID != -1) {
            this.target = Entity.getByID(this.targetID);
            this.targetID = -1;
        }
        if (this.primeTargetID != -1) {
            this.primeTarget = Entity.getByID(this.primeTargetID);
            this.primeTargetID = -1;
        }
        
        super.tick();
        if (this.hp > 0 && this.hp < this.maxHp && Math.random() * 300 < 1) this.hp++;
        if (this.hp <= 0 && this.hurtTime == 1 && Math.random() < 0.35) main.spawnIn.add(new ItemEntity(this.X(), this.Y(), Svirosum.loot[(int)(Math.random() * loot.length)], 2.5));
    }
    protected void control() {
        double[] move = {0,0};
        double speed = this.baseSpeed;
        
        switch (this.behavior) {
            case 0:
                if (this.primeTarget != null) {
                    this.target = this.primeTarget;
                    this.behavior = 3;
                    this.targetXY = null;
                    break;
                }
                if (this.idleTime <= 0) {
                    this.idleTime = (int)(Math.random() * 9) * 60;
                    int r = (int)(Math.random() * 360);
                    double x = Math.sin(Math.toRadians(r)), y = Math.cos(Math.toRadians(r));
                    if (Math.hypot(main.campfire.X() - this.X(), main.campfire.Y() - this.Y()) > 256 && Math.random() < 0.1) {
                        this.targetXY = new double[] {main.campfire.pos[0] + (x * 64), main.campfire.pos[1] + (y * 64)};
                    } else {
                        this.targetXY = new double[] {this.pos[0] + (x * 64), this.pos[1] + (y * 32)};
                    }
                    if (this.targetXY[0] > main.WORLD_SIZE()) {this.targetXY[0] -= main.WORLD_SIZE();} else if (this.targetXY[0] < 0) this.targetXY[0] += main.WORLD_SIZE();
                    if (this.targetXY[1] > main.WORLD_SIZE()) {this.targetXY[1] -= main.WORLD_SIZE();} else if (this.targetXY[1] < 0) this.targetXY[1] += main.WORLD_SIZE();
                    this.behavior = 1;
                } else this.idleTime--;
                if (Math.random() * 60 < 0.125) this.dir[0] = !this.dir[0];
                break;
            case 1:
                if (this.primeTarget != null) {
                    this.target = this.primeTarget;
                    this.behavior = 3;
                    this.targetXY = null;
                    break;
                }
                if (this.targetXY == null) {
                    this.behavior = 0;
                    break;
                }
                if (Math.hypot(this.targetXY[0] - this.pos[0], this.targetXY[1] - this.pos[1]) > 8) {
                    move[0] = this.targetXY[0] - this.pos[0];
                    move[1] = this.targetXY[1] - this.pos[1];
                } else {
                    this.targetXY = null;
                    this.behavior = 0;
                }
                break;
            case 2:
                if (this.target == null || this.target.hp()[0] <= 0 || Math.hypot(this.XY()[0] - this.target.XY()[0], this.XY()[1] - this.target.XY()[1]) > 128) {
                    this.behavior = 0;
                    this.idleTime = (int)(Math.random() * 9) * 60;
                    break;
                }
                if (this.targetXY == null || --this.idleTime <= 0) {
                    this.targetXY = new double[] {this.pos[0] - this.target.pos[0], this.pos[1] - this.target.pos[1]};
                    this.idleTime = (int)(Math.random() * 4 + 1) * 30;
                }
                speed *= 2;
                move[0] = this.targetXY[0];
                move[1] = this.targetXY[1];
                break;
            case 3:
                if (this.target == null || this.target.hp()[0] <= 0) {
                    this.behavior = 0;
                    this.idleTime = (int)(Math.random() * 9) * 60;
                    break;
                } else if (Math.hypot(this.XY()[0] - this.target.XY()[0], this.XY()[1] - this.target.XY()[1]) <= 3 + this.size() + this.target.size()) {
                    if (this.attackCooldown <= 0) {
                        this.behavior = 4;
                        this.attackWarmup = 6 * (int)(Math.random() * 2 + 1);
                    }
                    break;
                } else if (this.aggroTime <= 0 && this.target != this.primeTarget) {
                    this.target = this.primeTarget;
                    this.targetXY = null;
                    break;
                }
                if (this.targetXY == null || --this.idleTime <= 0) {
                    this.targetXY = new double[] {this.target.pos[0] - this.pos[0], this.target.pos[1] - this.pos[1]};
                    this.idleTime = (int)(Math.random() * 3 + 1) * 15;
                }
                speed *= 2.5;
                move[0] = this.targetXY[0];
                move[1] = this.targetXY[1];
                break;
            case 4:
                if (this.attackWarmup > 0) this.attackWarmup--;
                if (this.attackWarmup <= 0) {
                    if (Math.random() < 0.05) {this.sweepAttack();} else this.attack();
                    this.attackCooldown = (int)(Math.random() * 91);
                    this.behavior = 5;
                }
                break;
            case 5:
                if (this.attackTimer <= 0) {
                    this.behavior = 3;
                    this.targetXY = null;
                }
                break;
        }
        if (this.aggroTime > 0) this.aggroTime--;
        if (this.primeTarget != null && this.primeTarget.hp()[0] <= 0) this.primeTarget = null;
        
        double d = Math.hypot(move[0], move[1]);
        if (d != 0) {
            move[0] /= d;
            move[1] /= d;
        }
        
        if (move[0] > 0) {this.dir[0] = true;} else if (move[0] < 0) this.dir[0] = false;
        if (Math.abs(move[0]) + Math.abs(move[1]) > 0) {
            if (++this.moveAnim > 45) this.moveAnim = 1;
        } else this.moveAnim = 0;
        
        this.velocity[0] += move[0] * speed;
        this.velocity[1] += move[1] * speed;
        
        
    }
    public void render(Graphics2D g) {
        if (!this.visible) return;
        StringBuilder i = new StringBuilder("s_virosum."); 
        if (this.hp()[0] <= 0) {
            i.append("dead");
        } else if (this.attackWarmup > 0) {
            i.append("preattack");
        } else if (this.attackTimer > 0) {
            i.append("attack_");
            if (this.attackTimer > 8) {
                i.append(0);
            } else if (this.attackTimer > 4) {
                i.append(1);
            } else i.append(2);
        } else if (this.moveAnim > 0 && this.moveAnim <= 30) {
            i.append("walk_");
            i.append(this.moveAnim <= 15 ? 0 : 1);
        } else i.append("idle");
        if (this.hurtTime > 0) i.append("_hurt");
        if (!this.dir[0]) i.append("_flipped");
        
        g.drawImage(main.art.get(i.toString()), this.XY()[0] - 7, this.XY()[1] - 11, null);
    }
    
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.behavior);
        i.append(',');
        i.append(this.target != null ? this.target.ID() : -1);
        i.append(',');
        i.append(this.primeTarget != null ? this.primeTarget.ID() : -1);
        i.append(',');
        
        return i.toString();
    }
    
    public double size() {return 3;}
    protected byte[] collision() {return new byte[] {(byte)(this.hp > 0 ? 2 : 0),2};}
    protected String hurtsound() {return "sfx_xformidilosahurt";}
    protected void whenDamaged(Entity e) {
        if (this.behavior == 0 || this.behavior == 1) {
            this.target = e;
            this.behavior = 3;
            this.aggroTime = 1200;
        } else if (e != this.primeTarget && (this.behavior == 3 || this.behavior == 4 || this.behavior == 5) && Math.random() < 0.5) {
            this.target = e;
            this.aggroTime = 600;
        }
    }
    public void sweepAttack() {
        boolean hit = false;
        for (Entity e : main.ticking) if (Math.hypot(this.XY()[0] - e.XY()[0], this.XY()[1] - e.XY()[1]) <= 8 + this.size() + e.size() && e != this)
        if (e.damage(this.attackDamage(), 2.5, this) != null) hit = true;
        this.attackAnim();
        if (!hit) main.playSound("sfx_playerswing");
    }
    public void attack() {
        boolean hit = false;
        if (Math.hypot(this.XY()[0] - this.target.XY()[0], this.XY()[1] - this.target.XY()[1]) <= 8 + this.size() + this.target.size() && this.target != this)
        if (this.target.damage(this.attackDamage(), 2.5, this) != null) hit = true;
        this.attackAnim();
        if (!hit) main.playSound("sfx_playerswing");
    }
    
    public int attackDamage() {return 1;}
    public int attackCooldown() {return 60;}
    public void attackAnim() {this.attackTimer = 12;}
}
