package dsp;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.effects.DelayEffect;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

public class BeatnikAudioPlayer {
    private String currentSongFilePath;
    private AudioDispatcher dispatcher;
    private GainProcessor inputGain;

    private final int defaultInputGain = 100; // %
    private final int defaultDelay = 200; // ms
    private final int defaultDecay = 50; // %

    public BeatnikAudioPlayer() {
        inputGain = new GainProcessor(defaultInputGain / 100.0f);
    }

    public void setUp() {
        // Clean up previous disaster
        if (dispatcher != null) {
            dispatcher.stop();
            dispatcher = null;
        }

        //Not using Clips since we want to rely on TarsosDSP instead to see if that works better with effects. 
        //But not working right now
        try (InputStream audioStream = getClass().getClassLoader()
                .getResourceAsStream("songs/" + currentSongFilePath)) {
            if (audioStream == null) {
                throw new IllegalArgumentException("Song not found: " + currentSongFilePath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            AudioFormat originalFormat = audioInputStream.getFormat();

            TarsosDSPAudioInputStream audioDSPStream = new JVMAudioInputStream(audioInputStream);
            int bufferSize = 1024;
            int overlap = 0;

            dispatcher = new AudioDispatcher(audioDSPStream, bufferSize, overlap);

            DelayEffect delayEffect = new DelayEffect(
                    defaultDelay / 1000.0,
                    defaultDecay / 100.0,
                    originalFormat.getSampleRate());

            inputGain.setGain(defaultInputGain / 100.0f);

            dispatcher.addAudioProcessor(inputGain);
            dispatcher.addAudioProcessor(delayEffect);
            dispatcher.addAudioProcessor(new AudioPlayer(originalFormat));

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (dispatcher == null) {
            setUp();
        }

        if (!dispatcher.isStopped()) {
            dispatcher.stop();
        }

        // Create new thread with current dispatcher
        new Thread(dispatcher, "Audio Dispatching").start();
    }

    public void setSong(String filepath) {
        this.currentSongFilePath = filepath;
        setUp(); // Reinitialize with new song???
    }

    public void setVolume(float volumePercent) {
        if (inputGain != null) {
            inputGain.setGain(volumePercent / 100.0f);
        }
    }
}