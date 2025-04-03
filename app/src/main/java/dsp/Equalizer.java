package dsp;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.filters.BandPass;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.GainProcessor;

public class Equalizer implements AudioProcessor {
    private final float sampleRate;
    private BandPass bandPassFilter;
    private GainProcessor gainProcessor;
    private double gain; // in dB
    private float centerFrequency;
    private float bandwidth;
    private AudioDispatcher dispatcher;
    private String filePath;

    public Equalizer(float sampleRate, float bandWidth, float frequency, String filePath) {
        this.sampleRate = sampleRate;
        this.centerFrequency = frequency;
        this.bandwidth = bandWidth;
        this.gain = 0.0f; // 0 dB by default
        //this.gainProcessor = new GainProcessor(1.0f); // Start with unity gain
        updateFilter();
    }
    
    // public void setFilePath(String filePath){
    //     this.dispatcher = AudioDispatcherFactory.fromPipe(filePath, 44100, 4096, 0);
    //     this.filePath = filePath;
    //     this.dispatcher.addAudioProcessor(gainProcessor);
    // }

    public void updateFilter() {
        bandPassFilter = new BandPass(centerFrequency, bandwidth, sampleRate);
    }

    //Set the gain for the specified frequencies of the EQ
    public void setGain(double gainDb) {
        this.gain = gainDb;
        //float gainLinear = (float) Math.pow(10.0, gainDb / 20.0); // Convert dB to linear gain
        gainProcessor.setGain(gainDb);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        /*
         * Attempting a manual processing of the audio-buffer to bypass the creation of a 
         * gainprocessor and hopefully make it faster.
         */
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();
        for (int i = audioEvent.getOverlap(); i < audioFloatBuffer.length; i++) {
            float newValue = (float)(audioFloatBuffer[i] * gain);
            if (newValue > 1.0f){
                newValue = 1.0f;
            } else if (newValue < -1.0f){
                newValue = -1.0f;
            }
            audioFloatBuffer[i] = newValue;
        }
        return true;
    }

    @Override
    public void processingFinished() {
        // Cleanup resources if needed
    }
}