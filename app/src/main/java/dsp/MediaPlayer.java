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
    private Clip clip; // For main playback
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

            effectDispatcher = new AudioDispatcher(
                    new JVMAudioInputStream(mainAudioStream), 1024, 0);
            volumeProcessor = new GainProcessor(1.0f);

            gainVolumeProcessor = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
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
        if (clip != null) {
            if (!clip.isActive()) {
                clip.start();
            } else
                clip.stop();
        }
    }

    public void setVolume(float volume) {
        if (clip != null) {
            // Convert percentage (0-100) to gain (-80.0 to 6.0 dB)
            if (gainVolumeProcessor != null) {
                
                float minGain = gainVolumeProcessor.getMinimum(); // Typically -80 dB
                float maxGain = gainVolumeProcessor.getMaximum(); // Typically 6 dB
        
                // Map slider range (0-100) to dB range (minGain to maxGain)
                float gain = (float) ((volume / 100.0) * (maxGain - minGain) + minGain);

                gainVolumeProcessor.setValue(gain);
                System.out.println("Setting volume to " + volume);
            }
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
