package view;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class WaveFormCanvas extends Canvas { //TODO: GÖR OM SÅ ATT BARA float[] ANVÄNDS FÖR EXTRA SPEEEED
    private final GraphicsContext gc;
    private double width;
    private double height;
    private List<Float> originalAudioData;
    private int originalAudioDataSize;
    private final float tenFloatSeconds = 438097.051598F;
    private final float floatSecond = 43809.7051598F;

    public WaveFormCanvas(double width, double height) {
        super(width, height);
        this.width = width;
        this.height = height;
        gc = getGraphicsContext2D();
    }

    public void setOriginalAudioData(List<Float> originalAudioData) {
        this.originalAudioData = originalAudioData;
        originalAudioDataSize = originalAudioData.size();
        ArrayList<Float> audioSnippet = new ArrayList<>();
        for (int i = 0; i < (tenFloatSeconds * 2); i++ ) {
            audioSnippet.add(originalAudioData.get(i));
        }
        draw(audioSnippet);
    }

    public void update(float currentSecond) {
        float currentFloatSecond = currentSecond * floatSecond;
        int startingPoint = Math.round(currentFloatSecond - tenFloatSeconds);
        if ((startingPoint > 0) && (currentFloatSecond + tenFloatSeconds < originalAudioDataSize +1)) {
            int length = Math.min(originalAudioData.size() - startingPoint, (int)(currentFloatSecond + tenFloatSeconds) - startingPoint);
            float[] audioSnippet = new float[length];
            for (int i = 0; i < length; i++) {
                audioSnippet[i] = originalAudioData.get(startingPoint + i);
            }

            /*
            ArrayList<Float> audioSnippet = new ArrayList<>();
            for (int i = startingPoint; i < (currentFloatSecond + tenFloatSeconds); i++) {
                audioSnippet.add(originalAudioData.get(i));
            }

             */
            draw(audioSnippet);
        }
    }

    public void draw(float[] audioData) {
        Platform.runLater(() -> {
            gc.clearRect(0, 0, width, height);
            gc.setStroke(Color.RED);
            double midY = height / 2;
            int widthX = (int) width;
            int totalSamples = audioData.length;
            for (int x = 0; x < widthX; x++) {
                int index = (int) ((x / (double) widthX) * totalSamples);
                float sampleValue = audioData[index];
                double y = sampleValue * midY;
                gc.strokeLine(x, midY - y, x, midY + y);
            }
        });
    }

    public void draw(List<Float> audioData) {
        Platform.runLater(() -> {
            gc.clearRect(0, 0, width, height);
            gc.setStroke(Color.RED);
            double midY = height / 2;
            int widthX = (int) width;
            int totalSamples = audioData.size();
            for (int x = 0; x < widthX; x++) {
                int index = (int) ((x / (double) widthX) * totalSamples);
                float sampleValue = audioData.get(index);
                double y = sampleValue * midY;
                gc.strokeLine(x, midY - y, x, midY + y);
            }
        });
    }
}
