package core;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.*;
import java.util.HashSet;
import entities.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.image.BufferedImage;

public class Window extends JFrame {
    private String name;
    private Canvas c;
    private boolean fullscreen;
    public final int gameSize = 128;
    public int craftingOffset[] = {0,0};
    
    public Window(String name) {
        setSize(526 - 32, 549 - 32);
        //setSize(512, 512);
        this.name = name;
        setTitle(name);
        //setResizable(false);
        //fullscreen = false;
        
        //setUndecorated(true);
        
        //Object testing = Window.class.getResourceAsStream("/assets/icon.png");
        //System.out.println(testing);
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/icon.png"));
        Image windowIcon = icon.getImage();
        setIconImage(windowIcon);

        c = new Canvas() {
            public void paintComponent(Graphics g) {};  
        };
        
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) Math.floor((dim.width - getSize().width) / 2), (int) Math.floor((dim.height - getSize().height) / 2));
        
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank cursor");
        c.setCursor(blankCursor);
        
        c.setBackground(Color.black);
        add(c);
        setVisible(true);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void render() {
        if (!isDisplayable()) return;
        BufferedImage buff = new BufferedImage(gameSize, gameSize, 1);
        
        Graphics2D g = buff.createGraphics();
        
        g.translate(Math.floor(gameSize / 2), Math.floor(gameSize / 2));
        
        g.setColor(new Color(40,40,50));
        g.fillRect(-64,-64,128,128);
        
        if (main.cam != null) {
            g.translate(0 - Math.round(main.cam.x), 0 - Math.round(main.cam.y));
        }
        
        g.setColor(new Color(1f,1f,1f));
        int y1 = main.WORLD_SIZE + 1, y2 = -main.WORLD_SIZE - 1;
        ArrayList<Entity> rendering = new ArrayList<Entity>();
        if (!main.loadingWorld) for (Entity e : main.entities) if (main.distanceFromEntity(main.cam.x, main.cam.y, e) <= e.distanceUntilUnrendered()) {
            rendering.add(e);
            y1 = Math.min(y1, e.XY()[1]);
            y2 = Math.max(y2, e.XY()[1]);
        }
        for (int y = y1; y <= y2; y++) for (Entity e : rendering) if (e.XY()[1] == y) e.render(g);
        //MAIN RENDERING STUFF GOES HERE!
        
        if (main.cam != null) {
            g.translate(Math.round(main.cam.x), Math.round(main.cam.y));
        }
        
        int E_001 = buff.getHeight(), E_002 = buff.getWidth();
        
        if (main.lightmap != null) for (int y = 0; y < E_001; y++) for (int x = 0; x < E_002; x++) {
            if (main.lightmap.get(y).get(x) >= 5) continue;
            byte l = main.lightmap.get(y).get(x);
            Color c = null;
            if (main.lightmap.get(y).get(x) <= 0) {
                c = new Color(0,0,0);
                buff.setRGB(x, y, c.getRGB());
            } else {
                c = new Color(buff.getRGB(x,y));
                float shade = 1;
                switch (l) {
                    case 4 -> shade = 0.75f;
                    case 3 -> shade = 0.5f;
                    case 2 -> shade = 0.25f;
                    case 1 -> shade = 0.125f;
                }
                float red = ((float)c.getRed() / 255) * shade;
                float green = ((float)c.getGreen() / 255) * shade;
                float blue = ((float)c.getBlue() / 255) * shade;
                
                c = new Color(red, green, blue);
                if (c != null) buff.setRGB(x, y, c.getRGB());
            }
        }
        
        //for (int i = -128; i < 128; i += 16) for (int j = -128; j < 128; j += 16) g.drawImage(main.art.get("player.idle"), j, i, null);
        if (main.playerAccess != null) {
            int x, y;
            String count;
            
            x = 52;
            y = 40;
            g.drawImage(main.art.get("slots.tool"), x, y, null);
            if (main.playerAccess.getItem(2) != null) {
                g.drawImage(main.art.get(main.playerAccess.getItem(2).item()), x + 2, y + 2, null);
                int tmp[] = main.playerAccess.getItem(2).hp();
                if (tmp[0] < tmp[1]) drawHealthBar(g, x + 2, y + 9, 8, tmp[0], tmp[1], new Color(255,0,0), new Color(0,0,0));
            }
            count = main.playerAccess.getItemCount(1) + "";
            if (main.playerAccess.getItemCount(2) > 1) renderText(g, count, x + 11 - getTextWidth(count), y + 6);
            
            x = 38;
            y = 52;
            g.drawImage(main.art.get("slots.off"), x, y, null);
            if (main.playerAccess.getItem(1) != null) {
                g.drawImage(main.art.get(main.playerAccess.getItem(1).item()), x + 2, y + 2, null);
                int tmp[] = main.playerAccess.getItem(1).hp();
                if (tmp[0] < tmp[1]) drawHealthBar(g, x + 2, y + 9, 8, tmp[0], tmp[1], new Color(255,0,0), new Color(0,0,0));
            }
            count = main.playerAccess.getItemCount(1) + "";
            if (main.playerAccess.getItemCount(1) > 1) renderText(g, count, x + 12 - getTextWidth(count), y + 7);
            
            x = 50;
            y = 50;
            g.drawImage(main.art.get("slots.main"), x, y, null);
            if (main.playerAccess.getItem(0) != null) {
                g.drawImage(main.art.get(main.playerAccess.getItem(0).item()), x + 2, y + 2, null);
                int tmp[] = main.playerAccess.getItem(0).hp();
                if (tmp[0] < tmp[1]) drawHealthBar(g, x + 1, y + 10, 10, tmp[0], tmp[1], new Color(255,0,0), new Color(0,0,0));
            }
            count = main.playerAccess.getItemCount(0) + "";
            if (main.playerAccess.getItemCount(0) > 1) renderText(g, count, x + 13 - getTextWidth(count), y + 8);
            
            if (main.playerAccess.crafting()) {
                int E_003 = main.playerAccess.currentCraft().recipe.length, E_004 = main.playerAccess.currentCraft().output.length;
                if (craftingOffset[1] != 0) craftingOffset[0] = 0;
                x = (E_003 + E_004 + 1) * -6 + craftingOffset[0];
                y = -38 + craftingOffset[1];
                for (int i = 0; i < E_003; i++) {
                    g.drawImage(main.art.get("slots.main"), x, y, null);
                    g.drawImage(main.art.get(main.playerAccess.currentCraft().recipe[i].item()), x + 2, y + 2, null);
                    x += 12;
                }
                g.drawImage(main.art.get("slots.output"), x, y, null);
                x += 12;
                for (int i = 0; i < E_004; i++) {
                    g.drawImage(main.art.get("slots.main"), x, y, null);
                    g.drawImage(main.art.get(main.playerAccess.currentCraft().output[i].item()), x + 2, y + 2, null);
                    x += 12;
                }
            } else if (main.playerAccess.accessing() && main.playerAccess.accessing instanceof HasInventory) {
                HasInventory e = (HasInventory) main.playerAccess.accessing;
                int w = 1, h = 1, slots = e.totalSlots(), index = 0;
                if (slots % 3 == 0) {
                    w = 3;
                    h = slots / 3;
                } else if (slots % 4 == 0) {
                    w = 4;
                    h = slots / 4;
                } else w = slots;
                x = w * -6 + craftingOffset[0];
                y = -32 + (h * -6) + craftingOffset[1];
                for (int i = 0; i < h; i++) for (int j = 0; j < w; j++) {
                    g.drawImage(main.art.get("slots.main"), x + (j * 12), y + (i * 12), null);
                    if (e.getItem(index) != null) {
                        g.drawImage(main.art.get(e.getItem(index).item()), x + (j * 12) + 2, y + (i * 12) + 2, null);
                        int tmp[] = e.getItem(index).hp();
                        if (tmp[0] < tmp[1]) drawHealthBar(g, x + (j * 12) + 1, y + (i * 12) + 10, 10, tmp[0], tmp[1], new Color(255,0,0), new Color(0,0,0));
                    }
                    count = e.getItemCount(index) + "";
                    if (e.getItemCount(index) > 1) renderText(g, count, x + (j * 12) + 12 - getTextWidth(count), y + (i * 12) + 7);
                    index++;
                }
            }
            
            x = -60;
            y = 52;
            g.drawImage(main.art.get("healthbar.blank"), x, y, null);
            int h = Math.max((int)Math.round(((double)main.playerAccess.hp()[0] / main.playerAccess.hp()[1]) * 48), (main.playerAccess.hp()[0] > 0 ? 1 : 0));
            if (h > 0) {
                BufferedImage tmp = new BufferedImage(h, 4, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g1 = tmp.createGraphics();
                g1.drawImage(main.art.get("healthbar.health"), 0, 0, null);
                g1.dispose();
                g.drawImage((Image) tmp, x, y, null);
            }
            
            x = -60;
            y = 58;
            g.drawImage(main.art.get("healthbar.blank"), x, y, null);
            h = Math.max((int)Math.round(((double)main.playerAccess.food() / 32) * 48), (main.playerAccess.food() > 0 ? 1 : 0));
            if (h > 0) {
                BufferedImage tmp = new BufferedImage(h, 4, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g1 = tmp.createGraphics();
                g1.drawImage(main.art.get("healthbar.hunger"), 0, 0, null);
                g1.dispose();
                g.drawImage((Image) tmp, x, y, null);
            }
        }
        if (!main.paused) {
            if (craftingOffset[0] != 0) craftingOffset[0] = (int)main.between(craftingOffset[0], 0, 0.5);
            if (craftingOffset[1] != 0) craftingOffset[1] = (int)main.between(craftingOffset[1], 0, 0.5);
        }
        //STUFF THAT ISN'T AFFECTED BY CAMERA GOES HERE!
        
        if (main.loadingWorld || !main.WORLD_LOADED()) {
            g.setColor(Color.BLACK);
            g.fillRect(-64,-64,128,128);
            //g.setColor(new Color(255,0,0));
            //g.fillRect(0,0,1,1);
            
            if (main.saveLoadTask instanceof GenerateWorld) {
                renderText(g, "Generating World", (int)(getTextWidth("Generating World") * -0.5), -24);
            } else if (main.saveLoadTask instanceof SaveWorld) {
                renderText(g, "Saving World", (int)(getTextWidth("Saving World") * -0.5), -24);
            } else if (main.saveLoadTask instanceof LoadWorld) {
                renderText(g, "Loading World", (int)(getTextWidth("Loading World") * -0.5), -24);
            } else renderText(g, "No World Loaded", (int)(getTextWidth("No World Loaded") * -0.5), -24);
            //renderText(g, "Please be patient", (int)(getTextWidth("Please be patient") * -0.5), -16);
            
            if (main.saveLoadTask != null) {
                int p1 = main.saveLoadTask.progress()[0], p2 = main.saveLoadTask.progress()[1];
                g.setColor(new Color(255, 255, 255));
                g.fillRect(-34, 14, 68, 5);
                g.setColor(new Color(0, 0, 0));
                g.fillRect(-33, 15, 66, 3);
                drawHealthBar(g, -32, 16, 64, p1, p2, new Color(255,255,255), new Color(63,63,63));
                if (p1 <= p2) {
                    int percent = (int)(100 * ((double)p1 / (double)p2));
                    renderText(g, percent + "%", (int)(getTextWidth(percent + "%") * -0.5), 24);
                } else renderText(g, "Done!", (int)(getTextWidth("Done!") * -0.5), 24);
            }
            
        }
        
        g.dispose();
        
        Graphics2D g1 = (Graphics2D) c.getGraphics();
        g1.translate(getCanvas().getWidth() * 0.5, getCanvas().getHeight() * 0.5);
        
        int s;
        if (getCanvas().getWidth() < getCanvas().getHeight()) {
            s = getCanvas().getWidth();
        } else s = getCanvas().getHeight();
        s /= gameSize;
        
        g1.scale(s,s);
        g1.drawImage((Image) buff, (int)(gameSize * 0.5) * -1, (int)(gameSize * 0.5) * -1, null);
        g1.dispose();
    }
    public Canvas getCanvas() {
        return this.c;
    }
    public void NOWS_YOUR_CHANCE_TO_BE_A_BIG_SHOT() {
        if (fullscreen == false) {
            main.prevWindowSizes = new int[] {getSize().width,getSize().height,getLocation().x,getLocation().y};
            GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice d = g.getDefaultScreenDevice();
            dispose();
            setUndecorated(true);
            setVisible(true);
            d.setFullScreenWindow(this);
            fullscreen = true;
        } else if (fullscreen == true) {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            dispose();
            setUndecorated(false);
            setVisible(true);
            //setSize(512, 512);
            //setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
            setSize(main.prevWindowSizes[0], main.prevWindowSizes[1]);
            setLocation(main.prevWindowSizes[2], main.prevWindowSizes[3]);
            fullscreen = false;
        }
    }
    
    public static void drawHealthBar(Graphics2D g, int x, int y, int width, double current, double max, Color bar, Color back) {
        int w = Math.max((int)Math.round((current / max) * (double)width), (current > 0 ? 1 : 0));
        g.setColor(back);
        g.drawRect(x,y,width - 1,0);
        g.setColor(bar);
        g.drawRect(x,y,Math.min(w, width) - 1,0);
    }
    
    static int getCharWidth(String letter) {
        String c = main.letters;
        String w = "44444444444444444444444444344444444422422224343333333443444";
        try {
            String out = w.charAt(c.indexOf(letter.toUpperCase())) + "";
            return Integer.parseInt(out);
        } catch(Exception e) {}
        return 3;
    }
    static int getTextWidth(String text) {
        int w = 0;
        int E_001 = text.length();
        for (int i = 0; i < E_001; i++) w += getCharWidth(text.charAt(i) + "");
        return w - 1;
    }
    static void renderText(Graphics2D g, String text, int x, int y) {
        text = text.toUpperCase();
        int E_001 = text.length();
        for (int i = 0; i < E_001; i++) {
            StringBuilder s = new StringBuilder("text.");
            s.append(text.charAt(i));
            g.drawImage(main.art.get(s.toString()), x, y, null);
            x += getCharWidth(text.charAt(i) + "");
        }
    }
    
    
    
    
    
}