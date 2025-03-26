package controller;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

public class AudioPlayBackTest {
    private File file;
    private Clip clip;

    public AudioPlayBackTest() {
        setUp();
    }

    public void setUp() {

        File file = new File("test.wav");
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playAudio(){
        if (clip != null){
            clip.start();
        }
    }
}
