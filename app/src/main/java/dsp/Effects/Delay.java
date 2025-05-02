package dsp.Effects;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

/**
 * This class represents the delay effect. Had to be made custom to support
 * mixing
 * dry and wet signal.
 */
public class Delay implements AudioProcessor {

    private double sampleRate;
    private float[] echoBuffer;
    private int position;
    private float decay;
    private float mix;
    private double newEchoLength;

    /**
     * @param echoLength in seconds
     * @param decay      the decay of the echo, a value between 0 and 1
     * @param sampleRate sample rate in hertz (the same as the audio!)
     */
    public Delay(double echoLength, double decay, double sampleRate) {
        this.sampleRate = sampleRate;
        this.mix = 0.0f; // initialize effect with 100% dry signal
        setDecay(decay);
        setEchoLength(echoLength);
        applyNewEchoLength();
    }

    /**
     * Sets the decay values for the delay
     * imprecise casting to float from double .
     * Not relevant since it doesnt need to be super accurate.
     * 
     * @param newDecay
     */
    public void setDecay(double newDecay) {
        this.decay = (float) newDecay;
    }

    /**
     * Set the echo length permits null or blank values.
     * 
     * @param newEchoLength
     */
    public void setEchoLength(double newEchoLength) {
        this.newEchoLength = newEchoLength;
    }

    /**
     * Can't be repeated unless new echo length is set.
     * Updates the echo length from the last read position in the buffer 
     */
    private void applyNewEchoLength() {
        if (newEchoLength != -1) {

            float[] newEchoBuffer = new float[(int) (sampleRate * newEchoLength)];
            if (echoBuffer != null) {
                for (int i = 0; i < newEchoBuffer.length; i++) {
                    if (position >= echoBuffer.length) {
                        position = 0;
                    }
                    newEchoBuffer[i] = echoBuffer[position];
                    position++;
                }
            }
            this.echoBuffer = newEchoBuffer;
            newEchoLength = -1;
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
    public void setMix(float mix) {
        this.mix = mix;
    }

    /**
     * Processes the audio and apply the delay-effect
     * gets the overlap value from the currently playing songs buffer and syncs up with it.
     */
    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();
        int overlap = audioEvent.getOverlap();

        for (int i = overlap; i < audioFloatBuffer.length; i++) {
            if (position >= echoBuffer.length) {
                position = 0;
            }

            float drySignal = audioFloatBuffer[i];
            float wetSignal = echoBuffer[position] * decay;

            audioFloatBuffer[i] = audioFloatBuffer[i] + echoBuffer[position] * decay;
            // store the sample in the buffer;
            echoBuffer[position] = audioFloatBuffer[i];
            audioFloatBuffer[i] = (1.0f - mix) * drySignal + mix * (drySignal + wetSignal);

            position++;
        }

        applyNewEchoLength();

        return true;
    }

    /**
     * is called when the processing on the stream is finished.
     * Does nothing for now.
     */
    @Override
    public void processingFinished() {
    }
}
