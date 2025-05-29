package dsp;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * This class loads audio from a resource path and plays it, used for audio
 * effects
 */
public class SoundPlayer {

	private Clip clip;
	private FloatControl gainControl;

	public SoundPlayer(String resourcePath) {
		try {
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(
					getClass().getResource(resourcePath));
			clip = AudioSystem.getClip();
			clip.open(audioStream);
			this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

			float min = gainControl.getMinimum();
			float max = gainControl.getMaximum();
			gainControl.setValue(min + (max - min) * 0.5f); // start at 50% volume
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void play() {
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.start();
	}

	public void setVolume(float volume) {
		if (clip == null)
			return;

		float min = gainControl.getMinimum();
		float max = gainControl.getMaximum();
		float dB = min + (max - min) * volume; // interpolate between min and max
		this.gainControl.setValue(dB);
	}
}
