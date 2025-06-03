package dsp;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;

public class Equalizer implements AudioProcessor {
	private BandPass bandPassFilter;
	private float gain;
	private float centerFrequency;
	private float bandwidth;
	private boolean cut;

	/**
	 * 
	 * @param sampleRate sample rate of the song
	 * @param bandWidth  the width of the effected frequencies
	 * @param frequency  the center of the frequency that is effected
	 */
	public Equalizer(float sampleRate, float bandWidth, float frequency) {
		this.centerFrequency = frequency;
		this.bandwidth = bandWidth;
		setGain(50); // knob at twelve O clock
		this.bandPassFilter = new BandPass(centerFrequency, bandwidth, sampleRate);
		this.cut = false;
	}

	/**
	 * Set the gain of the effected frequencies
	 * converts them to a dB scale +/-12dB.
	 * 
	 * @param gainDb
	 */
	public void setGain(float gainDb) {
		System.out.println(gainDb);
		float scaledDb = (gainDb - 50) * 0.24f; // This maps 0-100 to +/-12dB

		// Increase the Q value of the
		if (scaledDb < 0 && !cut) {
			this.bandwidth = bandwidth * 2;
			this.bandPassFilter.setBandWidth(this.bandwidth);
			this.cut = true;
		}
		if (scaledDb > 0 && cut) {
			this.cut = false;
			this.bandwidth = bandwidth / 2;
			this.bandPassFilter.setBandWidth(this.bandwidth);
		}
		this.gain = (float) Math.pow(10.0, scaledDb / 20.0);
	}

	/**
	 * Processes the audio and apply the equalizer effect
	 * gets the overlap value from the currently playing songs buffer and syncs up
	 * with it.
	 */
	@Override
	public boolean process(AudioEvent audioEvent) {
		/*
		 * First apply the bandPass filter to isolate the target frequencies,
		 * then apply gain only to those frequencies.
		 */
		float[] audioFloatBuffer = audioEvent.getFloatBuffer();
		float[] originalBuffer = audioFloatBuffer.clone();
		bandPassFilter.process(audioEvent);

		// apply gain to the isolated frequencies,
		// subtract the isolated frequencies from the original audio signal,
		// then mix the signals
		for (int i = audioEvent.getOverlap(); i < audioFloatBuffer.length; i++) {

			float filteredSignal = audioFloatBuffer[i] * gain;
			float originalSignal = originalBuffer[i] - audioFloatBuffer[i];
			float mixedSignal = filteredSignal + originalSignal;

			// Clip to prevent distortion
			if (mixedSignal > 1.0f) {
				mixedSignal = 1.0f;
			}

			audioFloatBuffer[i] = mixedSignal;
		}
		return true;
	}

	/**
	 * called when the audio buffer is completely processed (song is over)
	 */
	@Override
	public void processingFinished() {
		// Cleanup resources if needed
	}
}
