package core;

public abstract class WorldLoadTask extends Thread {
    protected int min = 0, max = 1;
    public void run() {}
    public int[] progress() {return new int[] {min,max};}
    
}
