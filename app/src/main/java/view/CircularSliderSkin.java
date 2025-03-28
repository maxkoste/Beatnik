package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class CircularSliderSkin extends SkinBase<CircularSlider> {

  private static final int size = 50;
  private final Canvas canvas;
  private final GraphicsContext gc;
  private double lastAngle = 0; // Track last valid angle

  protected CircularSliderSkin(CircularSlider control) {
    super(control);

    canvas = new Canvas(size, size);
    gc = canvas.getGraphicsContext2D();

    drawKnob(control.getAngle());

    canvas.setOnMouseDragged(this::handleMouseDrag);
    canvas.setOnMousePressed(this::handleMouseDrag);

    StackPane container = new StackPane();
    container.setPrefSize(size, size);

    container.getChildren().addAll(canvas);

    getChildren().add(container);
  }

  private void handleMouseDrag(MouseEvent event) {
    double centerX = size / 2.0;
    double centerY = size / 2.0;
    double dx = event.getX() - centerX;
    double dy = event.getY() - centerY;

    double newAngle = Math.toDegrees(Math.atan2(dy, dx));
    newAngle = ((newAngle + 360 - 135) % 360);

    if (newAngle < 0) {
      newAngle = 0;
    } else if (newAngle > 270) {
      newAngle = 270;
    }

    if (Math.abs(newAngle - lastAngle) > 50) {
      return; // Prevent jumps
    }

    lastAngle = newAngle;

    CircularSlider control = getSkinnable();
    control.setAngle(newAngle);
    drawKnob(newAngle);
  }

  private void drawKnob(double angle) {
    gc.clearRect(0, 0, size, size);
    gc.setFill(Color.LIGHTGRAY);
    gc.fillOval(0, 0, size, size);
    gc.setFill(Color.DARKGRAY);
    gc.fillOval(10, 10, size - 20, size - 20);

    double centerX = size / 2.0;
    double centerY = size / 2.0;
    double knobRadius = (size / 2.0) - 5;
    double knobX = centerX + knobRadius * Math.cos(Math.toRadians(angle + 135));
    double knobY = centerY + knobRadius * Math.sin(Math.toRadians(angle + 135));

    gc.setFill(Color.DARKGRAY);
    gc.fillOval(knobX - 5, knobY - 5, 10, 10);
  }
}