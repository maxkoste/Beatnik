package view;

import controller.Controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MainFrame implements EventHandler<ActionEvent> {
  AnchorPane primaryPane;
  BorderPane songsPane;
  Stage playlistStage;
  SelectionModel songSelector;
  Controller controller;
  ObservableList<String> songs = FXCollections.observableArrayList(); // Temp implementation but correct class/collection
  TextArea channelOneContainer;
  double screenHeight;
  double screenWidth;

  public MainFrame(Controller controller) {
    this.controller = controller;
  }

  public void start(Stage primaryStage) {
    Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
    screenHeight = screenResolution.getHeight();
    screenWidth = screenResolution.getWidth();

    primaryStage.setTitle("Beatnik");
    primaryStage.setResizable(false);
    primaryStage.setMaximized(true);

    playlistStage = new Stage();
    playlistStage.setTitle("Songs and Playlists");
    playlistStage.setResizable(false);

    primaryPane = new AnchorPane(); // Pane which contains all content
    songsPane = new BorderPane(); // Pane which contains playlist popup content

    initializeZoneOne();
    initializeZoneTwo();
    initializeZoneThree();
    initializeZoneFour();

    Scene primaryScene = new Scene(primaryPane, screenWidth, screenHeight); // Add pane to scene

    Scene playlistScene = new Scene(songsPane, 400, 600);

    Image flowers = new Image("flowers.JPG"); // Add icon
    primaryStage.getIcons().add(flowers);
    primaryStage.setScene(primaryScene); // Finalize window to be shown
    primaryStage.show();

    playlistStage.setScene(playlistScene);
    playlistStage.initModality(Modality.APPLICATION_MODAL);
  }

  private void initializeZoneOne() {
    Button songsButton = new Button();
    songsButton.setText("⏏");
    songsButton.setOnAction(this);
    AnchorPane.setTopAnchor(songsButton, screenHeight / 10);
    AnchorPane.setLeftAnchor(songsButton, screenWidth / 10);

      ListView<String> songList = new ListView<>(songs);
      songSelector = songList.getSelectionModel();
      songList.setOnMouseClicked(this::handleSongSelection);
      songsPane.setCenter(songList);

      Button importSongs = new Button();
      importSongs.setText("Import");
      importSongs.setOnAction(this::handleImport);

      Button viewPlaylists = new Button();
      viewPlaylists.setText("Playlists");

      ToolBar songsMenu = new ToolBar(importSongs, viewPlaylists);
      songsPane.setTop(songsMenu);

    Button quantize = new Button(); // Temporary implementation
    quantize.setText("Q");
    quantize.setOnAction(this::handleQuantizer);
    AnchorPane.setTopAnchor(quantize, screenHeight / 2);
    AnchorPane.setLeftAnchor(quantize, screenWidth / 10);

    CircularSlider cueVolume = new CircularSlider();
    cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(cueVolume, screenHeight / 1.15);
    AnchorPane.setLeftAnchor(cueVolume, screenWidth / 10);

    primaryPane.getChildren().addAll(songsButton, quantize, cueVolume);
  }

  private void initializeZoneTwo() {
    channelOneContainer = new TextArea(); // Temporary implementation maybe try splitPane?
    channelOneContainer.setPrefSize(800.0, 75.0);
    AnchorPane.setTopAnchor(channelOneContainer, 75.0);
    AnchorPane.setLeftAnchor(channelOneContainer, (((screenWidth / 2)) - (800 / 2)));

    TextArea channelTwoContainer = new TextArea(); // Temporary implementation
    channelTwoContainer.setPrefSize(800.0, 75.0);
    AnchorPane.setTopAnchor(channelTwoContainer, (75.0 * 2));
    AnchorPane.setLeftAnchor(channelTwoContainer, (((screenWidth / 2)) - (800 / 2)));

    Button channelOnePlayPause = new Button();
    channelOnePlayPause.setPrefSize(20.0, 75.0);
    channelOnePlayPause.setText("⏯");
    channelOnePlayPause.setOnAction(this::handleChannelOnePlayPause);
    AnchorPane.setTopAnchor(channelOnePlayPause, 75.0);
    AnchorPane.setLeftAnchor(channelOnePlayPause, ((((screenWidth / 2)) - (800 / 2)) - 20));

    Button channelTwoPlayPause = new Button();
    channelTwoPlayPause.setPrefSize(20.0, 75.0);
    channelTwoPlayPause.setText("⏯");
    channelTwoPlayPause.setOnAction(this::handleChannelTwoPlayPause);
    AnchorPane.setTopAnchor(channelTwoPlayPause, 150.0);
    AnchorPane.setLeftAnchor(channelTwoPlayPause, ((((screenWidth / 2)) - (800 / 2)) - 20));

    primaryPane.getChildren().addAll(channelOneContainer, channelTwoContainer,
        channelOnePlayPause, channelTwoPlayPause);
  }

  private void initializeZoneThree() {
    Slider crossFader = new Slider();
    crossFader.setPrefSize((screenWidth / 10), (screenHeight / 20));
    crossFader.setMax(100);
    crossFader.setBlockIncrement(20);
    crossFader.setShowTickMarks(true);
    crossFader.setValue(50);
    crossFader.setOnDragDetected(this::handleCrossFader);
    AnchorPane.setTopAnchor(crossFader, (screenHeight / 1.115));
    AnchorPane.setLeftAnchor(crossFader, ((screenWidth / 2) - (crossFader.getPrefWidth() / 2)));

    Label crossFaderLabel = new Label();
    crossFaderLabel.setPrefSize((screenWidth / 45), (screenHeight / 100));
    crossFaderLabel.setText("Crossfader");
    AnchorPane.setTopAnchor(crossFaderLabel, (screenHeight / 1.075));
    AnchorPane.setLeftAnchor(crossFaderLabel, ((screenWidth / 2) - (crossFaderLabel.getPrefWidth() / 2)));

    ToggleButton channelOneCue = new ToggleButton(); // Button that can be tied to boolean value
    channelOneCue.setPrefSize((screenWidth / 60), (screenHeight / 100));
    channelOneCue.setText("CUE");
    AnchorPane.setTopAnchor(channelOneCue, (screenHeight / 1.1));
    AnchorPane.setLeftAnchor(channelOneCue, (screenWidth / 2.33) - (channelOneCue.getPrefWidth() / 2));

    ToggleButton channelTwoCue = new ToggleButton(); // Button that can be tied to boolean value
    channelTwoCue.setPrefSize((screenWidth / 60), (screenHeight / 100));
    channelTwoCue.setText("CUE");
    AnchorPane.setTopAnchor(channelTwoCue, (screenHeight / 1.1));
    AnchorPane.setLeftAnchor(channelTwoCue, (screenWidth / 1.75) - (channelTwoCue.getPrefWidth() / 2));

    Slider channelOneVolume = new Slider();
    channelOneVolume.setOrientation(Orientation.VERTICAL);
    channelOneVolume.setMax(100.0);
    channelOneVolume.setBlockIncrement(20);
    channelOneVolume.setShowTickMarks(true);
    AnchorPane.setTopAnchor(channelOneVolume, (screenHeight / 1.25));
    AnchorPane.setLeftAnchor(channelOneVolume, (screenWidth / 2.342));

    Slider channelTwoVolume = new Slider();
    channelTwoVolume.setOrientation(Orientation.VERTICAL);
    channelTwoVolume.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    channelTwoVolume.setMax(100.0);
    channelTwoVolume.setBlockIncrement(20);
    channelTwoVolume.setShowTickMarks(true);
    AnchorPane.setTopAnchor(channelTwoVolume, (screenHeight / 1.25));
    AnchorPane.setLeftAnchor(channelTwoVolume, (screenWidth / 1.77));

    CircularSlider channelOneBass = new CircularSlider();
    channelOneBass.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelOneBass, screenHeight / 1.35);
    AnchorPane.setLeftAnchor(channelOneBass, screenWidth / 2.385);

    CircularSlider channelTwoBass = new CircularSlider();
    channelTwoBass.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelTwoBass, (screenHeight / 1.35));
    AnchorPane.setLeftAnchor(channelTwoBass, (screenWidth / 1.78));

    CircularSlider channelOneTreble = new CircularSlider();
    channelOneTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelOneTreble, (screenHeight / 1.45));
    AnchorPane.setLeftAnchor(channelOneTreble, (screenWidth / 2.385));

    CircularSlider channelTwoTreble = new CircularSlider();
    channelTwoTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelTwoTreble, (screenHeight / 1.45));
    AnchorPane.setLeftAnchor(channelTwoTreble, (screenWidth / 1.78));

    CircularSlider channelOneSpeed = new CircularSlider();
    channelOneSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelOneSpeed, (screenHeight / 1.565));
    AnchorPane.setLeftAnchor(channelOneSpeed, (screenWidth / 2.385));

    CircularSlider channelTwoSpeed = new CircularSlider();
    channelTwoSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelTwoSpeed, (screenHeight / 1.565));
    AnchorPane.setLeftAnchor(channelTwoSpeed, (screenWidth / 1.78));

    ScrollBar channelOneVolumeIndicator = new ScrollBar(); // Temporary implementation
    channelOneVolumeIndicator.setPrefSize(1.0, 308.0);
    channelOneVolumeIndicator.setOrientation(Orientation.VERTICAL);
    AnchorPane.setTopAnchor(channelOneVolumeIndicator, (screenHeight / 1.47));
    AnchorPane.setLeftAnchor(channelOneVolumeIndicator, (screenWidth / 2.2));

    ScrollBar channelTwoVolumeIndicator = new ScrollBar(); // Temporary implementation
    channelTwoVolumeIndicator.setPrefSize(1.0, 308.0);
    channelTwoVolumeIndicator.setOrientation(Orientation.VERTICAL);
    AnchorPane.setTopAnchor(channelTwoVolumeIndicator, (screenHeight / 1.47));
    AnchorPane.setLeftAnchor(channelTwoVolumeIndicator, (screenWidth / 1.85));

    primaryPane.getChildren().addAll(crossFader, crossFaderLabel, channelOneCue, channelTwoCue, channelOneVolume,
        channelTwoVolume, channelOneBass, channelTwoBass, channelOneTreble, channelTwoTreble, channelOneSpeed, channelTwoSpeed,
        channelOneVolumeIndicator, channelTwoVolumeIndicator);
  }

  private void initializeZoneFour() {
    CircularSlider effectIntensity = new CircularSlider();
    effectIntensity.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(effectIntensity, screenHeight / 10);
    AnchorPane.setLeftAnchor(effectIntensity, screenWidth / 1.1);

    Button effectSelector = new Button(); // Temporary implementation
    effectSelector.setText("E");
    AnchorPane.setTopAnchor(effectSelector, screenHeight / 2);
    AnchorPane.setLeftAnchor(effectSelector, (screenWidth / 1.1));

    Slider masterVolume = new Slider();
    masterVolume.setOrientation(Orientation.VERTICAL);
    masterVolume.setMax(100.0);
    masterVolume.setBlockIncrement(20);
    masterVolume.setMinorTickCount(0);
    masterVolume.setShowTickLabels(true);
    AnchorPane.setTopAnchor(masterVolume, (screenHeight / 1.25));
    AnchorPane.setLeftAnchor(masterVolume, (screenWidth / 1.1));

    // listener for setting the volume
    masterVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      int volume = newValue.intValue();
      System.out.println("volume: " + volume);
      controller.setMasterVolume(volume);
    });

    masterVolume.setValue(50);

    Label masterVolumeLabel = new Label();
    masterVolumeLabel.setText("Master Volume");
    AnchorPane.setTopAnchor(masterVolumeLabel, (screenHeight / 1.1));
    AnchorPane.setLeftAnchor(masterVolumeLabel, (screenWidth / 1.115));

    primaryPane.getChildren().addAll(effectIntensity, effectSelector, masterVolume, masterVolumeLabel);
  }

  public void addSongs(String[] songPaths) {
    songs.addAll(songPaths);
  }

  public void addSong(String songPath) {
    songs.add(songPath);
  }

  @Override
  public void handle(ActionEvent actionEvent) {
    playlistStage.showAndWait();
    System.out.println("Playlist Button");
  }

  public void handleSongSelection(MouseEvent mouseEvent) {
    if(mouseEvent.getClickCount() == 2) {
      controller.setSong(1, songs.get(songSelector.getSelectedIndex())); // set only to channel 1 for now
      channelOneContainer.setText("Song loaded: " + songs.get(songSelector.getSelectedIndex()));
    }
  }

  public void handleImport(ActionEvent actionEvent) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Import a song");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3"));
    File selectedFile = fileChooser.showOpenDialog(playlistStage);
    if (selectedFile != null) {
      try {
        controller.moveFile(selectedFile, String.valueOf(Paths.get("src/main/resources/songs/" + selectedFile.getName())));
      } catch (IOException e) {
        System.out.println("File could not be moved");;
      }
    }
  }

  private void handleQuantizer(ActionEvent actionEvent) {
    System.out.println("Quantizer");
  }

  private void handleCueVolume(ActionEvent actionEvent) {
    System.out.println("Cue Volume");
  }

  private void handleChannelOnePlayPause(ActionEvent actionEvent) {
    System.out.println("Channel One Play/Pause");
    controller.playSong();
  }

  private void handleChannelTwoPlayPause(ActionEvent actionEvent) {
    System.out.println("Channel Two Play/Pause");
  }

  private void handleCrossFader(MouseEvent mouseEvent) {
    System.out.println("Crossfader");
  }

}