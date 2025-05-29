package dsp;

import be.tarsos.dsp.filters.IIRFilter;

public class LowPassEqualizer extends IIRFilter {

	/**
	 * @param sampleRate the sample rate of the audio
	 * @param frequency  The frequency that sets the cutoff point for the equalizer
	 */

	public LowPassEqualizer(float frequency, float sampleRate) {
		super(frequency > 60.0F ? frequency : 60.0F, sampleRate);
	}

	/**
	 * 
	 * @param frequency The frequency that sets the cutoff point for the equalizer
	 */
	public void setFrequency(float frequency) {
		super.setFrequency(frequency);
	}

	/**
	 * Calculates the curve for the Equalizer
	 */
	protected void calcCoeff() {
		float freqFrac = this.getFrequency() / this.getSampleRate();
		float x = (float) Math.exp(-14.445 * (double) freqFrac);
		this.a = new float[] { (float) Math.pow((double) (1.0F - x), 4.0) };
		this.b = new float[] { 4.0F * x, -6.0F * x * x, 4.0F * x * x * x, -x * x * x * x };
	}
}
