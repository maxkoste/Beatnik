package view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class TopPnl {
  private MainFrame mainFrame;
  private GridPane primaryPane;
  private int maxCols;
  private Label waveformOneText;
  private Label waveformTwoText;
  private WaveFormCanvas waveFormOne;
  private WaveFormCanvas waveFormTwo;

  public TopPnl(MainFrame mainFrame, GridPane primaryPane, int maxCols) {
    this.mainFrame = mainFrame;
    this.primaryPane = primaryPane;
    this.maxCols = maxCols -1;
    initializeButtons();
    initializeWaveForms();
  }

  private void initializeButtons() {
    Button channelOnePlayPause = new Button("⏯");
    channelOnePlayPause.setOnAction(mainFrame::handleChannelOnePlayPause);
    channelOnePlayPause.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    channelOnePlayPause.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    primaryPane.add(channelOnePlayPause, 0, 0);

    Button channelTwoPlayPause = new Button("⏯");
    channelTwoPlayPause.setOnAction(mainFrame::handleChannelTwoPlayPause);
    channelTwoPlayPause.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    channelTwoPlayPause.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    primaryPane.add(channelTwoPlayPause, 0, 2);

    Button channelOneTrackCue = new Button("C");
    channelOneTrackCue.setOnAction(mainFrame::handleChannelOneTrackCue);
    channelOneTrackCue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    channelOneTrackCue.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    primaryPane.add(channelOneTrackCue, 0 ,1);

    Button channelTwoTrackCue = new Button("C");
    channelTwoTrackCue.setOnAction(mainFrame::handleChannelTwoTrackCue);
    channelTwoTrackCue.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    channelTwoTrackCue.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    primaryPane.add(channelTwoTrackCue, 0, 3);

    Button channelOneSkip = new Button("⏭");
    channelOneSkip.setOnAction(mainFrame::handleChannelOneSkip);
    channelOneSkip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    channelOneSkip.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    GridPane.setRowSpan(channelOneSkip, 2);
    primaryPane.add(channelOneSkip, maxCols, 0);

    Button channelTwoSkip = new Button("⏭");
    channelTwoSkip.setOnAction(mainFrame::handleChannelTwoSkip);
    channelTwoSkip.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    channelTwoSkip.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
    GridPane.setRowSpan(channelTwoSkip, 2);
    primaryPane.add(channelTwoSkip, maxCols, 2);
  }

  private void initializeWaveForms() {
    StackPane waveformOneContainer = new StackPane();
    waveformOneContainer.setAlignment(Pos.TOP_LEFT);
    GridPane.setRowSpan(waveformOneContainer, 2);
    GridPane.setColumnSpan(waveformOneContainer, maxCols -1);
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
    GridPane.setColumnSpan(waveformTwoContainer, maxCols -1);
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

  public void setInfoText(boolean playlist, String song, int channel) {
    if (channel == 1) {
      if (playlist) {
        waveformOneText.setText("Playing " + song + " in " + mainFrame.getSelectedPlaylist());
      } else {
        waveformOneText.setText("Playing " + song);
      }
    } else {
      if (playlist) {
        waveformTwoText.setText("Playing " + song + " in " + mainFrame.getSelectedPlaylist());
      } else {
        waveformTwoText.setText("Playing " + song);
      }
    }
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
