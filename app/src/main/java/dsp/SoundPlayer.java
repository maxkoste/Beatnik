package dsp;

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

	/**
	 * @param resourcePath the audio to be played
	 */
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

	/**
	 * Resets the audios starting position after each play
	 */
	public void play() {
		if (clip.isRunning()) {
			clip.stop();
		}
		clip.setFramePosition(0);
		clip.start();
	}

	/**
	 * Normalizes the incomming float value to a dB scale
	 * 
	 * @param volume value between 1-100
	 */
	public void setVolume(float volume) {
		if (clip == null)
			return;

		float min = gainControl.getMinimum();
		float max = gainControl.getMaximum();
		float dB = min + (max - min) * volume; // interpolate between min and max
		this.gainControl.setValue(dB);
	}
}
