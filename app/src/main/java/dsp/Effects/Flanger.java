package dsp.Effects;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

public class Flanger implements AudioProcessor {

    private float[] flangerBuffer;
    private int writePosition;
    private float dry;
    private float wet;
    private double lfoFrequency;
    private double sampleRate;

    /**
     * 
     * @param maxFlangerLength the max length should be a really small number
     * @param wet              The amount of the effect value between 1-0
     * @param sampleRate       sample rate of the song
     * @param lfoFrequency     the frequency of the lfo changes the sound of the
     *                         effect
     */
    public Flanger(double maxFlangerLength, double wet,
            double sampleRate, double lfoFrequency) {
        this.flangerBuffer = new float[(int) (sampleRate * maxFlangerLength)];
        this.sampleRate = sampleRate;
        this.lfoFrequency = lfoFrequency;
        this.wet = (float) wet;
        this.dry = (float) (1 - wet);
    }

    /**
     * Processes the audio and applies the flanger effect.
     * Reads the buffer to sync up with it and creates a sine wave that 
     * makes the audio get out of phase
     */
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

    /**
     * is called when the audiobuffer is completely processed
     */
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