package dsp;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//This class is responsible for playing the audio.
public class AudioPlayer {

        private Clip clip;
        
        public AudioPlayer(){
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

    //This method is called when you want to play the audio.
    public void playAudio() {
        if (clip != null) {
            clip.start();
        }
    }
}
