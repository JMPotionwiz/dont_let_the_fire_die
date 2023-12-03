package entities;

public abstract class LivingEntity extends MovingEntity {
    protected int attackTimer = 0, attackWarmup = 0, attackCooldown = 0, aggroTime = 0, idleTime = 0, deathTimer = 600, nextDeathTimer = 30;
    protected boolean visible = true;
    
    public LivingEntity(int x, int y) {
        super(x,y);
    }
    protected LivingEntity(String[] in) {
        super(in);
        int i = 10;
        
        this.attackTimer = Integer.parseInt(in[i++]);
        this.attackWarmup = Integer.parseInt(in[i++]);
        this.attackCooldown = Integer.parseInt(in[i++]);
        this.aggroTime = Integer.parseInt(in[i++]);
        this.idleTime = Integer.parseInt(in[i++]);
        this.deathTimer = Integer.parseInt(in[i++]);
        this.nextDeathTimer = Integer.parseInt(in[i++]);
        this.visible = Boolean.parseBoolean(in[i++]);
        
    }
    public void tick() {
        super.tick();
        if (this.hp()[0] > 0) {
            if (this.attackTimer > 0) this.attackTimer--;
            if (this.attackCooldown > 0 && this.attackTimer <= 0) this.attackCooldown--;
            this.control();
        } else {
            if (--this.deathTimer <= 0) {
                this.deathTimer = this.nextDeathTimer;
                this.visible = !this.visible;
                if (this.visible) this.nextDeathTimer *= 0.9;
                if (this.nextDeathTimer <= 0) this.BEGONE();
            }
        }
        this.move();
    }
    protected abstract void control();
    public String SAVE() {
        StringBuilder i = new StringBuilder(super.SAVE());
        i.append(this.attackTimer);
        i.append(',');
        i.append(this.attackWarmup);
        i.append(',');
        i.append(this.attackCooldown);
        i.append(',');
        i.append(this.aggroTime);
        i.append(',');
        i.append(this.idleTime);
        i.append(',');
        i.append(this.deathTimer);
        i.append(',');
        i.append(this.nextDeathTimer);
        i.append(',');
        i.append(this.visible);
        i.append(',');
        
        return i.toString();
    }
}
