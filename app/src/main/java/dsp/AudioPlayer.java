package dsp;

import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//This class is responsible for playing the audio, and its volume
public class AudioPlayer {

        private Clip clip;
        private FloatControl volumeControl;

        public AudioPlayer(){
            setUp();
        }

        public void setUp() {
            //will replaced with a file path variable.
        try (InputStream audioStream = getClass().getClassLoader().getResourceAsStream("songs/test.wav")) {
            if (audioStream == null) {
                throw new IllegalArgumentException("Resource test.wav not found.");
            }

            // Create an audio input stream from the resource stream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioStream);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            //get volume control
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void setVolume(float volume){
        if(volumeControl != null ){
            //we need to convert the 0-100 float number to mimic how dB scales 
            float minGain = volumeControl.getMinimum(); // Typically -80 dB
            float maxGain = volumeControl.getMaximum(); // Typically 6 dB
    
            // Map slider range (0-100) to dB range (minGain to maxGain)
            float gain = (float) ((volume / 100.0) * (maxGain - minGain) + minGain);
    
            // Set value safely within bounds
            volumeControl.setValue(gain);
        }
    }

    //This method is called when you want to play the audio.
    public void playAudio() {
        if (clip != null) {
          if (!clip.isActive()) {
            clip.start();
          } else clip.stop();
        }
    }
}
