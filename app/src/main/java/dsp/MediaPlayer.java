package dsp;

import java.io.File;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import dsp.Effects.Delay;
import dsp.Effects.Flanger;

//This class is responsible for playing the audio, and its volume
public class MediaPlayer {
    private AudioDispatcher playbackDispatcher; // For effects processing
    private GainProcessor gainProcessor;
    private String currentSongFilePath;
    private GainProcessor volumeProcessor;
    private Equalizer bassEqualizer;
    private Equalizer trebleEqualizer;
    private boolean isPlaying;
    private float currentTime;
    private Delay delayEffect;
    private Flanger flangerEffect;

    public MediaPlayer() {
        // Initialize equalizers with wide bandwidths to simulate shelf behavior
        bassEqualizer = new Equalizer(44100, 80, 80); // 80Hz center, 50Hz bandwidth
        flangerEffect = new Flanger(0.0002, 0, 44100, 3);
        trebleEqualizer = new Equalizer(44100, 5000, 7000); // 7khz center, 5kHz bandwidth
        delayEffect = new Delay(0.5, 0.6, 44100);
    }

    public void setUp() {
        try {
            // Clean up previous resources if they exist
            if (playbackDispatcher != null) {
                playbackDispatcher.stop();
                playbackDispatcher = null;
            }

            System.out.println("Sampling this file in the: songs/" + currentSongFilePath);

            String filePath = new File("src/main/resources/songs/" + currentSongFilePath).getAbsolutePath();
            System.out.println("Loading audio file from: " + filePath);

            // Use AudioDispatcherFactory with the actual file path
            playbackDispatcher = AudioDispatcherFactory.fromPipe(filePath, 44100, 4096, 0);
            TarsosDSPAudioFormat format = playbackDispatcher.getFormat();
            System.out.println("Audio format: " + format.toString());

            // Add volume controll first
            volumeProcessor = new GainProcessor(0.0f);
            playbackDispatcher.addAudioProcessor(volumeProcessor);

            // Add effects-processing
            playbackDispatcher.addAudioProcessor(delayEffect);
            playbackDispatcher.addAudioProcessor(flangerEffect);
            playbackDispatcher.addAudioProcessor(bassEqualizer);
            playbackDispatcher.addAudioProcessor(trebleEqualizer);

            // Add audio player for final output
            AudioPlayer audioPlayer = new AudioPlayer(format);
            playbackDispatcher.addAudioProcessor(audioPlayer);

            System.out.println("Setup complete..");

        } catch (Exception e) {
            System.err.println("Error setting up audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closing stream and stopping any playback of audio
     */
    public void shutDown() {
        if (playbackDispatcher != null) {
            System.out.println("Shutting down audioDispatcher");
            playbackDispatcher.stop();
            playbackDispatcher = null;
        }
        isPlaying = false;
        currentTime = 0;
        System.out.println("Audio Shutdown Complete");
    }

    // plays the song from the MediaPlayer class
    public void playAudio() {
        if (!isPlaying) {
            setUp();
            System.out.println("Playing..");
            playbackDispatcher.skip(currentTime);
            Thread audioThread = new Thread(playbackDispatcher, "Playback thread");
            audioThread.setPriority(Thread.MAX_PRIORITY); // Give audio thread high priority
            audioThread.start();
            isPlaying = true;
        } else {
            isPlaying = !isPlaying;
            System.out.println("Stopping..");
            currentTime = playbackDispatcher.secondsProcessed();
            playbackDispatcher.stop();
        }
    }

    public void setVolume(float volume) {
        if (volumeProcessor != null) {
            // Convert volume percentage (0-100) to gain multiplier (0.0-1.0)
            float gain = volume / 100.0f;
            volumeProcessor.setGain(gain);
        }
    }

    public void setTreble(float trebleGain) {
        trebleEqualizer.setGain(trebleGain);
    }

    public void setBass(float bassGain) {
        bassEqualizer.setGain(bassGain);
    }

    /**
     * set the effect-mix of the delay
     * needs to be a value between 0.0-1.0f
     * When mix = 0 100% dry signal
     * when mix = 1 100% wet signal
     * when mix = 0.5 50% wet 50% dry
     * 
     * @param mix
     */
    public void setDelayEffectMix(float mix) { // 0.0f to 1.0f
        if (delayEffect != null) {
            delayEffect.setMix(mix);
        }
    }

    /**
     * Same as delay...
     * 
     * @param mix
     */
    public void setFlangerEffectMix(float mix) {
        if (flangerEffect != null) {
            flangerEffect.setWet(mix);
        }
    }

    /**
     * @return the audio dispatcher responsible for playing and processing the
     *         audio.
     */
    public AudioDispatcher getAudioDispatcher() {
        return playbackDispatcher;
    }

    /**
     * @param filepath filepath to the audio that will be loaded into the playback
     *                 dispatcher
     */
    public void setSong(String filepath) {
        this.isPlaying = false;
        this.currentSongFilePath = filepath;
        this.currentTime = 0;
    }

    public void resetSong() {
        System.out.println("Resetting");
        currentTime = 0;
        isPlaying = false;
        playAudio();
        playAudio();
    }
}
