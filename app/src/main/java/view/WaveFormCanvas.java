package view;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class WaveFormCanvas extends Canvas {
    private final GraphicsContext gc;
    private float[] originalAudioData;
    private int originalAudioDataSize;
    private final float sevenFloatSeconds = 306667.936119F;
    private final float floatSecond = 43809.7051598F;
    private final int snippetLength = Math.round(sevenFloatSeconds * 2);

    /**
     * For now width and height cant be changed. Might get implemented later
     * Collects the graphics content from the super class.
     * 
     * @param //width
     * @param //height
     */
    public WaveFormCanvas() {
        gc = getGraphicsContext2D();

        // to make sure its colored before playing audio
        gc.setFill(Color.web("#191c2b")); // Dark gray
        gc.fillRect(0, 0, getWidth(), getHeight());

        widthProperty().addListener((obs, oldVal, newVal) -> redraw());
        heightProperty().addListener((obs, oldVal, newVal) -> redraw());
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    /**
     * Sets the songs audio data to a float array equal to the length of the entire
     * song.
     * Sets the length that is used in the update method.
     * 
     * @param originalAudioData
     */
    public void setOriginalAudioData(float[] originalAudioData) {
        this.originalAudioData = originalAudioData;
        originalAudioDataSize = originalAudioData.length;
    }

    /**
     * Updates the waveform 10 sek before and after what is currently being played.
     * 10 sek before the songs starts and 10 seconds after it ended the array is
     * filled with 0.0f
     * @param currentSecond the current values in the float array, represented by its float value
     */
    public void update(float currentSecond) {
        if (currentSecond != 0) {
            float currentFloatSecond = currentSecond * floatSecond;
            int startingPoint = Math.round(currentFloatSecond - sevenFloatSeconds);
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

    /**
     * Redraws the new waveform based on a array of float values
     * Lines are closer to the middle the lower the float value
     * 
     * @param audioData
     */
    public void draw(float[] audioData) {
        Platform.runLater(() -> {
            double width = getWidth();
            double height = getHeight();
            gc.setFill(Color.web("#191c2b"));
            gc.fillRect(0, 0, width, height);
            // gc.clearRect(0, 0, width, height);
            gc.setStroke(Color.web("#444857"));
            gc.strokeLine(width/2, height, width/2, 0);

            double midY = height / 2;
            int widthX = (int) width;
            int totalSamples = audioData.length;
            for (int x = 0; x < widthX; x++) {
                int index = (int) ((x / (double) widthX) * totalSamples);
                float sampleValue = audioData[index];
                double y = sampleValue * midY;

                // Rainbow effect
                // double hue = (x * 360.0 / widthX) % 360; // Gradual color shift from left to
                // right
                // gc.setStroke(Color.hsb(hue, 1.0, 1.0));

                // Color based on the amplitude of the sample value
                float normalizedSample = Math.abs(sampleValue); // Range from 0 to 1
                gc.setStroke(Color.hsb(normalizedSample * 240, 1.0, 1.0)); // Blue to red based on value

                gc.strokeLine(x, midY - y, x, midY + y);
            }
        });
    }

    private void redraw() {
        gc.setFill(Color.web("#191c2b"));
        gc.fillRect(0, 0, getWidth(), getHeight());
    }
}
