package controller;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;

public class AudioPlayBackTest {
    private Clip clip;

    public AudioPlayBackTest() {
        setUp();
    }

    public void setUp() {
        try (InputStream audioStream = getClass().getClassLoader().getResourceAsStream("test.wav")) {
            if (audioStream == null) {
                throw new IllegalArgumentException("Resource test.wav not found.");
            }

            // Create an audio input stream from the resource stream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (clip != null) {
            clip.start();
        }
    }
}
