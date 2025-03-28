package dsp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

//This class is responsible for playing the audio, and its volume
public class MediaPlayer {

    private Clip clip;
    private FloatControl volumeControl;
    private String currentSongFilePath;

    public MediaPlayer(){
    }

    public void setUp() {
        try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("src/main/resources/songs/" + currentSongFilePath).getAbsoluteFile())) {
            if (audioInputStream == null) {
                throw new IllegalArgumentException("Song cannot be played");
            }
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            //get volume control
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            setVolume(50);
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

    public void setSong(String filepath){
        this.currentSongFilePath = filepath;
        setUp();
    }
}
