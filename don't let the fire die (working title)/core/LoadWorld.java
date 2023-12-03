package core;

import entities.Entity;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LoadWorld extends WorldLoadTask {
    private final String filename;
    private final File save;
    
    public LoadWorld(String filename) throws Exception {
        if (main.WORLD_LOADED()) throw new Exception("Multiple worlds cannot be loaded at ONCE!");
        save = new File("data/" + filename + ".wrld");
        if (!save.exists()) throw new IOException("File " + filename + ".wrld does not exist!");
        
        main.setWORLD_LOADED(true);
        main.loadingWorld = true;
        this.filename = filename;
        
        this.start();
    }
    
    public void run() {
        min = 0;
        max = 1;
        main.entities.clear();
        main.ticking.clear();
        main.lights.clear();
        main.cam = new Camera(0,0);
        
        String raw = null;
        try {
            Scanner r = new Scanner(save);
            raw = r.nextLine();
        } catch (Exception e) {}
        if (raw == null) return;
        
        String in[] = main.SPLIT_DATA(raw);
        main.WORLD_SIZE = Integer.parseInt(in[0]);
        main.id = Long.parseLong(in[1]);
        main.nextWaveAttempt = Integer.parseInt(in[2]);
        main.nextWaveChance = Integer.parseInt(in[3]);
        main.threatLevel = Integer.parseInt(in[4]);
        main.waveMonstersToSpawn = Integer.parseInt(in[5]);
        
        in = main.SPLIT_DATA(in[6]);
        
        max = in.length;
        for (String i : in) {
            Entity.LOAD(i);
            min++;
        }
        
        
        min++;
        try{this.sleep(1000);}catch(Exception e){}
        
        main.loadingWorld = false;
        main.saveLoadTask = null;
        return;
    }
    public int[] progress() {return new int[] {min,max};}
    
    private class mark {
        int x, y;
        boolean prime = false;
        mark(int x, int y, boolean p) {
            this.x = x;
            this.y = y;
            this.prime = p;
        }
    }
}
