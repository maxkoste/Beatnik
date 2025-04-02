package view;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class WaveFormCanvas extends Canvas {
    private final List<Float> samples;

    public WaveFormCanvas(List<Float> samples, double width, double height) {
        super(width, height);
        this.samples = samples;
        draw();
    }
    public void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.setStroke(Color.RED);
        double midY = getHeight() / 2;
        int width = (int) getWidth();
        int totalSamples = samples.size();
        for (int x = 0; x < width; x++) {
            int index = (int)((x / (double)width) * totalSamples);
            float sampleValue = samples.get(index);
            double y = sampleValue * midY;
            gc.strokeLine(x, midY - y, x, midY + y);
        }
    }
}
