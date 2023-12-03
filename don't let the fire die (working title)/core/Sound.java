package core;

import java.net.URL;
import javax.sound.sampled.*;

public class Sound {
    URL url;
    public Clip clip;
    public Sound(String sound) throws Exception {
        url = this.getClass().getClassLoader().getResource("assets/audio/" + sound + ".wav");
        clip = AudioSystem.getClip();
        
        AudioInputStream ais = AudioSystem.getAudioInputStream(url);
        clip.open(ais);
    }
    public void Start() {clip.start();}
    public void Stop() {clip.stop();}
    public void Loop() {clip.loop(Clip.LOOP_CONTINUOUSLY);}
    public void Reset() {
        clip.stop();
        clip.setMicrosecondPosition(0);
    }
    public float getVolume() {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        return (float) Math.pow(10f, gainControl.getValue() / 20f);
    }
    public void setVolume(float volume) {
        if (volume < 0f) volume = 0f;
        if (volume > 1f) volume = 1f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(volume));
    }
    public float getPanning() {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
        return (float) gainControl.getValue();
    }
    public void setPanning(float panning) {
        if (panning < -1f) panning = -1f;
        if (panning > 1f) panning = 1f;
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
        gainControl.setValue(panning);
    }
    public boolean canRemove() {
        if (clip.getMicrosecondPosition() == clip.getMicrosecondLength()) {
            this.Stop();
            return true;
        }
        return false;
    }
}