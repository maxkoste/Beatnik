package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class LeftPnl {
	private MainFrame mainFrame;
	private GridPane primaryPane;
	private int maxCols;
	private Soundboard soundboard;

	public LeftPnl(Soundboard soundboard, MainFrame mainFrame, GridPane primaryPane, int maxCols) {
		this.soundboard = soundboard;
		this.mainFrame = mainFrame;
		this.primaryPane = primaryPane;
		this.maxCols = maxCols - 1;

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
		primaryPane.add(songsButton, 1, 5);

		Button soundboardButton = new Button("");
		soundboardButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		soundboardButton.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		soundboardButton.setOnAction(soundboard);
		soundboardButton.getStylesheets().add("soundboardButton.css");
		soundboardButton.setScaleY(0.9);
		soundboardButton.setScaleX(0.9);
		// quantize.setScaleX(0.8);
		// quantize.setScaleY(0.8);
		primaryPane.add(soundboardButton, 1, 7);

		CircularSlider cueVolume = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
		cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
			double volume = newValue.doubleValue();
			System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
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
