package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * This class is responsible for the logic and the appearance of the Knob. 
 */
public class CircularSliderSkin extends SkinBase<CircularSlider> {

    private static final int size = 50;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private double lastAngle; // Track last valid angle
    int tickCount;
    boolean snapToTick;

    /**
     * 
     * @param control the object to be drawn, the interface. 
     * @param tickCount The ticks in the knob, represents each value that can be selected. 
     * @param snapToTick if the knob snaps to the values or is completely free like a bird
     */
    public CircularSliderSkin(CircularSlider control, int tickCount, boolean snapToTick) {
        super(control);

        this.tickCount = tickCount;
        this.snapToTick = snapToTick;
        canvas = new Canvas(size, size);
        gc = canvas.getGraphicsContext2D();

        lastAngle = control.getAngle();
        drawKnob(control.getAngle());

        canvas.setOnMouseDragged(this::handleMouseDrag);
        canvas.setOnMousePressed(this::handleMouseDrag);

        StackPane container = new StackPane();
        container.setPrefSize(size, size);

        container.getChildren().addAll(canvas);

        getChildren().add(container);
    }
    /**
     * takes the position of the mouse and converts it into a position (a degree) on a circle
     * if snap to tick is true it only allows the values dictated by the ticks.
     * Never allows access to the bottom of the circle or anything but continuos movement of the slider.
     * Sets the calculated angle to the interface and redraws the knob.
     * @param event the user moved my knob
     */
    private void handleMouseDrag(MouseEvent event) {
        double centerX = size / 2.0;
        double centerY = size / 2.0;
        double dx = event.getX() - centerX;
        double dy = event.getY() - centerY;

        double newAngle = Math.toDegrees(Math.atan2(dy, dx));
        newAngle = ((newAngle + 360 - 135) % 360);

        if (snapToTick) {
            if (newAngle > 0 && newAngle <= 30) {
                newAngle = 0;
            } else if (newAngle > 30 && newAngle <= 105) {
                newAngle = 68;
            } else if (newAngle > 105 && newAngle <= 165) {
                newAngle = 135;
            } else if (newAngle > 165 && newAngle <= 237) {
                newAngle = 204;
            } else newAngle = 270;
            if (Math.abs(newAngle - lastAngle) > 70) {
                return; // Prevent jumps
            }
        } else {
            if (newAngle < 0) {
                newAngle = 0;
            } else if (newAngle > 270) {
                newAngle = 270;
            }
            if (Math.abs(newAngle - lastAngle) > 50) {
                return; // Prevent jumps
            }
        }

        lastAngle = newAngle;

        CircularSlider control = getSkinnable();
        control.setAngle(newAngle);
        drawKnob(newAngle);
    }
    /**
     * Redraws the knob (the interface control)
     * offsets the knob by 125 degrees so that 0 is not to the right
     * @param angle the current position of the knob. 
     */
    public void drawKnob(double angle) {
        gc.clearRect(0, 0, size, size);
        gc.setFill(Color.LIGHTGRAY);
        gc.fillOval(0, 0, size, size);
        gc.setFill(Color.DARKGRAY);
        gc.fillOval(10, 10, size - 20, size - 20);

        double centerX = size / 2.0;
        double centerY = size / 2.0;
        double knobRadius = (size / 2.0) - 5;
        double tickLength = 5;

        for (int i = 0; i < tickCount; i++) {
            double tickAngle = 270.0 * (i / (double)(tickCount - 1)) - 225;
            double innerX = centerX + (knobRadius - tickLength) * Math.cos(Math.toRadians(tickAngle));
            double innerY = centerY + (knobRadius - tickLength) * Math.sin(Math.toRadians(tickAngle));
            double outerX = centerX + (knobRadius + 2) * Math.cos(Math.toRadians(tickAngle));
            double outerY = centerY + (knobRadius + 2) * Math.sin(Math.toRadians(tickAngle));

            gc.setStroke(Color.DARKGRAY);
            gc.setLineWidth(2);
            gc.strokeLine(innerX, innerY, outerX, outerY);
        }

        double knobX = centerX + knobRadius * Math.cos(Math.toRadians(angle + 135));
        double knobY = centerY + knobRadius * Math.sin(Math.toRadians(angle + 135));

        gc.setFill(Color.DARKGRAY);
        gc.fillOval(knobX - 5, knobY - 5, 10, 10);
    }
}