package view;

import controller.Controller;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class TopPnl {
	private MainFrame mainFrame;
	private Controller controller;
	private GridPane primaryPane;
	private int maxCols;
	private Label waveformOneText;
	private Label waveformTwoText;
	private WaveFormCanvas waveFormOne;
	private WaveFormCanvas waveFormTwo;
	private Button[] buttons = new Button[6];

	public TopPnl(MainFrame mainFrame, Controller controller, GridPane primaryPane, int maxCols) {
		this.mainFrame = mainFrame;
		this.controller = controller;
		this.primaryPane = primaryPane;
		this.maxCols = maxCols - 1;
		initializeButtons();
		initializeWaveForms();
	}

	/**
	 * Creates the GUI buttons on top of the screen.
	 */
	private void initializeButtons() {
		Button channelOnePlayPause = new Button();
		channelOnePlayPause.setOnAction(this::handleChannelOnePlayPause);
		channelOnePlayPause.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		channelOnePlayPause.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		primaryPane.add(channelOnePlayPause, 0, 0);
		channelOnePlayPause.getStylesheets().add("topPanel.css");
		buttons[0] = channelOnePlayPause;

		Button channelTwoPlayPause = new Button();
		channelTwoPlayPause.setOnAction(this::handleChannelTwoPlayPause);
		channelTwoPlayPause.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		channelTwoPlayPause.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		primaryPane.add(channelTwoPlayPause, 0, 2);
		channelTwoPlayPause.getStylesheets().add("topPanel.css");
		buttons[1] = channelTwoPlayPause;

		Button channelOneTrackCue = new Button();
		channelOneTrackCue.setOnAction(this::handleChannelOneTrackCue);
		channelOneTrackCue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		channelOneTrackCue.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		primaryPane.add(channelOneTrackCue, 0, 1);
		channelOneTrackCue.getStylesheets().add("topPanel.css");
		buttons[2] = channelOneTrackCue;

		Button channelTwoTrackCue = new Button();
		channelTwoTrackCue.setOnAction(this::handleChannelTwoTrackCue);
		channelTwoTrackCue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		channelTwoTrackCue.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		primaryPane.add(channelTwoTrackCue, 0, 3);
		channelTwoTrackCue.getStylesheets().add("topPanel.css");
		buttons[3] = channelTwoTrackCue;

		Button channelOneSkip = new Button();
		channelOneSkip.setOnAction(this::handleChannelOneSkip);
		channelOneSkip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		channelOneSkip.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		GridPane.setRowSpan(channelOneSkip, 2);
		primaryPane.add(channelOneSkip, maxCols, 0);
		channelOneSkip.getStylesheets().add("topPanel.css");
		buttons[4] = channelOneSkip;

		Button channelTwoSkip = new Button();
		channelTwoSkip.setOnAction(this::handleChannelTwoSkip);
		channelTwoSkip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		channelTwoSkip.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		GridPane.setRowSpan(channelTwoSkip, 2);
		primaryPane.add(channelTwoSkip, maxCols, 2);
		channelTwoSkip.getStylesheets().add("topPanel.css");
		buttons[5] = channelTwoSkip;

		String[] resources = { "/Symbols/play_pause.png", "/Symbols/play_pause.png", "/Symbols/replay.png",
				"/Symbols/replay.png", "/Symbols/skip_next.png", "/Symbols/skip_next.png" };
		int counter = 0;

		for (Button button : buttons) {
			button.setScaleY(0.8);
			button.setScaleX(0.8);
			ImageView image = new ImageView(new Image(resources[counter]));
			image.setScaleY(0.4);
			image.setScaleX(0.4);
			image.setFitHeight(button.getWidth());
			image.setFitWidth(button.getWidth());
			image.setPreserveRatio(true);
			button.setGraphic(image);
			counter++;
		}
	}

	/**
	 * Creates the containers for the waveform graphics on top of the screen.
	 */
	private void initializeWaveForms() {
		StackPane waveformOneContainer = new StackPane();
		waveformOneContainer.setAlignment(Pos.TOP_LEFT);
		GridPane.setRowSpan(waveformOneContainer, 2);
		GridPane.setColumnSpan(waveformOneContainer, maxCols - 1);
		waveformOneContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		waveformOneContainer.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

		waveformOneText = new Label();
		waveformOneText.setBackground(Background.EMPTY);

		waveFormOne = new WaveFormCanvas();
		waveFormOne.widthProperty().bind(waveformOneContainer.widthProperty());
		waveFormOne.heightProperty().bind(waveformOneContainer.heightProperty());

		waveformOneContainer.getChildren().addAll(waveFormOne, waveformOneText);
		primaryPane.add(waveformOneContainer, 1, 0);

		StackPane waveformTwoContainer = new StackPane();
		waveformTwoContainer.setAlignment(Pos.TOP_LEFT);
		GridPane.setRowSpan(waveformTwoContainer, 2);
		GridPane.setColumnSpan(waveformTwoContainer, maxCols - 1);
		waveformTwoContainer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		waveformTwoContainer.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

		waveformTwoText = new Label();
		waveformTwoText.setBackground(Background.EMPTY);

		waveFormTwo = new WaveFormCanvas();
		waveFormTwo.widthProperty().bind(waveformTwoContainer.widthProperty());
		waveFormTwo.heightProperty().bind(waveformTwoContainer.heightProperty());

		waveformTwoContainer.getChildren().addAll(waveFormTwo, waveformTwoText);
		primaryPane.add(waveformTwoContainer, 1, 2);
	}

	/**
	 * Sets the informational text on the waveform graphic.
	 */
	public void setInfoText(boolean playlist, String song, int channel) {
		String songWithoutExtension = song.replaceFirst("\\.(mp3|mp4|wav|flac)$", "");
		if (channel == 1) {
			if (playlist) {
				waveformOneText.setText(" Playing " + songWithoutExtension + " in " + mainFrame.getSelectedPlaylist());
			} else {
				waveformOneText.setText(" Playing " + songWithoutExtension);
			}
		} else {
			if (playlist) {
				waveformTwoText.setText(" Playing " + songWithoutExtension + " in " + mainFrame.getSelectedPlaylist());
			} else {
				waveformTwoText.setText(" Playing " + songWithoutExtension);
			}
		}
	}

	public void handleChannelOnePlayPause(ActionEvent actionEvent) {
		controller.playSong(1);
	}

	public void handleChannelTwoPlayPause(ActionEvent actionEvent) {
		controller.playSong(2);
	}

	public void handleChannelOneTrackCue(ActionEvent actionEvent) {
		controller.resetSong(1);
	}

	public void handleChannelTwoTrackCue(ActionEvent actionEvent) {
		controller.resetSong(2);
	}

	public void handleChannelOneSkip(ActionEvent actionEvent) {
		controller.nextSong(1);
	}

	public void handleChannelTwoSkip(ActionEvent actionEvent) {
		controller.nextSong(2);
	}

	public void setWaveformAudioData(float[] originalAudioData, int channel) {
		if (channel == 1) {
			waveFormOne.setOriginalAudioData(originalAudioData);
		} else
			waveFormTwo.setOriginalAudioData(originalAudioData);
	}

	public void updateWaveformOne(float currentSecond) {
		waveFormOne.update(currentSecond);
	}

	public void updateWaveformTwo(float currentSecond) {
		waveFormTwo.update(currentSecond);
	}
}
