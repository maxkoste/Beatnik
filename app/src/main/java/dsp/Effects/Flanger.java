package dsp.Effects;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

//This class represents the pahser implementation
public class Flanger implements AudioProcessor {

    private float[] flangerBuffer;
    private int writePosition;
    private float dry;
    private float wet;
    private double lfoFrequency;
    private double sampleRate;

    public Flanger(double maxFlangerLength, double wet,
            double sampleRate, double lfoFrequency) {
        this.flangerBuffer = new float[(int) (sampleRate * maxFlangerLength)];
        this.sampleRate = sampleRate;
        this.lfoFrequency = lfoFrequency;
        this.wet = (float) wet;
        this.dry = (float) (1 - wet);
    }

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();
        int overlap = audioEvent.getOverlap();

        double twoPIf = 2 * Math.PI * lfoFrequency / 2.0;
        double time = audioEvent.getTimeStamp(); // in seconds
        double timeStep = 1.0 / sampleRate; // also in seconds

        for (int i = overlap; i < audioFloatBuffer.length; i++) {

            double lfoValue = (flangerBuffer.length - 1) * Math.sin(twoPIf * time);
            time += timeStep;

            int delay = (int) (Math.round(Math.abs(lfoValue)));

            if (writePosition >= flangerBuffer.length) {
                writePosition = 0;
            }
            flangerBuffer[writePosition] = audioFloatBuffer[i];

            int readPosition = writePosition - delay;
            if (readPosition < 0) {
                readPosition += flangerBuffer.length;
            }

            writePosition++;

            audioFloatBuffer[i] = dry * audioFloatBuffer[i] + wet * flangerBuffer[readPosition];
        }
        return true;
    }

    @Override
    public void processingFinished() {
    }

    /**
     * Set the new length of the delay.
     * 
     * @param flangerLength
     */
    public void setFlangerLength(double flangerLength) {
        flangerBuffer = new float[(int) (sampleRate * flangerLength)];
    }

    /**
     * Sets the frequency of the LFO (sine wave), in Hertz.
     * 
     * @param lfoFrequency
     */
    public void setLFOFrequency(double lfoFrequency) {
        this.lfoFrequency = lfoFrequency;
    }

    /**
     * Sets the wetness and dryness of the effect.
     * 
     * @param wet
     */
    public void setWet(double wet) {
        this.wet = (float) wet;
        this.dry = (float) (1 - wet);
    }
}