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

	private CustomAudioPlayer masterPlayer;
	private CustomAudioPlayer cuePlayer;
	private Mixer masterMixer;
	private Mixer cueMixer;
	private boolean cueEnabled = false;
	private GainProcessor cueVolumeProcessor;
	private Controller controller;
	private int channel;

	private float smoothedFrequency = -1; // -1 indicates not initialized
	private final float smoothingFactor = 0.1f; // Smaller = smoother

	/**
	 * @param masterMixer the mixer responsible for the cue knob
	 * @param cueMixer
	 * @param controller
	 * @param channel     the channel either 1 or 2
	 */
	public MediaPlayer(Mixer masterMixer, Mixer cueMixer, Controller controller, int channel) {
		this.masterMixer = masterMixer;
		this.cueMixer = cueMixer;
		this.controller = controller;
		this.channel = channel;
	}

	/**
	 * Creates the audio dispatcher that manages audio playback of the main Channels
	 * creates the pipeline for effects handeling and gain controll,
	 * and assigns the audio processors for each effect, EQ, and gaincontroll to the
	 * audio-dispatcher.
	 */
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

			bassEqualizer = new Equalizer(format.getSampleRate(),
					50, 80); // 80Hz center, 50Hz bandwidth
			flangerEffect = new Flanger(0.0002,
					0, format.getSampleRate(), 3);
			trebleEqualizer = new Equalizer(format.getSampleRate(),
					5000, 7000); // 7khz center, 5kHz bandwidth
			delayEffect = new Delay(0.5, 0.6, format.getSampleRate());
			rateTransposer = new RateTransposer(1.0F);
			lowPassFilter = new LowPassEqualizer(20000f, format.getSampleRate());

			// Add effects-processing
			playbackDispatcher.addAudioProcessor(delayEffect);
			playbackDispatcher.addAudioProcessor(flangerEffect);
			playbackDispatcher.addAudioProcessor(bassEqualizer);
			playbackDispatcher.addAudioProcessor(trebleEqualizer);
			playbackDispatcher.addAudioProcessor(rateTransposer);
			playbackDispatcher.addAudioProcessor(lowPassFilter);

			// Add seperate GainProcessors
			volumeProcessor = new GainProcessor(1.0f);
			cueVolumeProcessor = new GainProcessor(0.5f);

			// Create the outputs
			masterPlayer = new CustomAudioPlayer(masterMixer, format);
			cuePlayer = new CustomAudioPlayer(cueMixer, format);

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
					AudioEvent cueEvent = null;
					if (cueEnabled) {
						cueEvent = makeCueEvent(audioEvent);
					}
					// Master signal
					if (!cueEnabled) {
						volumeProcessor.process(audioEvent);
						masterPlayer.write(audioEvent.getByteBuffer());
					}

					// Cue signal
					if (cueEnabled && cueEvent != null) {
						cueVolumeProcessor.process(cueEvent);
						cuePlayer.write(cueEvent.getByteBuffer());
					}

					return true; // continue processing
				}

				@Override
				public void processingFinished() {

					masterPlayer.processingFinished();
					cuePlayer.processingFinished();
					if (started) { // If the song finished playing naturally, play the next song.
						controller.nextSong(channel);
					}
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
		started = false;
		if (playbackDispatcher != null) {
			playbackDispatcher.stop();
			playbackDispatcher = null;
		}
		isPlaying = false;
	}

	/**
	 * toggles between playing and pausing the audio
	 * creates the audio thread with max priority
	 */
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
	 * Same as delay
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

	/**
	 * Replays the currently active song from the beginning.
	 */
	public void resetSong() {
		if (playbackDispatcher != null) {
			started = false;
			isPlaying = true;
			setUp();
		}
	}

	public void setCueEnabled(boolean enabled) {
		this.cueEnabled = enabled;
	}

	public boolean isCueEnabled() {
		return cueEnabled;
	}

	/**
	 * Sets the cue playback gain based on a received percentage volume.
	 * 
	 * @param volume the desired cue volume as a percentage (0–100)
	 */
	public void setCueVolume(float volume) {
		if (cueVolumeProcessor != null) {
			float gain = volume / 100.0f;
			cueVolumeProcessor.setGain(gain);
		}
	}

	/**
	 * Creates and returns a cue copy of the received AudioEvent.
	 * The Method replicates the data from the AudioEvent and
	 * the resulting event can be played independently of the original.
	 * 
	 * @param src The source AudioEvent to be duplicated
	 * @return Copied audio event
	 *
	 */
	private AudioEvent makeCueEvent(AudioEvent src) {

		float[] srcFloat = src.getFloatBuffer();
		float[] cueFloat = new float[srcFloat.length];
		System.arraycopy(srcFloat, 0, cueFloat, 0, srcFloat.length);

		TarsosDSPAudioFormat format = playbackDispatcher.getFormat();
		AudioEvent cue = new AudioEvent(format);
		cue.setFloatBuffer(cueFloat);

		cue.setOverlap(src.getOverlap());
		cue.setBytesProcessing(src.getByteBuffer().length);

		return cue;
	}

}
