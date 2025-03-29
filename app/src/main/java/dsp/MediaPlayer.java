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
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.io.jvm.JVMAudioInputStream;

//This class is responsible for playing the audio, and its volume
public class MediaPlayer {
    private Clip clip; // For main playback not used in this implementation
    private AudioDispatcher playbackDispatcher; // For effects processing
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
            if (playbackDispatcher != null) {
                playbackDispatcher.stop();
                playbackDispatcher = null;
            }

            System.out.println("Sampling this file in the: songs/" + currentSongFilePath);

            // Get the resource URL and convert it to a file path
            java.net.URL resourceUrl = getClass().getClassLoader().getResource("songs/" + currentSongFilePath);
            if (resourceUrl == null) {
                System.err.println("Error: Could not find audio file in resources!");
                return;
            }
            
            String filePath = new File(resourceUrl.toURI()).getAbsolutePath();
            System.out.println("Loading audio file from: " + filePath);

            // Use AudioDispatcherFactory with the actual file path
            playbackDispatcher = AudioDispatcherFactory.fromPipe(filePath, 44100, 4096, 0);
            TarsosDSPAudioFormat format = playbackDispatcher.getFormat();
            System.out.println("Audio format: " + format.toString());

            // Add volume control first - set initial volume to 1.0 (100%)
            volumeProcessor = new GainProcessor(1.0f);
            playbackDispatcher.addAudioProcessor(volumeProcessor);

            // Add effect chain processor
            playbackDispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    effectChain.process(audioEvent.getFloatBuffer());
                    return true;
                }

                @Override
                public void processingFinished() {
                    System.out.println("Processing finished..");
                }
            });

            // Add audio player for final output
            AudioPlayer audioPlayer = new AudioPlayer(format);
            playbackDispatcher.addAudioProcessor(audioPlayer);

            System.out.println("Setup complete..");

        } catch (Exception e) {
            System.err.println("Error setting up audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (playbackDispatcher == null) {
            System.err.println("Error could not play audio...");
            return;
        }
        System.out.println("Playing..");
        Thread audioThread = new Thread(playbackDispatcher, "Audio Playback thread");
        audioThread.setPriority(Thread.MAX_PRIORITY);  // Give audio thread high priority
        audioThread.start();
    }

    public void setVolume(float volume) {
        if (volumeProcessor != null) {
            // Convert volume percentage (0-100) to gain multiplier (0.0-1.0)
            float gain = volume / 100.0f;
            volumeProcessor.setGain(gain);
            System.out.println("Setting the volume to " + volume + " (gain: " + gain + ")");
        }
    }
    
    // Set the effect mix between 0.0f and 1.0f, 0.0f is dry only, 1.0f is wet only
    // 0.5f is equal mix of dry and wet
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
