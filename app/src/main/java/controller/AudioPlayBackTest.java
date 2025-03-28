package controller;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.*;

import dsp.MediaPlayer;
import dsp.Effects.Delay;

public class AudioPlayBackTest {
    private MediaPlayer mediaPlayer;

    public AudioPlayBackTest() {
        mediaPlayer = new MediaPlayer();
    }

    public void setUp() {
        try {
            // Set up a test song
            mediaPlayer.setSong("test.wav");
            
            // Add a delay effect for testing
            Delay delayEffect = new Delay(0.2, 0.5, 44100); // 200ms delay, 50% decay, 44.1kHz sample rate
            mediaPlayer.setEffect(delayEffect);
            
            // Set initial volume to 50%
            mediaPlayer.setVolume(50);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.playAudio();
        }
    }

    public static void main(String[] args) {
        AudioPlayBackTest test = new AudioPlayBackTest();
        test.setUp();
        test.playAudio();
    }
}
