package view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CircularSlider extends Application {
  private double angle = 0; // Start from bottom-left
  private double value = 0; // Value from 0 to 1
  private static final int SIZE = 50;
  private Label valueLabel = new Label("Value: 0.0");
  private double lastAngle = 0; // Track last valid angle

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    Canvas canvas = new Canvas(SIZE, SIZE);
    GraphicsContext gc = canvas.getGraphicsContext2D();
    drawKnob(gc);

    canvas.setOnMouseDragged(event -> handleMouseDrag(event, gc));
    canvas.setOnMousePressed(event -> handleMouseDrag(event, gc));

    StackPane root = new StackPane();
    root.getChildren().addAll(canvas, valueLabel);
    Scene scene = new Scene(root, SIZE, SIZE + 20);

    primaryStage.setTitle("Knob Control");
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  private void handleMouseDrag(MouseEvent event, GraphicsContext gc) {
    double centerX = SIZE / 2.0;
    double centerY = SIZE / 2.0;
    double dx = event.getX() - centerX;
    double dy = event.getY() - centerY;


    double newAngle = Math.toDegrees(Math.atan2(dy, dx));

    newAngle = ((newAngle + 360 - 135) % 360);  // Offset by -135° to start at bottom-left

    // Ensure the knob stays in the bottom-left to bottom-right arc without jumping across
    if (newAngle < 0) {
      newAngle = 0;
    } else if (newAngle > 270) {
      newAngle = 270;
    }
    // A similar method could be used to create a knob that jumps from a set number of different values (like the effect selector)

    // Prevent sudden jumps across the bottom gap
    if (Math.abs(newAngle - lastAngle) > 50) {
      return; // Ignore input that jumps across the gap
    }

    angle = newAngle;
    lastAngle = newAngle;
    value = (angle / 2.7);
    valueLabel.setText(String.format("Value: %.2f", value));
    drawKnob(gc);
  }

  private void drawKnob(GraphicsContext gc) {
    gc.clearRect(0, 0, SIZE, SIZE);
    gc.setFill(Color.LIGHTGRAY);
    gc.fillOval(0, 0, SIZE, SIZE);
    gc.setFill(Color.DARKGRAY);
    gc.fillOval(10, 10, SIZE - 20, SIZE - 20);

    double centerX = SIZE / 2.0;
    double centerY = SIZE / 2.0;
    double knobRadius = (SIZE / 2.0) - 5;
    double knobX = centerX + knobRadius * Math.cos(Math.toRadians(angle +135));
    double knobY = centerY + knobRadius * Math.sin(Math.toRadians(angle +135));

    gc.setFill(Color.DARKGRAY);
    gc.fillOval(knobX - 5, knobY - 5, 10, 10);
  }
}
