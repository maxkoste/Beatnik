package dsp;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.GainProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.resample.RateTransposer;
import dsp.Effects.Delay;
import dsp.Effects.Flanger;
import controller.Controller;

import javax.sound.sampled.*;

//This class is responsible for playing the audio, and its volume
public class TestMediaPlayer {
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
    private CustomAudioPlayer masterPlayer;
    private CustomAudioPlayer cuePlayer;
    private Mixer masterMixer;
    private Mixer cueMixer;
    private boolean cueEnabled = false; // Om cue är aktiv för denna kanal
    private GainProcessor cueVolumeProcessor;
    private Controller controller;


    public TestMediaPlayer(Mixer masterMixer, Mixer cueMixer) {
        this.masterMixer = masterMixer;
        this.cueMixer = cueMixer;
    }

    public void setUp() {
        try {
            // Clean up previous resources if they exist
            if (playbackDispatcher != null) {
                playbackDispatcher.stop();
                playbackDispatcher = null;
            }

            /**
             * TODO: Stereo playback
             *
             * For some stupid reason AudioDispatchFactory.fromPipe() creates a mono audio
             * stream
             * which in turn results in all audio playback being in mono instead of stereo.
             *
             * Alternative is to create a audio file, File audioFile = new File(fullPath);
             * and pass it to the
             * AudioDispatcherFactory with: AudioDispatcherFactory.fromFile(audioFile, 2048,
             * 0);
             * This will give us stereo audio, BUT for some reason the audio playback is
             * laggy and
             * suuuper weird when doing this.. this needs to be fixed!!
             */

            // File audioFile = new File(fullPath);
            // playbackDispatcher = AudioDispatcherFactory.fromFile(audioFile, 4096, 0);

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
            // Add volume controll first
            volumeProcessor = new GainProcessor(0.0f);
            playbackDispatcher.addAudioProcessor(volumeProcessor);

            // Add effects-processing
            playbackDispatcher.addAudioProcessor(delayEffect);
            playbackDispatcher.addAudioProcessor(flangerEffect);
            playbackDispatcher.addAudioProcessor(bassEqualizer);
            playbackDispatcher.addAudioProcessor(trebleEqualizer);
            playbackDispatcher.addAudioProcessor(rateTransposer);


            // === HUVUDUTGÅNG: MASTER ===
            masterPlayer = new CustomAudioPlayer(masterMixer, format);

            cueVolumeProcessor = new GainProcessor(0.5f);

            // === CUE-UTGÅNG: HÖRLURAR ===
            cuePlayer = new CustomAudioPlayer(cueMixer, format);
            playbackDispatcher.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    if (!isPlaying) {
                        synchronized (lock) {
                            try {
                                lock.wait();
                            } catch (InterruptedException e) {
                                return false;
                            }
                        }
                    }
                    byte[] originalBuffer = audioEvent.getByteBuffer();



                    masterPlayer.write(originalBuffer);


                    if (cueEnabled) {
                        byte[] copiedBuffer = new byte[originalBuffer.length];
                        System.arraycopy(originalBuffer, 0, copiedBuffer, 0, originalBuffer.length);

                        cuePlayer.write(copiedBuffer);
                    }

                    return true;
                }

                @Override
                public void processingFinished() {
                    masterPlayer.processingFinished();
                    cuePlayer.processingFinished();
                }
            });

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
        System.out.println("Audio Shutdown Complete");
    }

    public void playPause() throws InterruptedException {
        if (!started) {
            this.audioThread = new Thread(playbackDispatcher, "Playback Thread");
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

    public void setPlaybackSpeed(double speedFactor) {
        rateTransposer.setFactor(speedFactor);
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
        this.fullPath = "src/main/resources/songs/" + filepath;
        this.started = false;
        this.isPlaying = true;
        setUp();
    }

    public void resetSong() {
        started = false;
        isPlaying = true;
        setUp();
    }

    public void setCueEnabled(boolean enabled) {
        this.cueEnabled = enabled;
    }

    public boolean isCueEnabled() {
        return cueEnabled;
    }

    public void setCueVolume(float volume) {
        if (cueVolumeProcessor != null) {
            float gain = volume / 100.0f;
            System.out.println("Setting cue gain to: " + gain);
            cueVolumeProcessor.setGain(gain);
        }
    }
}