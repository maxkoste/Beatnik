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
            
            // Set initial effect mix to 0 (dry only)
            mediaPlayer.setEffectMix(0.0f);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.playAudio();
        }
    }

    public void setEffectMix(float mix) {
        if (mediaPlayer != null) {
            mediaPlayer.setEffectMix(mix);
        }
    }

    public static void main(String[] args) {
        AudioPlayBackTest test = new AudioPlayBackTest();
        test.setUp();
        test.playAudio();
        
        // After 2 seconds, gradually increase the effect mix
        try {
            Thread.sleep(2000);
            for (float mix = 0.0f; mix <= 1.0f; mix += 0.1f) {
                test.setEffectMix(mix);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
