package dsp.Effects;
import be.tarsos.dsp.effects.DelayEffect;

//This class represents the delay implementation
public class Delay extends DelayEffect implements AudioEffect{

    public Delay(double echoLength, double decay, double sampleRate) {
            super(echoLength, decay, sampleRate);
        }
    
        @Override
    public void process(float[] samples) {
    }
    
}
