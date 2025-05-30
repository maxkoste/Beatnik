package view;

import controller.Controller;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class LeftPnl {
	private MainFrame mainFrame;
	private GridPane primaryPane;

	private Controller controller;
	private int maxCols;
	private Soundboard soundboard;

	public LeftPnl(Soundboard soundboard, MainFrame mainFrame, GridPane primaryPane, int maxCols,
			Controller controller) {

		this.soundboard = soundboard;
		this.mainFrame = mainFrame;
		this.primaryPane = primaryPane;
		this.maxCols = maxCols - 1;

		this.controller = controller;

		initialize();
	}

	private void initialize() {
		Button songsButton = new Button("⏏");
		songsButton.setOnAction(mainFrame);
		songsButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		songsButton.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

		songsButton.getStylesheets().add("importButton.css");
		songsButton.setScaleY(0.9);
		songsButton.setScaleX(0.9);

		songsButton.setText("");
		ImageView image2 = new ImageView(new Image("/Symbols/eject.png"));
		image2.setFitHeight(songsButton.getHeight());
		image2.setFitWidth(songsButton.getWidth());
		image2.setScaleY(0.25);
		image2.setScaleX(0.25);
		image2.setPreserveRatio(true);
		songsButton.setGraphic(image2);
		songsButton.setScaleY(0.8);
		songsButton.setScaleX(0.8);
		primaryPane.add(songsButton, 1, 5);

		Button soundboardButton = new Button("");
		soundboardButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		soundboardButton.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		soundboardButton.setOnAction(soundboard);
		soundboardButton.getStylesheets().add("soundboardButton.css");
		soundboardButton.setScaleY(0.8);
		soundboardButton.setScaleX(0.8);

		ImageView image = new ImageView(new Image("/Symbols/soundboard.png"));
		image.setFitHeight(soundboardButton.getHeight());
		image.setFitWidth(soundboardButton.getWidth());
		image.setScaleY(0.25);
		image.setScaleX(0.25);
		image.setPreserveRatio(true);
		soundboardButton.setGraphic(image);
		// quantize.setScaleX(0.8);
		// quantize.setScaleY(0.8);
		primaryPane.add(soundboardButton, 1, 8);

		CircularSlider cueVolume = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
		cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
			double value = newValue.doubleValue();

			int volume;
			if (value < 0.2) {
				volume = 0;
			} else {
				volume = (int) Math.ceil(value / 2.7);
			}

			System.out.println("volume: " + volume);
			controller.setCueVolume(1, volume);

		});
		cueVolume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		cueVolume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		// cueVolume.setScaleX(0.8);
		// cueVolume.setScaleY(0.8);
		primaryPane.add(cueVolume, 1, maxCols - 1);

		ImageView cueImg = new ImageView("/Knobs/knob-bg.png");
		cueImg.fitWidthProperty().bind(cueVolume.widthProperty());
		cueImg.fitHeightProperty().bind(cueVolume.heightProperty());
		cueImg.setMouseTransparent(true);
		// cueImg.setScaleX(0.8);
		// cueImg.setScaleY(0.8);
		primaryPane.add(cueImg, 1, maxCols - 1);

		Label cueVolumeLabel = new Label("Cue Volume");
		cueVolumeLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		cueVolumeLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		cueVolumeLabel.setAlignment(Pos.TOP_CENTER);
		GridPane.setColumnSpan(cueVolumeLabel, 3);
		primaryPane.add(cueVolumeLabel, 0, maxCols);
	}
}
