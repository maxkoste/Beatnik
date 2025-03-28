package dsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
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
    private Clip clip; // For main playback not used in this implementation
    private AudioDispatcher effectDispatcher; // For effects processing
    private GainProcessor gainProcessor;
    private String currentSongFilePath;
    private GainProcessor volumeProcessor;
    private EffectChain effectChain;
    private float effectMix = 0.0f; // 0 = dry only, 1 = wet only
    private FloatControl gainVolumeProcessor;

    public MediaPlayer() {
        effectChain = new EffectChain();
    }

    public void setUp() {
        try {
            // Clean up previous resources if they exist
            if (effectDispatcher != null) {
                effectDispatcher.stop();
                effectDispatcher = null;
            }

            System.out.println("Loading audio file: songs/" + currentSongFilePath);

            InputStream mainStream = getClass().getClassLoader()
                    .getResourceAsStream("songs/" + currentSongFilePath);
            if (mainStream == null) {
                System.err.println("Error: Could not load audio file!");
                return;
            }

            AudioInputStream mainAudioStream = AudioSystem.getAudioInputStream(mainStream);
            AudioFormat format = mainAudioStream.getFormat();

            System.out.println("Audio format: " + format.toString());

            // Create dispatcher with larger buffer size
            effectDispatcher = new AudioDispatcher(
                    new JVMAudioInputStream(mainAudioStream), 8192, 0);

            // Add volume control first - set initial volume to 1.0 (100%)
            volumeProcessor = new GainProcessor(1.0f);
            effectDispatcher.addAudioProcessor(volumeProcessor);

            // Add effect chain processor
            effectDispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    effectChain.process(audioEvent.getFloatBuffer());
                    return true;
                }

                @Override
                public void processingFinished() {
                    System.out.println("Processing finished");
                }
            });

            // Add audio player for final output
            AudioPlayer audioPlayer = new AudioPlayer(format);
            effectDispatcher.addAudioProcessor(audioPlayer);

            System.out.println("Audio setup completed successfully");

        } catch (Exception e) {
            System.err.println("Error setting up audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (effectDispatcher == null) {
            System.err.println("Error: AudioDispatcher is not initialized!");
            return;
        }
        System.out.println("Starting audio playback...");
        Thread audioThread = new Thread(effectDispatcher, "Audio Playback Thread");
        audioThread.setPriority(Thread.MAX_PRIORITY);  // Give audio thread high priority
        audioThread.start();
    }

    public void setVolume(float volume) {
        if (volumeProcessor != null) {
            // Convert volume percentage (0-100) to gain multiplier (0.0-1.0)
            float gain = volume / 100.0f;
            volumeProcessor.setGain(gain);
            System.out.println("Setting volume to " + volume + " (gain: " + gain + ")");
        }
    }

    public void setEffectMix(float mix) { // 0.0f to 1.0f
        this.effectMix = mix;
        if (gainProcessor != null) {
            gainProcessor.setGain(mix);
        }
    }

    public void setSong(String filepath) {
        this.currentSongFilePath = filepath;
        setUp();
        setVolume(100.0f); // Set initial volume to maximum
    }

    public void setEffect(dsp.Effects.AudioEffect effect) {
        effectChain.setEffect(effect);
    }
}
