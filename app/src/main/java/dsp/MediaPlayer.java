package dsp;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.effects.FlangerEffect;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.AudioPlayer;
import be.tarsos.dsp.resample.RateTransposer;
import controller.Controller;
import dsp.Effects.Delay;
import dsp.Effects.Flanger;
//Custom Dispatcher factory might be part of a solution for stereo
//audio
import dsp.util.DispatcherFactory;

import java.io.File;

import javax.sound.sampled.*;

//This class is responsible for playing the audio, and its volume
public class MediaPlayer {
    private AudioDispatcher playbackDispatcher; // For effects processing
    private GainProcessor volumeProcessor;
    private Equalizer bassEqualizer;
    private Equalizer trebleEqualizer;
    private boolean isPlaying;
    private boolean started;
    private Delay delayEffect;
    private Flanger flangerEffect;
    private String fullPath;
    private Thread audioThread;
    private final Object lock = new Object();
    private RateTransposer rateTransposer;
    private LowPassEqualizer lowPassFilter;
    private Controller controller;
    private int channel;

    private float smoothedFrequency = -1; // -1 indicates not initialized
    private final float smoothingFactor = 0.1f; // Smaller = smoother

    public MediaPlayer(Controller controller, int channel) {
        this.controller = controller;
        this.channel = channel;
    }

    public void setUp() {
        try {
            // Clean up previous resources if they exist
            if (playbackDispatcher != null) {
                playbackDispatcher.stop();
                playbackDispatcher = null;
            }
            playbackDispatcher = AudioDispatcherFactory.fromPipe(fullPath,
                    48000, 4096, 0);
            TarsosDSPAudioFormat format = playbackDispatcher.getFormat();
            System.out.println(format.toString());

            bassEqualizer = new Equalizer(format.getSampleRate(),
                    80, 80); // 80Hz center, 50Hz bandwidth
            flangerEffect = new Flanger(0.0002,
                    0, format.getSampleRate(), 3);
            trebleEqualizer = new Equalizer(format.getSampleRate(),
                    5000, 7000); // 7khz center, 5kHz bandwidth
            delayEffect = new Delay(0.5, 0.6, format.getSampleRate());
            rateTransposer = new RateTransposer(1.0F);
            lowPassFilter = new LowPassEqualizer(20000f, format.getSampleRate());

            // Add volume controll first
            volumeProcessor = new GainProcessor(0.0f);
            playbackDispatcher.addAudioProcessor(volumeProcessor);

            // Add effects-processing
            playbackDispatcher.addAudioProcessor(delayEffect);
            playbackDispatcher.addAudioProcessor(flangerEffect);
            playbackDispatcher.addAudioProcessor(bassEqualizer);
            playbackDispatcher.addAudioProcessor(trebleEqualizer);
            playbackDispatcher.addAudioProcessor(rateTransposer);
            playbackDispatcher.addAudioProcessor(lowPassFilter);
            playbackDispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    if (!isPlaying) {
                        synchronized (lock) {
                            try {
                                lock.wait(); // wait until resume is called
                            } catch (InterruptedException e) {
                                return false; // stop processing if interrupted
                            }
                        }
                    }
                    return true; // continue processing
                }

                @Override
                public void processingFinished() {
                    if (started) { // If the song finished playing naturally, play the next song.
                        controller.nextSong(channel);
                    }
                }
            });

            // Add audio player for final output
            AudioPlayer audioPlayer = new AudioPlayer(format);
            playbackDispatcher.addAudioProcessor(audioPlayer);

        } catch (Exception e) {
            System.err.println("Error setting up audio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Closing stream and stopping any playback of audio
     */
    public void shutDown() {
        started = false;
        if (playbackDispatcher != null) {
            System.out.println("Shutting down audioDispatcher");
            playbackDispatcher.stop();
            playbackDispatcher = null;
        }
        isPlaying = false;
        System.out.println("Audio Shutdown Complete");
    }

    public void playPause() throws InterruptedException {
        if (!started) {
            this.audioThread = new Thread(playbackDispatcher, "Playback Thread");
            audioThread.setDaemon(true);
            audioThread.setPriority(Thread.MAX_PRIORITY);
            audioThread.start();
            started = true;
            isPlaying = true;
        } else if (isPlaying) {
            synchronized (lock) {
                isPlaying = false;
            }
        } else {
            synchronized (lock) {
                isPlaying = true;
                lock.notifyAll();
            }
        }
    }

    /**
     * Helper method to map the value 0.0 - 1.0 to 20 000 hz-70hz
     * 
     * @param normalizedValue
     * @return
     */
    public float mapNormalizedToFrequency(float normalizedValue) {
        normalizedValue = Math.max(0.0f, Math.min(1.0f, normalizedValue));

        float minFreq = 70.0f;
        float maxFreq = 20000.0f;

        double minLog = Math.log10(minFreq);
        double maxLog = Math.log10(maxFreq);

        // Shape the input to make upper values less sensitive
        float shaped = normalizedValue * normalizedValue;

        double logFreq = maxLog - shaped * (maxLog - minLog);
        return (float) Math.pow(10, logFreq);
    }

    public void setPlaybackSpeed(double speedFactor) {
        if (rateTransposer != null) {
            rateTransposer.setFactor(speedFactor);
        }
    }

    public void setVolume(float volume) {
        if (volumeProcessor != null) {
            // Convert volume percentage (0-100) to gain multiplier (0.0-1.0)
            float gain = volume / 100.0f;
            volumeProcessor.setGain(gain);
        }
    }

    /**
     * Takes the mix value between 0.0 and 1.0 from the effect-selector
     * passes it to a helper function which maps it to a frequency and
     * applies it to the filter
     * Smoothes out the jumps in frequency if the user is turning the knob 
     * aggressivly or making big jumps. This is to avoid harsh noises 
     * or clicks that are an effect of big jumps near the lower end of the 
     * frequency spectrum.
     * 
     * @param frequency
     */
    public void setFilterFrequency(float frequency) {
        if (lowPassFilter != null) {
            float targetFreq = mapNormalizedToFrequency(frequency);

            if (smoothedFrequency < 0) {
                smoothedFrequency = targetFreq; // initialize on first call
            } else {
                // Apply exponential smoothing
                smoothedFrequency += smoothingFactor * (targetFreq - smoothedFrequency);
            }

            lowPassFilter.setFrequency(smoothedFrequency);
        }
    }

    public void setTreble(float trebleGain) {
        if (trebleEqualizer != null) {
            trebleEqualizer.setGain(trebleGain);
        }
    }

    public void setBass(float bassGain) {
        if (bassEqualizer != null) {
            bassEqualizer.setGain(bassGain);
        }
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
        if (playbackDispatcher != null) {
            return playbackDispatcher;
        } else
            return null;
    }

    /**
     * @param filepath filepath to the audio that will be loaded into the playback
     *                 dispatcher
     */
    public void setSong(String filepath) {
        this.fullPath = "src/main/resources/songs/" + filepath;
        this.started = false;
        this.isPlaying = true;
        setUp();
    }

    public void resetSong() {
        if (playbackDispatcher != null) {
            started = false;
            isPlaying = true;
            setUp();
        }
    }
}
