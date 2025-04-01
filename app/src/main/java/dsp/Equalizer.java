package dsp;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.filters.LowPassFS;

public class Equalizer implements AudioProcessor {
    private LowPassFS bassFilter;
    private HighPass trebleFilter;
    private final float sampleRate;
    private float bassCutoffHz;
    private float trebleCutoffHz;

    public Equalizer(float sampleRate, float bassCutoffHz, float trebleCutoffHz) {
        this.sampleRate = sampleRate;
        this.bassCutoffHz = bassCutoffHz;
        this.trebleCutoffHz = trebleCutoffHz;
        updateBassFilter(); // Initialize with 0dB gain
        updateTrebleFilter();
    }

    public void setBassCutoff(float cutoffHz) {
        this.bassCutoffHz = cutoffHz;
        updateBassFilter();
    }

    public void setTrebleCutoff(float cutoffHz) {
        this.trebleCutoffHz = cutoffHz;
        updateTrebleFilter();
    }

    private void updateBassFilter() {
        bassFilter = new LowPassFS(bassCutoffHz, sampleRate);
    }

    private void updateTrebleFilter() {
        trebleFilter = new HighPass(trebleCutoffHz, sampleRate);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        // Apply bass filter first, then treble filter
        bassFilter.process(audioEvent);
        trebleFilter.process(audioEvent);
        return true;
    }

    @Override
    public void processingFinished() {
        // Cleanup resources if needed
    }
}