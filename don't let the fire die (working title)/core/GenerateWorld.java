package core;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import entities.*;
import items.*;

public class GenerateWorld extends WorldLoadTask {
    private int treeDensity, tree;
    private byte hair, hurt;
    private Color colors[] = new Color[6];
    
    public GenerateWorld(byte size, byte h, byte s, Color[] c) throws Exception {
        if (main.WORLD_LOADED()) throw new Exception("Multiple worlds cannot be loaded at ONCE!");
        main.setWORLD_LOADED(true);
        
        main.WORLD_SIZE = 1024 * size;
        treeDensity = 3;
        switch (size) {
            case (byte) 2 -> treeDensity = 12;
            case (byte) 3 -> treeDensity = 27;
            case (byte) 4 -> treeDensity = 48;
        }
        tree = 1024 * treeDensity;
        hair = h;
        hurt = s;
        colors = c;
        main.loadingWorld = true;
        this.start();
    }
    
    public void run() {
        min = 0;
        max = tree;
        ArrayList<mark> trees = new ArrayList<mark>();
        for (int i = 0; i < tree; i++) {
            min++;
            int x = (int)(Math.random() * main.WORLD_SIZE()), y = (int)(Math.random() * main.WORLD_SIZE());
            boolean NEXT = false;
            for (mark t : trees) if (Math.hypot(x - t.x, y - t.y) < 10) {
                NEXT = true;
                break;
            }
            if (NEXT) continue;
            for (int y1 = y - main.WORLD_SIZE(); y1 <= y + main.WORLD_SIZE(); y1 += main.WORLD_SIZE())
            for (int x1 = x - main.WORLD_SIZE(); x1 <= x + main.WORLD_SIZE(); x1 += main.WORLD_SIZE())
            if (x1 > -10 && x1 < main.WORLD_SIZE() + 10 && y1 > -10 && y1 < main.WORLD_SIZE() + 10)
            trees.add(new mark(x1, y1, x1 == x && y1 == y));
        }
        
        Iterator<mark> IT = trees.iterator();
        while (IT.hasNext()) if (!IT.next().prime) IT.remove();
        
        int camp[] = {(int)(main.WORLD_SIZE() * 0.5), (int)(main.WORLD_SIZE() * 0.5)};
        for (mark t : trees) if (Math.hypot(camp[0] - t.x, camp[1] - t.y) < 48) t.prime = false;
        IT = trees.iterator();
        while (IT.hasNext()) if (!IT.next().prime) IT.remove();
        
        int r, x, y;
        r = (int)(Math.random() * 360);
        x = camp[0] + (int)Math.round(Math.sin(Math.toRadians(r)) * 16);
        y = camp[1] + (int)Math.round(Math.cos(Math.toRadians(r)) * 16);
        
        main.cam = new Camera(x,y);
        main.entities.add(new Player(x, y, hair, hurt, colors));
        
        main.entities.add(new Campfire(camp[0], camp[1]));
        
        for (mark t : trees) {
            if (Math.random() < 0.8) {
                main.entities.add(new Tree(t.x, t.y));
            } else {
                if (Math.random() < 0.4) {main.entities.add(new ItemEntity(t.x, t.y, new StoneItem(), 0));} else main.entities.add(new ItemEntity(t.x, t.y, new TwigItem(), 0));
            }
        }
        
        // r = (int)(Math.random() * 360);
        // x = camp[0] + (int)Math.round(Math.sin(Math.toRadians(r)) * 16);
        // y = camp[1] + (int)Math.round(Math.cos(Math.toRadians(r)) * 16);
        
        // main.entities.add(new ItemEntity(x, y, new StoneAxeItem(), 0));
        
        main.nextWaveAttempt = 18000;
        main.nextWaveChance = 5;
        main.threatLevel = 0;
        
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
