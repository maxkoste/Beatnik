package view;

import controller.Controller;
import javafx.application.Platform;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class CenterPnl {
    private Controller controller;
    private GridPane primaryPane;
    private int maxCols;
    private Circle[] auIndicatorCirclesOne = new Circle[15];
    private Circle[] auIndicatorCirclesTwo = new Circle[15];

    private CircularSlider channelOneBass;
    private CircularSlider channelOneTreble;
    private CircularSlider channelOneSpeed;

    private CircularSlider channelTwoBass;
    private CircularSlider channelTwoTreble;
    private CircularSlider channelTwoSpeed;

    private Slider channelOneVolume;
    private Slider channelTwoVolume;

    public CenterPnl(Controller controller, GridPane primaryPane, int maxCols) {
        this.controller = controller;
        this.primaryPane = primaryPane;
        this.maxCols = maxCols - 1;


        initializeCrossfader();
        initializeChannelOne();
        initializeChannelTwo();
    }

    /**
     * Creates the crossfader slider and label.
     */
    private void initializeCrossfader() {
        Slider crossFader = new Slider();
        crossFader.setMax(100);
        crossFader.setBlockIncrement(20);
        crossFader.setShowTickMarks(true);
        crossFader.setValue(50);
        crossFader.valueProperty().addListener((observable, oldValue, newValue) -> {
            controller.setCrossfaderModifier(newValue.floatValue());
        });
        crossFader.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        crossFader.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        crossFader.setStyle("-fx-padding: 0;");

        BorderPane crossFaderContainer = new BorderPane();
        GridPane.setColumnSpan(crossFaderContainer, 3);
        crossFaderContainer.setBottom(crossFader);

        primaryPane.add(crossFaderContainer, (maxCols / 2) - 1, maxCols - 1);

        Label crossFaderLabel = new Label("Crossfader");
        crossFaderLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        crossFaderLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        crossFaderLabel.setAlignment(Pos.TOP_CENTER);
        GridPane.setColumnSpan(crossFaderLabel, 3);

        primaryPane.add(crossFaderLabel, (maxCols / 2) - 1, maxCols);
    }

    /**
     * Creates the GUI elements in the left channel column.
     */
    private void initializeChannelOne() {
        ToggleButton channelOneCue = new ToggleButton(); // Button that can be tied to boolean value
        channelOneCue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelOneCue.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        channelOneCue.setBackground(Background.EMPTY);
        channelOneCue.setScaleX(0.8);
        channelOneCue.setScaleY(0.8);
        primaryPane.add(channelOneCue, 3, maxCols - 1);

        // Image overlay for channel one
        ImageView channelOneCueImage = new ImageView("/Buttons/cue-passive.png");
        channelOneCueImage.fitWidthProperty().bind(channelOneCue.widthProperty());
        channelOneCueImage.fitHeightProperty().bind(channelOneCue.heightProperty());
        channelOneCueImage.setMouseTransparent(true); // Allow mouse events to pass through
        channelOneCueImage.setScaleX(0.9);
        channelOneCueImage.setScaleY(0.9);
        primaryPane.add(channelOneCueImage, 3, maxCols - 1);

        // Toggle image on button state change
        channelOneCue.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
          channelOneCue.isSelected();
          controller.toggleCue(1);
            String imagePath = isNowSelected ? "/Buttons/cue-engaged.png" : "/Buttons/cue-passive.png";
            channelOneCueImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        });

        channelOneVolume = new Slider();
        channelOneVolume.setOrientation(Orientation.VERTICAL);
        channelOneVolume.setMax(100.0);
        channelOneVolume.setBlockIncrement(20);
        channelOneVolume.setShowTickMarks(true);
        channelOneVolume.setValue(50);
        channelOneVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            controller.setChannelOneVolume(newValue.floatValue());
        });
        channelOneVolume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelOneVolume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        GridPane.setRowSpan(channelOneVolume, 3);
        primaryPane.add(channelOneVolume, 3, maxCols - 4);

        channelOneBass = new CircularSlider(9, false, "/Knobs/knob-blue-fg.png");
        channelOneBass.valueProperty().addListener((observable, oldValue, newValue) -> {
            float bassGain = newValue.floatValue();
            // Scale value from 0–270 to 0–100dB
            float bass = (bassGain / 270) * 100;
            controller.setBass1(bass);
        });
        channelOneBass.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelOneBass.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(channelOneBass, 3, maxCols - 5);

        ImageView channelOneBassImg = new ImageView("/Knobs/knob-bg.png");
        channelOneBassImg.fitWidthProperty().bind(channelOneBass.widthProperty());
        channelOneBassImg.fitHeightProperty().bind(channelOneBass.heightProperty());
        channelOneBassImg.setMouseTransparent(true);

        primaryPane.add(channelOneBassImg, 3, maxCols - 5);

        channelOneTreble = new CircularSlider(9, false, "/Knobs/knob-green-fg.png");
        channelOneTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Scale the value from 0-270 to 0-100dB
            float trebleGain = newValue.floatValue();
            trebleGain = (trebleGain / 270) * 100;
            controller.setTreble1(trebleGain);
        });
        channelOneTreble.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelOneTreble.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(channelOneTreble, 3, maxCols - 6);

        ImageView channelOneTrebleImg = new ImageView("/Knobs/knob-bg.png");
        channelOneTrebleImg.fitWidthProperty().bind(channelOneTreble.widthProperty());
        channelOneTrebleImg.fitHeightProperty().bind(channelOneTreble.heightProperty());
        channelOneTrebleImg.setMouseTransparent(true);
        primaryPane.add(channelOneTrebleImg, 3, maxCols - 6);

        channelOneSpeed = new CircularSlider(9, false, "/Knobs/knob-red-fg.png");
        channelOneSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            double rawValue = newValue.doubleValue(); // 0.0 - 270.0// Map 0.0 - 270.0 to 0.8 - 1.2
            double mappedValue = 1.2 - (rawValue / 270.0) * (1.2 - 0.8);// Round to 2 decimal places
            mappedValue = Math.round(mappedValue * 100.0) / 100.0;
            controller.setPlaybackSpeedCh1(mappedValue);
        });
        channelOneSpeed.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelOneSpeed.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(channelOneSpeed, 3, maxCols - 7);

        ImageView channelOneSpeedImg = new ImageView("/Knobs/knob-bg.png");
        channelOneSpeedImg.fitWidthProperty().bind(channelOneSpeed.widthProperty());
        channelOneSpeedImg.fitHeightProperty().bind(channelOneSpeed.heightProperty());
        channelOneSpeedImg.setMouseTransparent(true);
        primaryPane.add(channelOneSpeedImg, 3, maxCols - 7);

        VBox audioIndicatorOne = new VBox(8);
        audioIndicatorOne.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        audioIndicatorOne.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        audioIndicatorOne.setAlignment(Pos.BOTTOM_CENTER);

        for (int i = auIndicatorCirclesOne.length - 1; i >= 0; i--) {
            Circle dot = new Circle();
            dot.setFill(Color.DARKGRAY);

            StackPane circleWrapper = new StackPane(dot);
            circleWrapper.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            circleWrapper.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
            VBox.setVgrow(circleWrapper, Priority.ALWAYS);

            // Bind the radius to a fraction of the container height
            dot.radiusProperty().bind(audioIndicatorOne.heightProperty().multiply(0.012));
            audioIndicatorOne.getChildren().add(circleWrapper);

            auIndicatorCirclesOne[i] = dot;
        }

        GridPane.setRowSpan(audioIndicatorOne, 5);
        primaryPane.add(audioIndicatorOne, 4, maxCols - 6);
    }

    /**
     * Creates the GUI elements in the right channel column.
     */
    private void initializeChannelTwo() {
        ToggleButton channelTwoCue = new ToggleButton(); // Button that can be tied to boolean value
        channelTwoCue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelTwoCue.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        channelTwoCue.setBackground(Background.EMPTY);

        channelTwoCue.setScaleY(0.9);
        channelTwoCue.setScaleX(0.9);

        primaryPane.add(channelTwoCue, maxCols - 3, maxCols - 1);

        // Image overlay for channel one
        ImageView channelTwoCueImage = new ImageView("/Buttons/cue-passive.png");
        channelTwoCueImage.fitWidthProperty().bind(channelTwoCue.widthProperty());
        channelTwoCueImage.fitHeightProperty().bind(channelTwoCue.heightProperty());
        channelTwoCueImage.setMouseTransparent(true); // Allow mouse events to pass through
        channelTwoCueImage.setScaleX(0.9);
        channelTwoCueImage.setScaleY(0.9);
        primaryPane.add(channelTwoCueImage, maxCols - 3, maxCols - 1);

        // Toggle image on button state change
        channelTwoCue.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
          channelTwoCue.isSelected();
          controller.toggleCue(2);
            String imagePath = isNowSelected ? "/Buttons/cue-engaged.png" : "/Buttons/cue-passive.png";
            channelTwoCueImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        });

        channelTwoVolume = new Slider();
        channelTwoVolume.setOrientation(Orientation.VERTICAL);
        channelTwoVolume.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
        channelTwoVolume.setMax(100.0);
        channelTwoVolume.setBlockIncrement(20);
        channelTwoVolume.setShowTickMarks(true);
        channelTwoVolume.setValue(50);
        channelTwoVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            controller.setChannelTwoVolume(newValue.floatValue());
        });
        channelTwoVolume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelTwoVolume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

        GridPane.setRowSpan(channelTwoVolume, 3);
        primaryPane.add(channelTwoVolume, maxCols - 3, maxCols - 4);

        channelTwoBass = new CircularSlider(9, false, "/Knobs/knob-blue-fg.png");
        channelTwoBass.valueProperty().addListener((observable, oldValue, newValue) -> {
            float bassGain = newValue.floatValue();
            // Scale value from 0–270 to 0–100dB
            float bass = (bassGain / 270) * 100;
            controller.setBass2(bass);
        });
        channelTwoBass.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelTwoBass.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(channelTwoBass, maxCols - 3, maxCols - 5);

        ImageView channelTwoBassImg = new ImageView("/Knobs/knob-bg.png");
        channelTwoBassImg.fitWidthProperty().bind(channelTwoBass.widthProperty());
        channelTwoBassImg.fitHeightProperty().bind(channelTwoBass.heightProperty());
        channelTwoBassImg.setMouseTransparent(true);

        primaryPane.add(channelTwoBassImg, maxCols - 3, maxCols - 5);

        channelTwoTreble = new CircularSlider(9, false, "/Knobs/knob-green-fg.png");
        channelTwoTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Scale the value from 0-270 to 0-100dB
            float trebleGain = newValue.floatValue();
            trebleGain = (trebleGain / 270) * 100;
            controller.setTreble2(trebleGain);
        });
        channelTwoTreble.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelTwoTreble.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(channelTwoTreble, maxCols - 3, maxCols - 6);

        ImageView channelTwoTrebleImg = new ImageView("/Knobs/knob-bg.png");
        channelTwoTrebleImg.fitWidthProperty().bind(channelTwoTreble.widthProperty());
        channelTwoTrebleImg.fitHeightProperty().bind(channelTwoTreble.heightProperty());
        channelTwoTrebleImg.setMouseTransparent(true);

        primaryPane.add(channelTwoTrebleImg, maxCols - 3, maxCols - 6);

        channelTwoSpeed = new CircularSlider(9, false, "/Knobs/knob-red-fg.png");
        channelTwoSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            double rawValue = newValue.doubleValue(); // 0.0 - 270.0
            // Map 0.0 - 270.0 to 0.8 - 1.2
            double mappedValue = 1.2 - (rawValue / 270.0) * (1.2 - 0.8);
            // Round to 2 decimal places
            mappedValue = Math.round(mappedValue * 100.0) / 100.0;
            controller.setPlaybackSpeedCh2(mappedValue);
        });
        channelTwoSpeed.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        channelTwoSpeed.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        primaryPane.add(channelTwoSpeed, maxCols - 3, maxCols - 7);

        ImageView channelTwoSpeedImg = new ImageView("/Knobs/knob-bg.png");
        channelTwoSpeedImg.fitWidthProperty().bind(channelTwoSpeed.widthProperty());
        channelTwoSpeedImg.fitHeightProperty().bind(channelTwoSpeed.heightProperty());
        channelTwoSpeedImg.setMouseTransparent(true);

        primaryPane.add(channelTwoSpeedImg, maxCols - 3, maxCols - 7);

        VBox audioIndicatorTwo = new VBox(8);
        audioIndicatorTwo.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        audioIndicatorTwo.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        audioIndicatorTwo.setAlignment(Pos.BOTTOM_CENTER);
        audioIndicatorTwo.setMouseTransparent(true);
        audioIndicatorTwo.setPickOnBounds(false);

        for (int i = auIndicatorCirclesTwo.length - 1; i >= 0; i--) {
            Circle dot = new Circle();
            dot.setFill(Color.DARKGRAY);
            dot.setMouseTransparent(true);
            dot.setPickOnBounds(false);

            StackPane circleWrapper = new StackPane(dot);
            circleWrapper.setMouseTransparent(true);
            circleWrapper.setPickOnBounds(false);
            circleWrapper.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            circleWrapper.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
            VBox.setVgrow(circleWrapper, Priority.ALWAYS);

            // Bind the radius to a fraction of the container height
            dot.radiusProperty().bind(audioIndicatorTwo.heightProperty().multiply(0.012));
            audioIndicatorTwo.getChildren().add(circleWrapper);

            auIndicatorCirclesTwo[i] = dot;
        }

        GridPane.setRowSpan(audioIndicatorTwo, 5);
        primaryPane.add(audioIndicatorTwo, maxCols - 4, maxCols - 6);
    }

    /**
     * Updates the audio indicator with colours corresponding to sound intensity.
     * @param rms Root-mean-square, a mathematical calculation representing an averaged
     *            intensity of sound over a given time
     */
    public void updateAudioIndicatorOne(double rms) {
        int totalDots = auIndicatorCirclesOne.length;
        int activeDots = Math.min((int) (rms * totalDots * 5), totalDots);

        Platform.runLater(() -> {
            for (int i = 0; i < totalDots; i++) {
                Color targetColor;

                if (i < activeDots) {
                    targetColor = (i < 8) ? Color.LIGHTGREEN : (i < 13) ? Color.GOLD : Color.RED;
                } else {
                    targetColor = Color.GRAY;
                }
                if (!auIndicatorCirclesOne[i].getFill().equals(targetColor)) {
                    auIndicatorCirclesOne[i].setFill(targetColor);
                }
            }
        });
    }

    /**
     * Updates the audio indicator with colours corresponding to sound intensity.
     * @param rms Root-mean-square, a mathematical calculation representing an averaged
     *            intensity of sound over a given time
     */
    public void updateAudioIndicatorTwo(double rms) {
        int totalDots = auIndicatorCirclesTwo.length;
        int activeDots = Math.min((int) (rms * totalDots * 5), totalDots);

        Platform.runLater(() -> {
            for (int i = 0; i < totalDots; i++) {
                Color targetColor;

                if (i < activeDots) {
                    targetColor = (i < 8) ? Color.LIGHTGREEN : (i < 13) ? Color.GOLD : Color.RED;
                } else {
                    targetColor = Color.GRAY;
                }
                if (!auIndicatorCirclesTwo[i].getFill().equals(targetColor)) {
                    auIndicatorCirclesTwo[i].setFill(targetColor);
                }
            }
        });
    }

    public void resetChannelOneBassTreblePitch() {
        if (channelOneBass != null) {
            channelOneBass.setAngle(135f);
        }
        if (channelOneTreble != null) {
            channelOneTreble.setAngle(135f);
        }
        if (channelOneSpeed != null) {
            channelOneSpeed.setAngle(135f);
        }
    }

    public void resetChannelTwoBassTreblePitch() {
        if (channelTwoBass != null) {
            channelTwoBass.setAngle(135f);
        }
        if (channelTwoTreble != null) {
            channelTwoTreble.setAngle(135f);
        }
        if (channelTwoSpeed != null) {
            channelTwoSpeed.setAngle(135f);
        }
    }

    public void resetChannelOneVolumeSlider(){
        channelOneVolume.setValue(50);
    }

    public void resetChannelTwoVolumeSlider(){
        channelTwoVolume.setValue(50);
    }
}
