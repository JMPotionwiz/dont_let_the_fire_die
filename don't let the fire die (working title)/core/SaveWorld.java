package core;

import entities.Entity;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class SaveWorld extends WorldLoadTask {
    private final String filename;
    
    public SaveWorld(String filename) throws Exception {
        if (!main.WORLD_LOADED()) throw new Exception("No world to SAVE!");
        main.loadingWorld = true;
        this.filename = filename;
        
        this.start();
    }
    
    public void run() {
        min = 0;
        max = main.entities.size();
        StringBuilder i = new StringBuilder();
        i.append(main.WORLD_SIZE());
        i.append(',');
        i.append(main.id);
        i.append(',');
        i.append(main.nextWaveAttempt);
        i.append(',');
        i.append(main.nextWaveChance);
        i.append(',');
        i.append(main.threatLevel);
        i.append(',');
        i.append(main.waveMonstersToSpawn);
        i.append(",[");
        for (Entity e : main.entities) {
            i.append('[');
            i.append(e.SAVE());
            i.append("],");
            min++;
        }
        i.append("],");
        
        String output = i.toString();
        File save = new File("data/" + filename + ".wrld");
        try {
            File folder = new File("data");
            if (!folder.isDirectory()) {
                folder.mkdir();
            }
            if (!save.exists()) {
                save.createNewFile();
            }
            BufferedWriter w = new BufferedWriter(new FileWriter(save));
            w.write(output);
            w.close();
        } catch (Exception e) {System.out.println(e.toString());}
        
        main.entities.clear();
        main.ticking.clear();
        main.lights.clear();
        main.cam = null;
        main.setWORLD_LOADED(false);
        min++;
        try{this.sleep(1000);}catch(Exception e){}
        
        main.saveLoadTask = null;
        //return;
        System.exit(0); // This is only a temporary thing, the game won't close when saving a world!
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
