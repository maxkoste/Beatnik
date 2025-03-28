package dsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

//This class is responsible for playing the audio, and its volume
public class MediaPlayer {
    private AudioDispatcher dispatcher;
    private GainProcessor gainProcessor;
    private String currentSongFilePath;
    private EffectChain effectChain;
    private boolean isPlaying;

    public MediaPlayer() {
        effectChain = new EffectChain();
        isPlaying = false;
    }

    public void setUp() {
        // Clean up previous dispatcher if it exists
        if (dispatcher != null) {
            dispatcher.stop();
            dispatcher = null;
        }

        try (InputStream audioStream = getClass().getClassLoader()
                .getResourceAsStream("songs/" + currentSongFilePath)) {
            if (audioStream == null) {
                throw new IllegalArgumentException("Song not found: " + currentSongFilePath);
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            AudioFormat originalFormat = audioInputStream.getFormat();

            // Create TarsosDSP audio stream
            TarsosDSPAudioInputStream audioDSPStream = new JVMAudioInputStream(audioInputStream);
            
            // Configure dispatcher with buffer size and overlap
            int bufferSize = 1024;
            int overlap = 0;
            dispatcher = new AudioDispatcher(audioDSPStream, bufferSize, overlap);

            // Set up gain processor for volume control
            gainProcessor = new GainProcessor(0.5f); // Start at 50% volume
            dispatcher.addAudioProcessor(gainProcessor);

            // Add effect chain processor
            dispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                System.out.print("Adding audio processor!");
                public boolean process(AudioEvent audioEvent) {
                    effectChain.process(audioEvent.getFloatBuffer());
                    return true;
                }

                @Override
                public void processingFinished() {
                    // Nothing to do here
                }
            });

            // Add audio player as final processor
            dispatcher.addAudioProcessor(new AudioPlayer(originalFormat));

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume) {
        if (gainProcessor != null) {
            // Convert percentage (0-100) to gain (0-1)
            float gain = volume / 100.0f;
            gainProcessor.setGain(gain);
        }
    }

    public void playAudio() {
        if (dispatcher == null) {
            setUp();
        }

        if (!isPlaying) {
            new Thread(dispatcher, "Audio Dispatching").start();
            isPlaying = true;
        } else {
            dispatcher.stop();
            isPlaying = false;
        }
    }

    public void setSong(String filepath) {
        this.currentSongFilePath = filepath;
        setUp();
    }

    public void setEffect(dsp.Effects.AudioEffect effect) {
        effectChain.setEffect(effect);
    }
}
