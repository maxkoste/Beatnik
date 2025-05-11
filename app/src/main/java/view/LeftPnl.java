package view;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class LeftPnl {
  private MainFrame mainFrame;
  private GridPane primaryPane;

  public LeftPnl(MainFrame mainFrame, GridPane primaryPane) {
    this.mainFrame = mainFrame;
    this.primaryPane = primaryPane;

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
    ImageView quantizeImg = new ImageView(new Image("/Knobs/knob-bg.png"));
    quantizeImg.fitWidthProperty().bind(quantize.widthProperty());
    quantizeImg.fitHeightProperty().bind(quantize.heightProperty());
    quantizeImg.setMouseTransparent(true);
    primaryPane.add(quantizeImg, 1, 8);

    Label quantizeLabel = new Label("Quantizer");
    GridPane.setColumnSpan(quantizeLabel, 3);
    primaryPane.add(quantizeLabel, 1, 9);


  }
}
