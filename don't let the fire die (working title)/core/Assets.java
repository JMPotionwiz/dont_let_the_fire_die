package core;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.io.InputStream;

public class Assets {
    HashMap<String, BufferedImage> sprites = new HashMap<String, BufferedImage>();
    public Assets() {
        generateAsset("player/player_hair_0", 24, 16, "player0", main.playerFrames, false, false);
        generateAsset("player/player_hair_1", 24, 16, "player1", main.playerFrames, false, false);
        generateAsset("player/player_hair_2", 24, 16, "player2", main.playerFrames, false, false);
        generateAsset("player/player_hair_3", 24, 16, "player3", main.playerFrames, false, false);
        generateAsset("player/player_hair_4", 24, 16, "player4", main.playerFrames, false, false);
        generateAsset("player/player_hair_5", 24, 16, "player5", main.playerFrames, false, false);
        generateAsset("player/player_hair_6", 24, 16, "player6", main.playerFrames, false, false);
        generateAsset("player/player_hair_7", 24, 16, "player7", main.playerFrames, false, false);
        generateAsset("player/player_hair_8", 24, 16, "player8", main.playerFrames, false, false);
        
        generateAsset("tree", 32, 32, "tree", new String[] {"0","1"}, true, true);
        generateAsset("campfire", 16, 32, "campfire", new String[] {"0","1","2","3","4","5","6","out"}, true, false);
        generateAsset("item_entity", 8, 8, "item_entity", main.items, false, false);
        generateAsset("item", 8, 8, "item", main.items, false, false);
        generateAsset("slots", 12, 12, "slots", new String[] {"main","off","tool","output"}, false, false);
        generateAsset("healthbar", 48, 4, "healthbar", new String[] {"blank","health","hunger","recover","lost"}, false, false);
        generateAsset("chest", 12, 12, "chest", new String[] {"closed","opened"}, true, true);
        
        // Xenocerva formidilosa
        generateAsset("monsters/x_formidilosa",16,20,"x_formidilosa",new String[]{"idle","walk_0","walk_1","walk_2","preattack","attack_0","attack_1","attack_2","dead"},true,true);
        // Sminos virosum
        generateAsset("monsters/s_virosum",16,12,"s_virosum",new String[]{"idle","walk_0","walk_1","preattack","attack_0","attack_1","attack_2","dead"},true,true);
        
        
        generateFont("text", 4, 6, "text");
        
        
    }
    private void generateAsset(String path, int x, int y, String name, String[] frames, boolean hasHurt, boolean hasFlipped) {
        InputStream tmp = getClass().getResourceAsStream("/assets/" + path + ".png");
        int l = frames.length;
        Image img = null;
        try {
            img = ImageIO.read(tmp);
        } catch(Exception e){
            
        }
        
        BufferedImage normal = new BufferedImage(x,y*l,BufferedImage.TYPE_INT_ARGB), flipped = new BufferedImage(x,y*l,BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = normal.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        
        if (hasFlipped) {
            g = flipped.createGraphics();
            g.translate(x,0);
            g.scale(-1,1);
            g.drawImage(img, 0, 0, null);
            g.dispose();
        }
        
        BufferedImage frame = null;
        
        for (int i = 0; i < l; i++) {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) normal, 0, y * i * -1, null);
            g.dispose();
            sprites.put(name + "." + frames[i], frame);
        }
        if (hasFlipped) for (int i = 0; i < l; i++) {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) flipped, 0, y * i * -1, null);
            g.dispose();
            sprites.put(name + "." + frames[i] + "_flipped", frame);
        }
        if (hasHurt) for (int i = 0; i < l; i++) {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) normal, 0, y * i * -1, null);
            g.dispose();
            for (int y1 = 0; y1 < y; y1++) for (int x1 = 0; x1 < x; x1++) {
                Color c = new Color(frame.getRGB(x1,y1), true);
                if ((int)c.getAlpha() <= 0) continue;
                c = new Color(1f, 1f, 1f);
                frame.setRGB(x1, y1, c.getRGB());
            }
            sprites.put(name + "." + frames[i] + "_hurt", frame);
        }
        if (hasHurt && hasFlipped) for (int i = 0; i < l; i++) {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) flipped, 0, y * i * -1, null);
            g.dispose();
            for (int y1 = 0; y1 < y; y1++) for (int x1 = 0; x1 < x; x1++) {
                Color c = new Color(frame.getRGB(x1,y1), true);
                if ((int)c.getAlpha() <= 0) continue;
                c = new Color(1f, 1f, 1f);
                frame.setRGB(x1, y1, c.getRGB());
            }
            sprites.put(name + "." + frames[i] + "_hurt_flipped", frame);
        }
    }
    private void generateFont(String path, int x, int y, String name) {
        InputStream tmp = getClass().getResourceAsStream("/assets/" + path + ".png");
        int l = main.letters.length();
        Image img = null;
        try {
            img = ImageIO.read(tmp);
        } catch(Exception e){
            
        }
        
        BufferedImage normal = new BufferedImage(x*l,y,BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = normal.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        
        BufferedImage frame = null;
        
        for (int i = 0; i < l; i++) {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) normal, x * i * -1, 0, null);
            g.dispose();
            sprites.put(name + "." + main.letters.charAt(i), frame);
        }
    }
    private void generateAsset(String path, int x, int y, String name, boolean hasFlipped) {
        InputStream tmp = getClass().getResourceAsStream("/assets/" + path + ".png");
        Image img = null;
        try {
            img = ImageIO.read(tmp);
        } catch(Exception e){
            
        }
        
        BufferedImage normal = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB), flipped = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g = normal.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        
        if (hasFlipped) {
            g = flipped.createGraphics();
            g.translate(x,0);
            g.scale(-1,1);
            g.drawImage(img, 0, 0, null);
            g.dispose();
        }
        
        BufferedImage frame = null;
        
        {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) normal, 0, 0, null);
            g.dispose();
            sprites.put(name, frame);
        }
        if (hasFlipped) {
            frame = new BufferedImage(x,y,BufferedImage.TYPE_INT_ARGB);
            g = frame.createGraphics();
            g.drawImage((Image) flipped, 0, 0, null);
            g.dispose();
            sprites.put(name + "_flipped", frame);
        }
    }
    public BufferedImage combine(BufferedImage[] imgs) {
        if (imgs.length <= 0) return null;
        BufferedImage out = new BufferedImage(imgs[0].getWidth(),imgs[0].getHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        int E_001 = imgs.length;
        for (int i = 0; i < E_001; i++) g.drawImage((Image)imgs[i], 0, 0, null);
        return out;
    }
    public Image get(String s) {return (Image)sprites.get(s);}
}