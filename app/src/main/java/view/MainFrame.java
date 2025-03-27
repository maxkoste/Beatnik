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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class MainFrame implements EventHandler<ActionEvent> {
  AnchorPane primaryPane;
  Stage playlistStage;
  SelectionModel songSelector;
  Controller controller;
  ObservableList<String> songs = FXCollections.observableArrayList(); // Temp implementation but correct class/collection

  public MainFrame(Stage primaryStage, Controller controller) {
    this.controller = controller;
    start(primaryStage);
  }

  public void start(Stage primaryStage) {
    primaryStage.setTitle("Beatnik");
    primaryStage.setResizable(false);

    playlistStage = new Stage();
    playlistStage.setTitle("Songs and Playlists");
    playlistStage.setResizable(false);

    primaryPane = new AnchorPane(); // Pane which contains all content
    BorderPane songsPane = new BorderPane(); // Pane which contains playlist popup content

    Button songsButton = new Button();
    songsButton.setText("⏏");
    songsButton.setOnAction(this);
    AnchorPane.setTopAnchor(songsButton, 150.0);
    AnchorPane.setLeftAnchor(songsButton, 150.0);

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
    AnchorPane.setTopAnchor(quantize, 450.0);
    AnchorPane.setLeftAnchor(quantize, 150.0);

    Button cueVolume = new Button(); // Temporary implementation
    cueVolume.setText("CV");
    cueVolume.setOnAction(this::handleCueVolume);
    AnchorPane.setTopAnchor(cueVolume, 750.0);
    AnchorPane.setLeftAnchor(cueVolume, 150.0);

    // Zone 2

    TextArea channelOneContainer = new TextArea(); // Temporary implementation maybe try splitPane?
    channelOneContainer.setPrefSize(800.0, 75.0);
    AnchorPane.setTopAnchor(channelOneContainer, 75.0);
    AnchorPane.setLeftAnchor(channelOneContainer, 410.0);

    TextArea channelTwoContainer = new TextArea(); // Temporary implementation
    channelTwoContainer.setPrefSize(800.0, 75.0);
    AnchorPane.setTopAnchor(channelTwoContainer, 150.0);
    AnchorPane.setLeftAnchor(channelTwoContainer, 410.0);

    Button channelOnePlayPause = new Button();
    channelOnePlayPause.setPrefSize(20.0, 75.0);
    channelOnePlayPause.setText("⏯");
    channelOnePlayPause.setOnAction(this::handleChannelOnePlayPause);
    AnchorPane.setTopAnchor(channelOnePlayPause, 75.0);
    AnchorPane.setLeftAnchor(channelOnePlayPause, 390.0);

    Button channelTwoPlayPause = new Button();
    channelTwoPlayPause.setPrefSize(20.0, 75.0);
    channelTwoPlayPause.setText("⏯");
    channelTwoPlayPause.setOnAction(this::handleChannelTwoPlayPause);
    AnchorPane.setTopAnchor(channelTwoPlayPause, 150.0);
    AnchorPane.setLeftAnchor(channelTwoPlayPause, 390.0);

    // Zone 3

    Slider crossFader = new Slider();
    crossFader.setMax(100);
    crossFader.setBlockIncrement(20);
    crossFader.setShowTickMarks(true);
    crossFader.setValue(50);
    crossFader.setOnDragDetected(this::handleCrossFader);
    AnchorPane.setTopAnchor(crossFader, 800.0);
    AnchorPane.setLeftAnchor(crossFader, 750.0);

    Label crossFaderLabel = new Label();
    crossFaderLabel.setText("Crossfader");
    AnchorPane.setTopAnchor(crossFaderLabel, 825.0);
    AnchorPane.setLeftAnchor(crossFaderLabel, 790.0);

    ToggleButton channelOneCue = new ToggleButton(); // Button that can be tied to boolean value
    channelOneCue.setText("CUE");
    AnchorPane.setTopAnchor(channelOneCue, 800.0);
    AnchorPane.setLeftAnchor(channelOneCue, 600.0);

    ToggleButton channelTwoCue = new ToggleButton(); // Button that can be tied to boolean value
    channelTwoCue.setText("CUE");
    AnchorPane.setTopAnchor(channelTwoCue, 800.0);
    AnchorPane.setLeftAnchor(channelTwoCue, 1000.0);

    Slider channelOneVolume = new Slider();
    channelOneVolume.setOrientation(Orientation.VERTICAL);
    channelOneVolume.setMax(100.0);
    channelOneVolume.setBlockIncrement(20);
    channelOneVolume.setShowTickMarks(true);
    AnchorPane.setTopAnchor(channelOneVolume, 640.0);
    AnchorPane.setLeftAnchor(channelOneVolume, 612.0);

    Slider channelTwoVolume = new Slider();
    channelTwoVolume.setOrientation(Orientation.VERTICAL);
    channelTwoVolume.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    channelTwoVolume.setMax(100.0);
    channelTwoVolume.setBlockIncrement(20);
    channelTwoVolume.setShowTickMarks(true);
    AnchorPane.setTopAnchor(channelTwoVolume, 640.0);
    AnchorPane.setLeftAnchor(channelTwoVolume, 1002.0);

    Button channelOneBass = new Button(); // Temporary implementation
    channelOneBass.setText("B");
    AnchorPane.setTopAnchor(channelOneBass, 600.0);
    AnchorPane.setLeftAnchor(channelOneBass, 607.0);

    Button channelTwoBass = new Button(); // Temporary implementation
    channelTwoBass.setText("B");
    AnchorPane.setTopAnchor(channelTwoBass, 600.0);
    AnchorPane.setLeftAnchor(channelTwoBass, 1007.0);

    Button channelOneTreble = new Button(); // Temporary implementation
    channelOneTreble.setText("T");
    AnchorPane.setTopAnchor(channelOneTreble, 560.0);
    AnchorPane.setLeftAnchor(channelOneTreble, 607.0);

    Button channelTwoTreble = new Button(); // Temporary implementation
    channelTwoTreble.setText("T");
    AnchorPane.setTopAnchor(channelTwoTreble, 560.0);
    AnchorPane.setLeftAnchor(channelTwoTreble, 1007.0);

    Button channelOneSpeed = new Button(); // Temporary implementation
    channelOneSpeed.setText("S");
    AnchorPane.setTopAnchor(channelOneSpeed, 520.0);
    AnchorPane.setLeftAnchor(channelOneSpeed, 607.0);

    Button channelTwoSpeed = new Button(); // Temporary implementation
    channelTwoSpeed.setText("S");
    AnchorPane.setTopAnchor(channelTwoSpeed, 520.0);
    AnchorPane.setLeftAnchor(channelTwoSpeed, 1007.0);

    ScrollBar channelOneVolumeIndicator = new ScrollBar(); // Temporary implementation
    channelOneVolumeIndicator.setPrefSize(1.0, 308.0);
    channelOneVolumeIndicator.setOrientation(Orientation.VERTICAL);
    AnchorPane.setTopAnchor(channelOneVolumeIndicator, 460.0);
    AnchorPane.setLeftAnchor(channelOneVolumeIndicator, 935.0);

    ScrollBar channelTwoVolumeIndicator = new ScrollBar(); // Temporary implementation
    channelTwoVolumeIndicator.setPrefSize(1.0, 308.0);
    channelTwoVolumeIndicator.setOrientation(Orientation.VERTICAL);
    AnchorPane.setTopAnchor(channelTwoVolumeIndicator, 460.0);
    AnchorPane.setLeftAnchor(channelTwoVolumeIndicator, 685.0);

    // Zone 4

    Button effectIntensity = new Button(); // Temporary implementation
    effectIntensity.setText("I");
    AnchorPane.setTopAnchor(effectIntensity, 150.0);
    AnchorPane.setLeftAnchor(effectIntensity, 1450.0);

    Button effectSelector = new Button(); // Temporary implementation
    effectSelector.setText("E");
    AnchorPane.setTopAnchor(effectSelector, 450.0);
    AnchorPane.setLeftAnchor(effectSelector, 1450.0);

    Slider masterVolume = new Slider();
    masterVolume.setOrientation(Orientation.VERTICAL);
    masterVolume.setMax(100.0);
    masterVolume.setBlockIncrement(20);
    masterVolume.setShowTickMarks(true);
    masterVolume.setShowTickLabels(true);
    AnchorPane.setTopAnchor(masterVolume, 640.0);
    AnchorPane.setLeftAnchor(masterVolume, 1450.0);

    // listener for setting the volume
    masterVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      float volume = newValue.floatValue();
      System.out.println("volume: " + volume);
      controller.setMasterVolume(volume);
    });

    masterVolume.setValue(50);

    Label masterVolumeLabel = new Label();
    masterVolumeLabel.setText("Master Volume");
    AnchorPane.setTopAnchor(masterVolumeLabel, 800.0);
    AnchorPane.setLeftAnchor(masterVolumeLabel, 1415.0);

    // Add all elements to primaryPane
    primaryPane.getChildren().addAll(songsButton, quantize, cueVolume, channelOneContainer, channelTwoContainer,
        channelOnePlayPause, channelTwoPlayPause, crossFader, crossFaderLabel, channelOneCue, channelTwoCue, channelOneVolume,
        channelTwoVolume, channelOneBass, channelTwoBass, channelOneTreble, channelTwoTreble, channelOneSpeed, channelTwoSpeed,
        channelOneVolumeIndicator, channelTwoVolumeIndicator, effectIntensity, effectSelector, masterVolume, masterVolumeLabel);
    Scene primaryScene = new Scene(primaryPane, 1600, 900); // Add pane to scene

    Scene playlistScene = new Scene(songsPane, 400, 600);

    Image flowers = new Image("flowers.JPG"); // Add icon
    primaryStage.getIcons().add(flowers);
    primaryStage.setScene(primaryScene); // Finalize window to be shown
    primaryStage.show();

    playlistStage.setScene(playlistScene);
    playlistStage.initModality(Modality.APPLICATION_MODAL);
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