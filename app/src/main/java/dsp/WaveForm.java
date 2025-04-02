package dsp;

import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WaveForm {

    public static List<Float> extract (String filePath) {
        List<Float> audioSamples = new ArrayList<>();

        try {
            URL audioUrl = WaveForm.class.getClassLoader().getResource("songs/" + filePath);
            System.out.println("Audio url= "+audioUrl);

            if (audioUrl == null) {
                System.out.println("File not found: " + filePath);
                return audioSamples;
            }

            File file = new File(audioUrl.toURI());
            AudioDispatcher waveDispatcher = AudioDispatcherFactory.fromPipe(file.getAbsolutePath(), 44100, 4096, 0);

            waveDispatcher.addAudioProcessor(new AudioProcessor() {

                @Override
                public boolean process(AudioEvent audioEvent) {
                    for (float sample : audioEvent.getFloatBuffer()) {
                        audioSamples.add(sample);
                    }
                    return true;
                }

                @Override
                public void processingFinished() {
                    System.out.println("Audio dispatcher finished");
                }
            });

            waveDispatcher.run();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return audioSamples;
    }
}
