package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class LeftPnl {
  private MainFrame mainFrame;
  private GridPane primaryPane;
  private int maxCols;

  public LeftPnl(MainFrame mainFrame, GridPane primaryPane, int maxCols) {
    this.mainFrame = mainFrame;
    this.primaryPane = primaryPane;
    this.maxCols = maxCols -1;

    initialize();
  }

  private void initialize() {
    Button songsButton = new Button("⏏");
    songsButton.setOnAction(mainFrame);
    songsButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    songsButton.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

    primaryPane.add(songsButton, 1, 5);

    CircularSlider quantize = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
    quantize.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    quantize.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    quantize.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    primaryPane.add(quantize, 1, 8);

    // image for the knob
    ImageView quantizeImg = new ImageView("/Knobs/knob-bg.png");
    quantizeImg.fitWidthProperty().bind(quantize.widthProperty());
    quantizeImg.fitHeightProperty().bind(quantize.heightProperty());
    quantizeImg.setMouseTransparent(true);
    primaryPane.add(quantizeImg, 1, 8);

    Label quantizeLabel = new Label("Quantizer");
    quantizeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    quantizeLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    quantizeLabel.setAlignment(Pos.TOP_CENTER);
    GridPane.setColumnSpan(quantizeLabel, 3);
    primaryPane.add(quantizeLabel, 0, 9);

    CircularSlider cueVolume = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
    cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    cueVolume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    cueVolume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    primaryPane.add(cueVolume, 1, maxCols -1);

    ImageView cueImg = new ImageView("/Knobs/knob-bg.png");
    cueImg.fitWidthProperty().bind(cueVolume.widthProperty());
    cueImg.fitHeightProperty().bind(cueVolume.heightProperty());
    cueImg.setMouseTransparent(true);
    primaryPane.add(cueImg, 1, maxCols -1);

    Label cueVolumeLabel = new Label("Cue Volume");
    cueVolumeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    cueVolumeLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    cueVolumeLabel.setAlignment(Pos.TOP_CENTER);
    GridPane.setColumnSpan(cueVolumeLabel, 3);
    primaryPane.add(cueVolumeLabel, 0, maxCols);
  }
}
