package view;

import com.sun.javafx.util.Utils;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.AccessibleAttribute;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CircularSlider extends Control {

    private double angle = 0;
    private DoubleProperty max;
    private DoubleProperty min;
    private DoubleProperty value;
    private CircularSliderSkin skin;
    private ImageView knobImage;

    public CircularSlider(int tickCount, boolean snapToTick, String imageUrl) {
        knobImage = new ImageView(new Image(imageUrl));
        knobImage.setFitHeight(70);
        knobImage.setFitWidth(70);

        setAngle(0.0); // Set twice because of bad design, give me a break
        setMin(0.0);
        setMax(270.0);
        setAngle(135.0); // Could be made into a parameter to allow custom start position
        this.skin = new CircularSliderSkin(this, tickCount, snapToTick);
        setSkin(this.skin);

        getChildren().add(knobImage);
    }

    public void updateAngle(double angle) {
        knobImage.setRotate(angle-135);
        this.skin.drawKnob(angle);
    }

    public double getValue() {
        return value.get();
    }

    public void setValue(double value) {
        if (!this.valueProperty().isBound()) {
            this.valueProperty().set(value);
        }
    }

    public ImageView getKnobImage(){
        return this.knobImage;
    }
    
    double getAngle() {
        return angle;
    }

    void setAngle(double angle) {
        this.angle = angle;
        setValue(angle);

        knobImage.setRotate(angle - 135); // Subtract 135 so 135° visually maps to 0°
    }

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

}