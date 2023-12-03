package core;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import entities.*;
import items.Item;
import java.util.Iterator;
import java.util.HashMap;
import items.TwigItem;
import items.StoneItem;
import items.StoneAxeItem;
import java.awt.Color;

public class main {
    private static boolean WORLD_LOADED = false;
    
    public static Window mainWindow;
    public static int prevWindowSizes[];
    public static Camera cam = null;
    
    public static final String[] playerFrames = {"idle","walk_0","walk_1","attack0_0","attack0_1","attack0_2","attack1_0","attack1_1","attack1_2","dead"};
    public static final String[] items = {
        "log","stone_axe","unlit_torch","lit_torch","charcoal","stone","twig","x_formidilosa_venison","x_formidilosa_steak","stone_spear","chest","wooden_club"
    };
    public static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890.!?:,;'\"-+_()[]{}/\\*%<>";
    public static Assets art = new Assets();
    
    private static boolean fullscreen = false;
    public enum Key {
        UP, DOWN, LEFT, RIGHT, ENTER,
        
        DROP, SWAP, DROP_ALL, TOOL, CRAFT, CRAFT_LEFT, CRAFT_RIGHT, INTERACT,
        SLOT1, SLOT2, SLOT3, SLOT4, SLOT5, SLOT6, SLOT7, SLOT8, SLOT9,
        
        FULLSCREEN, ESC
    }
    public static HashMap<Key, keybind> keys = new HashMap<Key, keybind>();
    
    public static ArrayList<Entity> entities = new ArrayList<Entity>(), spawnIn = new ArrayList<Entity>(), ticking = new ArrayList<Entity>();
    public static ArrayList<Monster> monsters = new ArrayList<Monster>();
    public static int spawnCap = 0;
    
    public static Player playerAccess = null;
    public static Campfire campfire = null;
    public static long id = 0;
    public static boolean paused = false, allowPausing = true;
    
    public static HashMap<String, ArrayList<Recipe>> crafts = new HashMap<String, ArrayList<Recipe>>();
    
    public static ArrayList<LightSource> lights = new ArrayList<LightSource>();
    public static ArrayList<ArrayList<Byte>> lightmap;
    public static byte LightFlickerIntensity = 100;
    
    public static int WORLD_SIZE = 1024;
    
    public static int nextWaveAttempt = 36000, nextWaveChance = 5, threatLevel = 0, waveMonstersToSpawn = 0;
    
    public static boolean loadingWorld = true;
    public static WorldLoadTask saveLoadTask = null;
    private static String name;
    
    public static ArrayList<Sound> sounds = new ArrayList<Sound>();
    private static Sound bg_silence;
    private static ArrayList<String> soundsQueue = new ArrayList<String>();
    public static float sfxVolume = 1f;
    
    private static final int TICKRATE = 1000 / 60;
    
    public static void main(String[] args) {
        if (args.length > 0) {
            name = args[0];
        } else name = "world";
        try {
            bg_silence = new Sound("bg_silence");
            bg_silence.Loop();
        } catch (Exception e) {}
        
        keys.put(Key.UP, new keybind("W"));
        keys.put(Key.DOWN, new keybind("S"));
        keys.put(Key.LEFT, new keybind("A"));
        keys.put(Key.RIGHT, new keybind("D"));
        keys.put(Key.ENTER, new keybind("Enter"));
        keys.put(Key.DROP, new keybind("Q"));
        keys.put(Key.SWAP, new keybind("F"));
        keys.put(Key.TOOL, new keybind("E"));
        keys.put(Key.FULLSCREEN, new keybind("F11"));
        keys.put(Key.ESC, new keybind("Escape"));
        keys.put(Key.DROP_ALL, new keybind("Ctrl"));
        keys.put(Key.INTERACT, new keybind("Shift"));
        keys.put(Key.CRAFT, new keybind("C"));
        keys.put(Key.CRAFT_LEFT, new keybind("Left"));
        keys.put(Key.CRAFT_RIGHT, new keybind("Right"));
        
        keys.put(Key.SLOT1, new keybind("1"));
        keys.put(Key.SLOT2, new keybind("2"));
        keys.put(Key.SLOT3, new keybind("3"));
        keys.put(Key.SLOT4, new keybind("4"));
        keys.put(Key.SLOT5, new keybind("5"));
        keys.put(Key.SLOT6, new keybind("6"));
        keys.put(Key.SLOT7, new keybind("7"));
        keys.put(Key.SLOT8, new keybind("8"));
        keys.put(Key.SLOT9, new keybind("9"));
        
        crafts = InitRecipes.run();
        
        mainWindow = new Window("<- temporary icon. | \"" + name + '"');
        mainWindow.addKeyListener(new keyboardInput());
        mainWindow.getCanvas().addKeyListener(new keyboardInput());
        
        LightSource.flickerIntensity[0] = (double)LightFlickerIntensity * 0.01 * 0.5;
        LightSource.flickerIntensity[1] = ((double)LightFlickerIntensity * 0.005 + 0.5) * 0.06;
        
        lightmap = resetLightmap();
        
        //try{Thread.sleep(200);}catch(Exception e){}
        //System.out.println(SAVE());
        try{saveLoadTask = new LoadWorld(name);}catch(Exception e){
            byte hair = 2;
            Color colors[] = {new Color(47,23,0), new Color(0,0,0), new Color(63,127,255), new Color(47,23,0), new Color(127,63,31), new Color(91,91,91)};
            try{saveLoadTask = new GenerateWorld((byte) 4, hair, (byte) 1, colors);}catch(Exception e1){}
        }
        
        while (true) {
            long s = System.currentTimeMillis();
            if (pressed(Key.FULLSCREEN)) {
                if (!fullscreen) mainWindow.NOWS_YOUR_CHANCE_TO_BE_A_BIG_SHOT();
                fullscreen = true;
            } else fullscreen = false;
            
            if (pressed(Key.ESC) && allowPausing) {
                paused = !paused;
                allowPausing = false;
            } else if (!pressed(Key.ESC)) allowPausing = true;
            
            if (paused && !loadingWorld && pressed(Key.ENTER)) {
                paused = false;
                try{saveLoadTask = new SaveWorld(name);}catch(Exception e){}
            }
            
            if (!paused && !loadingWorld) {
                if (Math.random() * 240 < 0.1) {
                    spawnCap = 0;
                    int E_001 = monsters.size();
                    for (int i = 0; i < E_001; i++) {
                        if (!(monsters.get(i) instanceof Entity)) {
                            monsters.remove(i);
                            i--;
                            E_001--;
                            continue;
                        }
                        Entity e = (Entity) monsters.get(i);
                        if (e.hp()[0] <= 0) {
                            monsters.remove(i);
                            i--;
                            E_001--;
                            continue;
                        }
                    }
                    for (Monster m : monsters) spawnCap += m.addsToSpawnCap;
                    
                    if (spawnCap < 1 + (threatLevel * 0.2)) {
                        int level = (int)Math.min(threatLevel * 0.2, 0), r = (int)(Math.random() * 360), d = (int)(Math.random() * 128) + 256;
                        int tmp = (int)(Math.random() * level), x = playerAccess.X() + (int)(Math.sin(r) * d), y = playerAccess.Y() + (int)(Math.cos(r) * d);
                        
                        switch (tmp) {
                            case 0 -> entities.add(new Xformidilosa(x,y,null));
                        }
                    }
                }
                if (nextWaveAttempt <= 0 && waveMonstersToSpawn <= 0) {
                    if (Math.random() * 100 < nextWaveChance) {
                        waveMonstersToSpawn = 4 + (int)(threatLevel * 0.5);
                        waveMonstersToSpawn += (int)(Math.random() * (waveMonstersToSpawn * 0.4));
                    } else {
                        nextWaveChance += (int)(Math.random() * 8) + 5;
                        nextWaveAttempt = (int)(Math.random() * 61 + 60) * 60;
                    }
                } else if (nextWaveAttempt <= 0 && waveMonstersToSpawn > 0) {
                    int r, d, x = 0, y = 0;
                    for (int i = 0; i < 4; i++) {
                        r = (int)(Math.random() * 360);
                        d = (int)(Math.random() * 128) + 256;
                        x = campfire.X() + (int)(Math.sin(r) * d);
                        y = campfire.Y() + (int)(Math.cos(r) * d);
                        if (Math.hypot(playerAccess.X() - x, playerAccess.Y() - y) >= 128) break;
                        x = playerAccess.X() + (int)(Math.sin(r) * d);
                        y = playerAccess.Y() + (int)(Math.cos(r) * d);
                    }
                    Entity target = Math.random() < 0.8 ? campfire : playerAccess;
                    if (Math.random() < 0.75) {
                        spawnIn.add(new Svirosum(x, y, target));
                    } else {
                        int level = (int)Math.min(threatLevel * 0.2, 0);
                        int tmp = (int)(Math.random() * level);
                        switch (tmp) {
                            case 0 -> entities.add(new Xformidilosa(x,y,target));
                        }
                    }
                    if (--waveMonstersToSpawn <= 0) {
                        threatLevel++;
                        nextWaveAttempt = 54000;
                        nextWaveChance = (int)Math.max(nextWaveChance * 0.25, 5);
                    } else {
                        nextWaveAttempt = (int)(Math.random() * 60 + 1);
                    }
                } else nextWaveAttempt--;
                
                if (spawnIn.size() > 0) {
                    for (Entity i : spawnIn) entities.add(i);
                    spawnIn.clear();
                }
                
                for (Entity e : ticking) e.pretick();
                for (Entity e : ticking) e.tick();
                ticking.clear();
                for (Entity e : entities) {
                    if((Math.hypot(e.XY()[0] - cam.x, e.XY()[1] - cam.y) < 256 || e instanceof AlwaysTick) && !e.delete()) ticking.add(e);
                    if(!(e instanceof Player) && Math.hypot(e.XY()[0] - cam.x, e.XY()[1] - cam.y) > WORLD_SIZE - e.distanceUntilUnrendered()) e.recalculateReletivePos();
                }
                
                lightmap = resetLightmap();
                for (LightSource l : lights) l.tick();
            }
            
            mainWindow.render();
            
            
            if (!paused && !loadingWorld) {
                Iterator<Entity> IT = entities.iterator();
                while(IT.hasNext()) if (IT.next().delete()) IT.remove();
                
                Iterator<LightSource> IT2 = lights.iterator();
                while(IT2.hasNext()) if (IT2.next().delete()) IT2.remove();
            }
            Iterator<Sound> IT3 = sounds.iterator();
            while(IT3.hasNext()) if (IT3.next().canRemove()) IT3.remove();
            soundsQueue.clear();
            
            if (false) break;
            long end = System.currentTimeMillis() - s;
            try{Thread.sleep(Math.max(TICKRATE - end, 0));}catch(Exception e){};
        }
        System.exit(0);
    }
    public static String[] SPLIT_DATA(String in) {
        StringBuilder s = new StringBuilder();
        ArrayList<StringBuilder> tmp = new ArrayList<StringBuilder>();
        int nested = 0;
        int E_001 = in.length();
        for (int i = 0; i < E_001; i++) {
            if (in.charAt(i) == '[' && nested <= 0) { // Begins nested data. "[" is not included
                nested++;
            } else if (in.charAt(i) == '[' && nested > 0) { // Additional nested data. "[" is included
                nested++;
                s.append(in.charAt(i));
            } else if (in.charAt(i) == ']' && nested > 1) { // Ends additional nested data. "]" is included
                nested--;
                s.append(in.charAt(i));
            } else if (in.charAt(i) == ']' && nested == 1) { // Ends nested data. "]" is not included
                nested--;
            } else if (in.charAt(i) == ',' && nested <= 0) { // Unnested commas mark a split in data
                tmp.add(s);
                s = new StringBuilder();
            } else s.append(in.charAt(i));
        }
        E_001 = tmp.size();
        String out[] = new String[E_001];
        
        for (int i = 0; i < E_001; i++) out[i] = tmp.get(i).toString();
        return out;
    }
    
    
    public static boolean pressed(main.Key k) {
        try {return keys.get(k).pressed();} catch (Exception e) {}
        return false;
    }
    private static ArrayList<ArrayList<Byte>> resetLightmap() {
        ArrayList<ArrayList<Byte>> out = new ArrayList<ArrayList<Byte>>();
        for (int i = 0; i < mainWindow.gameSize; i++) {
            ArrayList<Byte> tmp = new ArrayList<Byte>();
            for (int j = 0; j < mainWindow.gameSize; j++) {
                tmp.add((byte) 0);
            }
            out.add(tmp);
        }
        return out;
    }
    
    public static double between(double x1, double x2, double x3) {
        return ((x2 - x1) * x3) + x1;
    }
    public static void playSound(String sound) {
        for (String i : soundsQueue) if (sound.equals(i)) return;
        try {
            Sound tmp = new Sound(sound);
            tmp.setVolume(sfxVolume);
            tmp.Start();
            sounds.add(tmp);
            soundsQueue.add(sound);
        } catch (Exception e) {}
    }
    public static double distanceFromEntity(double x, double y, Entity e) {
        double d[] = {0,0,0,0,0,0,0,0,0};
        int index = 0;
        
        for (int y1 = e.Y() - main.WORLD_SIZE; y1 <= e.Y() + main.WORLD_SIZE; y1 += main.WORLD_SIZE)
        for (int x1 = e.X() - main.WORLD_SIZE; x1 <= e.X() + main.WORLD_SIZE; x1 += main.WORLD_SIZE) {
            d[index] = Math.hypot(x - x1, y - y1);
            index++;
        }
        
        double out = d[0];
        for (int i = 1; i < 9; i++) out = Math.min(d[i], out);
        return out;
    }
    public static double distanceFromLight(double x, double y, LightSource l, boolean edge) {
        double d[] = {0,0,0,0,0,0,0,0,0};
        int index = 0;
        
        for (int y1 = l.Y() - main.WORLD_SIZE; y1 <= l.Y() + main.WORLD_SIZE; y1 += main.WORLD_SIZE)
        for (int x1 = l.X() - main.WORLD_SIZE; x1 <= l.X() + main.WORLD_SIZE; x1 += main.WORLD_SIZE) {
            d[index] = Math.hypot(x - x1, y - y1);
            index++;
        }
        
        double out = d[0];
        for (int i = 1; i < 9; i++) out = Math.min(d[i], out);
        if (edge) out = Math.max(out - ((double)l.radius() * 1.75), 0);
        return out;
    }
    public static final boolean WORLD_LOADED() {return WORLD_LOADED;}
    public static final void setWORLD_LOADED(boolean set) {WORLD_LOADED = set;}
    public static final int WORLD_SIZE() {return WORLD_SIZE;}
}







