package view;

import com.sun.javafx.util.Utils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Control;

public class CircularSlider extends Control {

	private double angle;
	private DoubleProperty max;
	private DoubleProperty min;
	private DoubleProperty value;
	private CircularSliderSkin skin;

	/**
	 * @param tickCount
	 * @param snapToTick
	 * @param imageUrl
	 */
	public CircularSlider(int tickCount, boolean snapToTick, String imageUrl) {
		this.skin = new CircularSliderSkin(this, tickCount, snapToTick, imageUrl);
		setSkin(this.skin);
		setAngle(0.0); // Set twice because of bad design, give me a break
		setMin(0.0);
		setMax(270.0);
		setAngle(135.0); // Could be made into a parameter to allow custom start position
	}

	public double getValue() {
		return value.get();
	}

	public void setValue(double value) {
		if (!this.valueProperty().isBound()) {
			this.valueProperty().set(value);
		}
	}

	double getAngle() {
		return angle;
	}

	/**
	 * Sets the angle of the knob and rotates the skin to match
	 */
	void setAngle(double angle) {
		this.angle = angle;
		setValue(angle);
		skin.drawKnob(angle);
		skin.setLastAngle(angle);
	}

	/**
	 * Property bound to the value of the knob
	 */
	public final DoubleProperty valueProperty() {
		if (this.value == null) {
			this.value = new DoublePropertyBase(0.0) {
				protected void invalidated() {
					CircularSlider.this.adjustValues();
					CircularSlider.this.notifyAccessibleAttributeChanged(AccessibleAttribute.VALUE);
				}

				public Object getBean() {
					return CircularSlider.this;
				}

				public String getName() {
					return "value";
				}
			};
		}

		return this.value;
	}

	/**
	 *
	 * Clamps the value to make sure that it is between min and max
	 */
	private void adjustValues() {
		if (this.getValue() < this.getMin() || this.getValue() > this.getMax()) {
			this.setValue(Utils.clamp(this.getMin(), this.getValue(), this.getMax()));
		}
	}

	private double getMax() {
		return this.max.get();
	}

	private double getMin() {
		return this.min.get();
	}

	/**
	 * Property bound to the minimum value of the knob
	 */
	public final DoubleProperty minProperty() {
		if (this.min == null) {
			this.min = new DoublePropertyBase(0.0) {
				protected void invalidated() {
					if (this.get() > CircularSlider.this.getMax()) {
						CircularSlider.this.setMax(this.get());
					}

					CircularSlider.this.adjustValues();
					CircularSlider.this.notifyAccessibleAttributeChanged(AccessibleAttribute.MIN_VALUE);
				}

				public Object getBean() {
					return CircularSlider.this;
				}

				public String getName() {
					return "min";
				}
			};
		}

		return this.min;
	}

	public final void setMin(double min) {
		this.minProperty().set(min);
	}

	public final void setMax(double max) {
		this.maxProperty().set(max);
	}

	/**
	 * Property bound to the maxmimum value of the knob
	 */
	public final DoubleProperty maxProperty() {
		if (this.max == null) {
			this.max = new DoublePropertyBase(100.0) {
				protected void invalidated() {
					if (this.get() < CircularSlider.this.getMin()) {
						CircularSlider.this.setMin(this.get());
					}

					CircularSlider.this.adjustValues();
					CircularSlider.this.notifyAccessibleAttributeChanged(AccessibleAttribute.MAX_VALUE);
				}

				public Object getBean() {
					return CircularSlider.this;
				}

				public String getName() {
					return "max";
				}
			};
		}

		return this.max;
	}

	@Override
	public boolean isResizable() {
		return true;
	}
}
