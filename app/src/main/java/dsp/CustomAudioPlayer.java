package dsp;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;

import javax.sound.sampled.*;
/**
 * {@code CustomAudioPlayer} is an implementation of the TarsosDSP {@link AudioProcessor} interface
 * that handles audio playback via a specified {@link Mixer}.
 * <p>
 * It converts a {@link TarsosDSPAudioFormat} to a standard Java {@link AudioFormat}
 * and writes audio data to a {@link SourceDataLine} for playback through an audio output device.
 */

public class CustomAudioPlayer implements AudioProcessor {
    private final SourceDataLine sourceLine;

    /**
     * Constructs a new {@code CustomAudioPlayer} using the given mixer and audio format.
     *
     * @param mixer        The audio mixer to use for output. If {@code null}, the system default mixer will be used.
     * @param tarsosFormat The audio format from TarsosDSP to be converted for playback.
     * @throws LineUnavailableException If the specified line is not supported or cannot be opened.
     */
    public CustomAudioPlayer(Mixer mixer, TarsosDSPAudioFormat tarsosFormat) throws LineUnavailableException {
        AudioFormat format = new AudioFormat(
                tarsosFormat.getSampleRate(),
                tarsosFormat.getSampleSizeInBits(),
                tarsosFormat.getChannels(),
                true,
                false
        );

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
            throw new LineUnavailableException("Line not supported: " + format.toString());
        }

        if (mixer != null) {
            sourceLine = (SourceDataLine) mixer.getLine(info);
        } else {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        }

        sourceLine.open(format);
        sourceLine.start();
    }
    /**
     * Processes an incoming {@link AudioEvent} and writes the audio data to the output line.
     *
     * @param audioEvent The audio event containing the buffer to be played.
     * @return {@code true} to indicate processing should continue.
     */
    @Override
    public boolean process(AudioEvent audioEvent) {
        byte[] audioBuffer = audioEvent.getByteBuffer();
        int bufferSize = audioEvent.getBufferSize();
        sourceLine.write(audioBuffer, 0, bufferSize);
        return true;
    }
    /**
     * Writes raw audio data directly to the output line.
     *
     * @param audioBuffer A byte array containing audio samples to be played.
     */
    public void write(byte[] audioBuffer) {
        sourceLine.write(audioBuffer, 0, audioBuffer.length);
    }

    /**
     * Finishes audio processing by draining, stopping, and closing the audio output line.
     * This should be called when playback is complete.
     */
    @Override
    public void processingFinished() {
        sourceLine.drain();
        sourceLine.stop();
        sourceLine.close();
    }
}