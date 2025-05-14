package dsp.util;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.PipedAudioStream;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.UniversalAudioInputStream;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class DispatcherFactory {

    /**
     * Custom implementation that tries to send a custom script to ffmpeg to make it
     * play in stereo. Messes up the audio !...
     * Not ideal
     */

    public static AudioDispatcher fromPipeStereo(String audioFilePath, int sampleRate, int bufferSize,
            int bufferOverlap)
            throws IOException {
        if (audioFilePath == null || audioFilePath.isEmpty()) {
            throw new IllegalArgumentException("Audio file path cannot be null or empty.");
        }

        // Prepare FFmpeg command
        String[] command = {
                "ffmpeg",
                "-ss", "0.0", // start at beginning
                "-i", audioFilePath,
                "-f", "s16le", // raw 16-bit signed PCM
                "-acodec", "pcm_s16le", // specify 16-bit codec
                "-ac", "2", // stereo
                "-ar", String.valueOf(sampleRate),
                "-" // pipe to stdout
        };

        // Initialize process builder
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.redirectErrorStream(true); // FFmpeg errors to stdout
        Process process = builder.start();

        // Handling potential errors from FFmpeg process
        try (InputStream ffmpegStream = new BufferedInputStream(process.getInputStream())) {

            int bytesPerSample = 2; // 16-bit = 2 bytes per sample
            int channels = 2; // Stereo
            int byteBufferSize = bufferSize * bytesPerSample * channels;

            TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(
                    sampleRate,
                    bytesPerSample * 8, // 16 bits
                    channels,
                    true, // signed
                    false // little endian
            );

            // Return the AudioDispatcher with an input stream
            return new AudioDispatcher(new UniversalAudioInputStream(ffmpegStream, format), bufferSize, bufferOverlap);
        }
    }
}