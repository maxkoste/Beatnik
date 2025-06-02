package view;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class RightPnl {
    private MainFrame mainFrame;
    private GridPane primaryPane;
    private int maxCols;

    private CircularSlider effectIntensity;
    private CircularSlider effectSelector;

    public RightPnl(MainFrame mainFrame, GridPane primaryPane, int maxCols) {
        this.mainFrame = mainFrame;
        this.primaryPane = primaryPane;
        this.maxCols = maxCols - 1;

        initialize();
    }

    /**
     * Creates the GUI elements on the right side of the screen.
     */
    private void initialize() {
        effectIntensity = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
        effectIntensity.setAngle(0.0); // Starts of at 0 degrees
        effectIntensity.valueProperty().addListener((observable, oldValue, newValue) -> {
            float volume = newValue.floatValue();
            float mixValue = volume / 270.0f;
            mixValue = Math.max(0.0f, Math.min(1.0f, mixValue));
            mainFrame.setEffectMix(mixValue);
        });
        effectIntensity.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        effectIntensity.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        // effectIntensity.setScaleX(0.8);
        // effectIntensity.setScaleY(0.8);

        primaryPane.add(effectIntensity, maxCols - 1, 5);

        ImageView effectIntensityImg = new ImageView("/Knobs/knob-bg.png");
        effectIntensityImg.fitWidthProperty().bind(effectIntensity.widthProperty());
        effectIntensityImg.fitHeightProperty().bind(effectIntensity.heightProperty());
        effectIntensityImg.setMouseTransparent(true);
        // effectIntensityImg.setScaleX(0.8);
        // effectIntensityImg.setScaleY(0.8);
        primaryPane.add(effectIntensityImg, maxCols - 1, 5);

        Label effectIntensityLabel = new Label("Effect Intensity");
        effectIntensityLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        effectIntensityLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        effectIntensityLabel.setAlignment(Pos.TOP_CENTER);
        GridPane.setColumnSpan(effectIntensityLabel, 3);
        primaryPane.add(effectIntensityLabel, maxCols - 2, 6);

        effectSelector = new CircularSlider(5, true, "/Knobs/knob-black-fg.png");
        effectSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            int effectSelectorValue = newValue.intValue();
            mainFrame.setEffect(effectSelectorValue);
            // Gets the saved state of the selected effects mix settings and redraws the
            // knob
            float savedMix = mainFrame.getCurrentEffectMix();
            float knobValue = savedMix * 270.0f;
            effectIntensity.setAngle(knobValue);
            mainFrame.setEffectMix(savedMix);
        });
        effectSelector.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        effectSelector.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        // effectSelector.setScaleX(0.8);
        // effectSelector.setScaleY(0.8);
        primaryPane.add(effectSelector, maxCols - 1, 8);

        ImageView effectSelectorImg = new ImageView("/Knobs/effect-selector-bg.png");
        effectSelectorImg.fitWidthProperty().bind(effectSelector.widthProperty());
        effectSelectorImg.fitHeightProperty().bind(effectSelector.heightProperty());
        effectSelectorImg.setMouseTransparent(true);
        // effectSelectorImg.setScaleY(0.8);
        // effectSelectorImg.setScaleX(0.8);
        primaryPane.add(effectSelectorImg, maxCols - 1, 8);

        Label echo = new Label("Echo");
        echo.setAlignment(Pos.BOTTOM_RIGHT);
        echo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        echo.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(echo, maxCols - 2, 8);

        Label flanger = new Label("Flanger");
        flanger.setAlignment(Pos.TOP_RIGHT);
        flanger.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        flanger.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(flanger, maxCols - 2, 8);

        Label placebo = new Label("Filter");
        placebo.setAlignment(Pos.BOTTOM_CENTER);
        placebo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        placebo.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(placebo, maxCols - 1, 7);

        Slider masterVolume = new Slider();
        masterVolume.setOrientation(Orientation.VERTICAL);
        masterVolume.setMax(100.0);
        masterVolume.setBlockIncrement(20);
        masterVolume.setMinorTickCount(0);
        // masterVolume.setShowTickLabels(true);
        // masterVolume.setShowTickMarks(true);
        masterVolume.showTickLabelsProperty();
        masterVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            mainFrame.setMasterVolume(newValue.floatValue() / 100);
        });
        masterVolume.setValue(50);
        masterVolume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        masterVolume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        GridPane.setRowSpan(masterVolume, 2);
        primaryPane.add(masterVolume, maxCols - 1, maxCols - 2);

        Label masterVolumeLabel = new Label();
        masterVolumeLabel.setText("Master Volume");
        masterVolumeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        masterVolumeLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        masterVolumeLabel.setAlignment(Pos.TOP_CENTER);
        GridPane.setColumnSpan(masterVolumeLabel, 3);
        primaryPane.add(masterVolumeLabel, maxCols - 2, maxCols);
    }

    public void resetEffectIntensity() {
        effectIntensity.setAngle(0.0);
    }

    public void resetEffectSelector() {
        effectSelector.setAngle(135.0);
    }
}
