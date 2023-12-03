package core;

public class keybind {
    private String key;
    private boolean down;
    public keybind(String k) {
        this.key = k;
        this.down = false;
    }
    public void updatePressed(String k) {
        if (this.key.toUpperCase().equals(k.toUpperCase())) {this.down = true;}
    }
    public void updateUnpressed(String k) {
        if (this.key.toUpperCase().equals(k.toUpperCase())) {this.down = false;}
    }
    public String getKeybind() {
        return this.key;
    }
    public void changeKeybind(String k) {
        this.key = k;
    }
    public boolean pressed() {
        return this.down;
    }
}