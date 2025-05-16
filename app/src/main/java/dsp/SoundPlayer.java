package dsp;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundPlayer {

    private String filePath;

    public SoundPlayer(String sourceAudio) {
        this.filePath = sourceAudio;
    }

    public void play() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip player = AudioSystem.getClip();
            player.open(audioStream);
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
