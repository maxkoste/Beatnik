package dsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
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
    private Clip clip; // For main playback
    private AudioDispatcher effectDispatcher; // For effects processing
    private GainProcessor gainProcessor;
    private String currentSongFilePath;
    private EffectChain effectChain;
    private boolean isPlaying;
    private float effectMix = 0.0f; // 0 = dry only, 1 = wet only

    public MediaPlayer() {
        effectChain = new EffectChain();
        isPlaying = false;
    }

    public void setUp() {
        try {
            // Clean up previous resources if they exist
            if (clip != null) {
                clip.close();
                clip = null;
            }
            if (effectDispatcher != null) {
                effectDispatcher.stop();
                effectDispatcher = null;
            }

            // Set up main Clip playback
            InputStream mainStream = getClass().getClassLoader()
                    .getResourceAsStream("songs/" + currentSongFilePath);
            AudioInputStream mainAudioStream = AudioSystem.getAudioInputStream(mainStream);
            AudioFormat format = mainAudioStream.getFormat();

            clip = AudioSystem.getClip();
            clip.open(mainAudioStream);

            // Set up parallel effects processing with a fresh stream
            InputStream effectStream = getClass().getClassLoader()
                    .getResourceAsStream("songs/" + currentSongFilePath);
            AudioInputStream effectAudioStream = AudioSystem.getAudioInputStream(effectStream);
            effectDispatcher = new AudioDispatcher(
                    new JVMAudioInputStream(effectAudioStream), 1024, 0);

            // Add effect chain processor
            effectDispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    effectChain.process(audioEvent.getFloatBuffer());
                    return true;
                }

                @Override
                public void processingFinished() {
                }
            });

            // Add gain processor for effect mix
            gainProcessor = new GainProcessor(1.0f); // Start at 50%
            effectDispatcher.addAudioProcessor(gainProcessor);

            // Add audio player for effects output
            effectDispatcher.addAudioProcessor(new AudioPlayer(format));

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        if (!isPlaying) {
            if (clip == null) {
                setUp();
            }
            clip.start();
            try {
                new Thread(effectDispatcher, "Effects Processing").start();
                isPlaying = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            clip.stop();
            effectDispatcher.stop();
            isPlaying = false;
        }
    }

    public void setVolume(float volume) {
        if (clip != null) {
            // Convert percentage (0-100) to gain (0-1)
            float gain = volume / 100.0f;
            // TODO: Implement clip volume control
            System.out.println("Setting volume to " + volume);
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
    }

    public void setEffect(dsp.Effects.AudioEffect effect) {
        effectChain.setEffect(effect);
    }
}
