package core;

import java.awt.event.*;
import java.util.HashMap;

public class keyboardInput implements KeyListener {
    public static main.Key changingKeybind = null;
    public static boolean allowKeybindChange = false;
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        //System.out.println(e.getKeyText(e.getKeyCode()));
        if (changingKeybind == null) for (keybind i : main.keys.values()) {
            if (i instanceof keybind) {i.updatePressed(e.getKeyText(e.getKeyCode()));}
        }
        if (changingKeybind != null) {
            for (keybind i : main.keys.values()) if (i instanceof keybind && i.getKeybind().equals(e.getKeyText(e.getKeyCode())))
            i.changeKeybind(main.keys.get(changingKeybind).getKeybind());
            main.keys.get(changingKeybind).changeKeybind(e.getKeyText(e.getKeyCode()));
            changingKeybind = null;
            //main.menus.Settings(true); // IN CASE YOU FORGET, THIS LINE OF CODE WAS USED TO REFRESH THE SETTINGS MENU!
        }
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println(e.getKeyText(e.getKeyCode()) instanceof String);
        for (keybind i : main.keys.values()) {
            if (i instanceof keybind) i.updateUnpressed(e.getKeyText(e.getKeyCode()));
        }
        allowKeybindChange = true;
        for (keybind i : main.keys.values()) if (i.pressed()) allowKeybindChange = false;
    }
}
