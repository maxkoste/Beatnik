package dsp;

import dsp.Effects.AudioEffect;

//This class handles real-time effect pipeline
public class EffectChain {
    private AudioEffect currentEffect;

    public void setEffect(AudioEffect audioEffect){
        this.currentEffect = audioEffect;
    }

    public void process(float[] samples){
        if (currentEffect != null){
            currentEffect.process(samples);
        }
    }
}
