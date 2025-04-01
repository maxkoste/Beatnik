package dsp;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;

public class Equalizer implements AudioProcessor {
    private final float sampleRate;
    private BandPass bandPassFilter;
    private float gain; // in dB
    private float centerFrequency;
    private float bandwidth;

    public Equalizer(float sampleRate, float bandWidth, float frequency) {
        this.sampleRate = sampleRate;
        this.centerFrequency = frequency;
        this.bandwidth = bandWidth;
        this.gain = 0.0f; // 0 dB by default
        updateFilter();
    }

    public void updateFilter() {
        bandPassFilter = new BandPass(centerFrequency, bandwidth, sampleRate);
    }

    public void setGain(float gainDb) {
        this.gain = gainDb;
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        // Create a copy of the audio data to avoid modifying the original
        float[] audioData = audioEvent.getFloatBuffer().clone();
        
        // Apply the bandpass filter
        bandPassFilter.process(audioEvent);
        
        // Apply the gain
        float gainLinear = (float) Math.pow(10.0, gain / 20.0); // Convert dB to linear gain
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] *= gainLinear;
        }
        
        // Copy the processed data back to the original buffer
        System.arraycopy(audioData, 0, audioEvent.getFloatBuffer(), 0, audioData.length);
        
        return true;
    }

    @Override
    public void processingFinished() {
        // Cleanup resources if needed
    }
}