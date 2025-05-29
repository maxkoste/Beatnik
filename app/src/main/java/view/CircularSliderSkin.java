package view;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.control.SkinBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * This class is responsible for the logic and the appearance of the Knob.
 */
public class CircularSliderSkin extends SkinBase<CircularSlider> {

	private double lastAngle; // Track last valid angle
	int tickCount;
	boolean snapToTick;
	CircularSlider control;
	private final StackPane container;
	private final ImageView knobImage;

	/**
	 * 
	 * @param control    the object to be drawn, the interface.
	 * @param tickCount  The ticks in the knob, represents each value that can be
	 *                   selected.
	 * @param snapToTick if the knob snaps to the values or is completely free like
	 *                   a bird
	 * @param imagePath  the image of the knob
	 */
	public CircularSliderSkin(CircularSlider control, int tickCount, boolean snapToTick, String imagePath) {
		super(control);

		this.tickCount = tickCount;
		this.snapToTick = snapToTick;
		this.control = control;
		knobImage = new ImageView(new Image(imagePath));

		knobImage.setPreserveRatio(true);

		// Set preferred knob size
		knobImage.setFitWidth(60);
		knobImage.setFitHeight(60);
		knobImage.setMouseTransparent(true);

		container = new StackPane(knobImage);
		container.setPrefSize(60, 60);
		container.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		container.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		knobImage.fitWidthProperty().bind(container.widthProperty());
		knobImage.fitHeightProperty().bind(container.heightProperty());
		knobImage.setMouseTransparent(true);
		knobImage.setPickOnBounds(true);

		// MYSTICAL MAGICAL NODE that prevents closely placed CircularSliders from
		// eating mouse events (took me 5ish hours)
		Rectangle clip = new Rectangle();
		clip.widthProperty().bind(container.widthProperty());
		clip.heightProperty().bind(container.heightProperty());
		container.setClip(clip);

		container.setOnMousePressed(this::handleMouseDrag);
		container.setOnMouseDragged(this::handleMouseDrag);

		getChildren().add(container);

		// Initial position
		lastAngle = control.getAngle();
		drawKnob(control.getAngle());
	}

	/**
	 * takes the position of the mouse and converts it into a position (a degree) on
	 * a circle
	 * if snap to tick is true it only allows the values dictated by the ticks.
	 * Never allows access to the bottom of the circle or anything but continuos
	 * movement of the slider.
	 * Sets the calculated angle to the interface and redraws the knob.
	 * 
	 * @param event the user moved my knob
	 */
	private void handleMouseDrag(MouseEvent event) {
		Bounds bounds = container.getLayoutBounds();
		double centerX = bounds.getWidth() / 2.0;
		double centerY = bounds.getHeight() / 2.0;

		// Get mouse position relative to the container
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
			} else
				newAngle = 270;
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

		control.setAngle(newAngle);
		drawKnob(newAngle);
	}

	/**
	 * Redraws the knob image (the interface control)
	 * offsets the knob by 135 degrees so that 0 is not to the right
	 * 
	 * @param angle the current position of the knob.
	 */
	public void drawKnob(double angle) {
		// Rotate the knob image from the control
		Platform.runLater(() -> {
			knobImage.setRotate(angle - 135); // Normalize rotation
		});
	}

	/**
	 * Sets the last angle of the knob to prevent
	 * big jumps
	 */
	public void setLastAngle(double angle) {
		lastAngle = angle;
	}
}
