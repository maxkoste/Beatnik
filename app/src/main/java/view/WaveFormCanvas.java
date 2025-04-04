package view;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class WaveFormCanvas extends Canvas {
    private final GraphicsContext gc;
    private double width;
    private double height;
    private float[] originalAudioData;
    private int originalAudioDataSize;
    private final float tenFloatSeconds = 438097.051598F;
    private final float floatSecond = 43809.7051598F;
    private final int snippetLength = Math.round(tenFloatSeconds*2);

    public WaveFormCanvas(double width, double height) {
        super(width, height);
        this.width = width;
        this.height = height;
        gc = getGraphicsContext2D();
    }

    public void setOriginalAudioData(float[] originalAudioData) {
        this.originalAudioData = originalAudioData;
        originalAudioDataSize = originalAudioData.length;
    }

    public void update(float currentSecond) {
        if (currentSecond != 0) {
            float currentFloatSecond = currentSecond * floatSecond;
            int startingPoint = Math.round(currentFloatSecond - tenFloatSeconds);
            int audioDataStart = Math.max(startingPoint, 0);
            int audioDataEnd = Math.min(startingPoint + snippetLength, originalAudioDataSize);

            float[] audioSnippet = new float[snippetLength];

            int insertStart = Math.max(0, -startingPoint); // if startingPoint is negative, insert later
            for (int i = audioDataStart; i < audioDataEnd; i++) {
                audioSnippet[insertStart + i - audioDataStart] = originalAudioData[i];
            }

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
}
