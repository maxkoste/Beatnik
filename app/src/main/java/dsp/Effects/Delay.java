package dsp.Effects;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;

//This class represents the delay implementation
public class Delay implements AudioProcessor {

    private double sampleRate;
    private float[] echoBuffer;
    private int position;
    private float decay;

    private double newEchoLength;

    public Delay(double echoLength, double decay, double sampleRate) {
        this.sampleRate = sampleRate;
        setDecay(decay);
        setEchoLength(echoLength);
        applyNewEchoLength();
    }

    public void setDecay(double newDecay) {
        this.decay = (float) newDecay;
    }

    public void setEchoLength(double newEchoLength) {
        this.newEchoLength = newEchoLength;
    }

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

    @Override
    public boolean process(AudioEvent audioEvent) {
        float[] audioFloatBuffer = audioEvent.getFloatBuffer();
        int overlap = audioEvent.getOverlap();

        for (int i = overlap; i < audioFloatBuffer.length; i++) {
            if (position >= echoBuffer.length) {
                position = 0;
            }

            // output is the input added with the decayed echo
            audioFloatBuffer[i] = audioFloatBuffer[i] + echoBuffer[position] * decay;
            // store the sample in the buffer;
            echoBuffer[position] = audioFloatBuffer[i];

            position++;
        }

        applyNewEchoLength();

        return true;
    }

    @Override
    public void processingFinished() {
    }
}
