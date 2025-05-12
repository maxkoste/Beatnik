package dsp;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;

import javax.sound.sampled.*;

public class CustomAudioPlayer implements AudioProcessor {
    private final SourceDataLine sourceLine;

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

    @Override
    public boolean process(AudioEvent audioEvent) {
        byte[] audioBuffer = audioEvent.getByteBuffer();
        int bufferSize = audioEvent.getBufferSize();
        sourceLine.write(audioBuffer, 0, bufferSize);
        return true;
    }
    public void write(byte[] audioBuffer) {
        sourceLine.write(audioBuffer, 0, audioBuffer.length);
    }

    @Override
    public void processingFinished() {
        sourceLine.drain();
        sourceLine.stop();
        sourceLine.close();
    }

    public void stopAndFlush() {
        sourceLine.stop();
        sourceLine.flush();
    }
    public void startIfNeeded() {
        if (!sourceLine.isRunning()) {
            sourceLine.start();
        }
    }

}