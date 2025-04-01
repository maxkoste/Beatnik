package view;

import controller.Controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
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
import java.util.Optional;

public class MainFrame implements EventHandler<ActionEvent> {
  AnchorPane primaryPane;
  BorderPane songsPane;
  BorderPane playlistsPane;
  Stage playlistStage;
  Scene songsScene;
  Scene playlistsScene;
  MultipleSelectionModel<String> songSelector;
  SelectionModel<String> playlistSelector;
  ListView<String> currentPlaylist;
  MultipleSelectionModel<String> playlistSongSelector;
  Controller controller;
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
    songsPane = new BorderPane(); // Pane which contains songs popup content
    playlistsPane = new BorderPane(); // Pane which contains currentPlaylist popup content

    initializeZoneOne();
    initializeSongsPane();
    initializePlaylistPane();
    initializeZoneTwo();
    initializeZoneThree();
    initializeZoneFour();

    Scene primaryScene = new Scene(primaryPane, screenWidth, screenHeight); // Add pane to scene

    songsScene = new Scene(songsPane, 400, 400);
    playlistsScene = new Scene(playlistsPane, 400, 400);

    Image flowers = new Image("flowers.JPG"); // Add icon
    primaryStage.getIcons().add(flowers);
    primaryStage.setScene(primaryScene); // Finalize window to be shown
    primaryStage.show();

    playlistStage.setScene(songsScene);
    playlistStage.initModality(Modality.APPLICATION_MODAL);
  }

  private void initializeZoneOne() {
    Button songsButton = new Button();
    songsButton.setPrefSize(50, 50);
    songsButton.setText("⏏");
    songsButton.setOnAction(this);
    AnchorPane.setTopAnchor(songsButton, screenHeight / 10);
    AnchorPane.setLeftAnchor(songsButton, screenWidth / 10);

    CircularSlider quantize = new CircularSlider(10, false);
    quantize.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(quantize, (screenHeight / 2));
    AnchorPane.setLeftAnchor(quantize,(screenWidth / 10));

    CircularSlider cueVolume = new CircularSlider(10, false);
    cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(cueVolume, screenHeight / 1.25);
    AnchorPane.setLeftAnchor(cueVolume, screenWidth / 10);

    primaryPane.getChildren().addAll(songsButton, quantize, cueVolume);
  }

  private void initializeZoneTwo() {
    channelOneContainer = new TextArea(); // Temporary implementation maybe try splitPane?
    channelOneContainer.setPrefSize(screenWidth / 2, 75.0);
    AnchorPane.setTopAnchor(channelOneContainer, 75.0);
    AnchorPane.setLeftAnchor(channelOneContainer, (((screenWidth / 2)) - ((screenWidth / 2) / 2)));

    TextArea channelTwoContainer = new TextArea(); // Temporary implementation
    channelTwoContainer.setPrefSize(screenWidth / 2, 75.0);
    AnchorPane.setTopAnchor(channelTwoContainer, (75.0 * 2));
    AnchorPane.setLeftAnchor(channelTwoContainer, (((screenWidth / 2)) - ((screenWidth / 2) / 2)));

    Button channelOnePlayPause = new Button();
    channelOnePlayPause.setPrefSize(26.0, 75.0);
    channelOnePlayPause.setText("⏯");
    channelOnePlayPause.setOnAction(this::handleChannelOnePlayPause);
    AnchorPane.setTopAnchor(channelOnePlayPause, 75.0);
    AnchorPane.setLeftAnchor(channelOnePlayPause, ((((screenWidth / 2)) - ((screenWidth / 2) / 2)) - 26));

    Button channelTwoPlayPause = new Button();
    channelTwoPlayPause.setPrefSize(26.0, 75.0);
    channelTwoPlayPause.setText("⏯");
    channelTwoPlayPause.setOnAction(this::handleChannelTwoPlayPause);
    AnchorPane.setTopAnchor(channelTwoPlayPause, 150.0);
    AnchorPane.setLeftAnchor(channelTwoPlayPause, ((((screenWidth / 2)) - ((screenWidth / 2) / 2)) - 26));

    Button channelOneSkip = new Button();
    channelOneSkip.setPrefSize(26.0, 75.0);
    channelOneSkip.setText("⏭");
    channelOneSkip.setOnAction(this::handleChannelOneSkip);
    AnchorPane.setTopAnchor(channelOneSkip, 75.0);
    AnchorPane.setLeftAnchor(channelOneSkip, ((((screenWidth / 2)) + ((screenWidth / 2) / 2))));

    Button channelTwoSkip = new Button();
    channelTwoSkip.setPrefSize(26.0, 75.0);
    channelTwoSkip.setText("⏭");
    channelTwoSkip.setOnAction(this::handleChannelTwoSkip);
    AnchorPane.setTopAnchor(channelTwoSkip, 150.0);
    AnchorPane.setLeftAnchor(channelTwoSkip, ((((screenWidth / 2)) + ((screenWidth / 2) / 2))));

    primaryPane.getChildren().addAll(channelOneContainer, channelTwoContainer,
        channelOnePlayPause, channelTwoPlayPause, channelOneSkip, channelTwoSkip);
  }

  private void initializeZoneThree() {
    Slider crossFader = new Slider();
    crossFader.setPrefSize(250, 5);
    crossFader.setMax(100);
    crossFader.setBlockIncrement(20);
    crossFader.setShowTickMarks(true);
    crossFader.setValue(50);
    crossFader.setOnDragDetected(this::handleCrossFader);
    AnchorPane.setTopAnchor(crossFader, (screenHeight / 1.15));
    AnchorPane.setLeftAnchor(crossFader, ((screenWidth / 2) - (crossFader.getPrefWidth() / 2)));

    Label crossFaderLabel = new Label();
    crossFaderLabel.setPrefSize(60, 10);
    crossFaderLabel.setText("Crossfader");
    AnchorPane.setTopAnchor(crossFaderLabel, (screenHeight / 1.1));
    AnchorPane.setLeftAnchor(crossFaderLabel, ((screenWidth / 2) - (crossFaderLabel.getPrefWidth() / 2)));

    ToggleButton channelOneCue = new ToggleButton(); // Button that can be tied to boolean value
    channelOneCue.setPrefSize(50, 30);
    channelOneCue.setText("CUE");
    AnchorPane.setTopAnchor(channelOneCue, (screenHeight / 1.15));
    AnchorPane.setLeftAnchor(channelOneCue, ((screenWidth / 3.275)) - (channelOneCue.getPrefWidth() / 2));

    ToggleButton channelTwoCue = new ToggleButton(); // Button that can be tied to boolean value
    channelTwoCue.setPrefSize(50, 30);
    channelTwoCue.setText("CUE");
    AnchorPane.setTopAnchor(channelTwoCue, (screenHeight / 1.15));
    AnchorPane.setLeftAnchor(channelTwoCue, ((screenWidth / 1.442) - (channelTwoCue.getPrefWidth() / 2)));

    Slider channelOneVolume = new Slider();
    channelOneVolume.setPrefSize(5, (screenHeight / 4.5));
    channelOneVolume.setOrientation(Orientation.VERTICAL);
    channelOneVolume.setMax(100.0);
    channelOneVolume.setBlockIncrement(20);
    channelOneVolume.setShowTickMarks(true);
    AnchorPane.setTopAnchor(channelOneVolume, (screenHeight / 1.635));
    AnchorPane.setLeftAnchor(channelOneVolume, ((screenWidth / 3.275) - (channelOneVolume.getPrefWidth() / 2)));

    Slider channelTwoVolume = new Slider();
    channelTwoVolume.setPrefSize(5, (screenHeight / 4.5));
    channelTwoVolume.setOrientation(Orientation.VERTICAL);
    channelTwoVolume.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    channelTwoVolume.setMax(100.0);
    channelTwoVolume.setBlockIncrement(20);
    channelTwoVolume.setShowTickMarks(true);
    AnchorPane.setTopAnchor(channelTwoVolume, (screenHeight / 1.635));
    AnchorPane.setLeftAnchor(channelTwoVolume, ((screenWidth / 1.453) - (channelOneVolume.getPrefWidth() / 2)));

    CircularSlider channelOneBass = new CircularSlider(10, false);
    channelOneBass.valueProperty().addListener((observable, oldValue, newValue) -> {
      float bassCutoff = newValue.floatValue();
    
      // Scale value from 0–270 to 0–8000
      float bass = (bassCutoff / 270) * 8000;
      controller.setBass(bass);
      //Debugging
      System.out.println("Bass Knob Ch 1: " + bass);
    });
    AnchorPane.setTopAnchor(channelOneBass, (screenHeight / 1.87));
    AnchorPane.setLeftAnchor(channelOneBass,(screenWidth / 3.275) -25);

    CircularSlider channelTwoBass = new CircularSlider(10, false);
    channelTwoBass.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelTwoBass, (screenHeight / 1.87));
    AnchorPane.setLeftAnchor(channelTwoBass, (screenWidth / 1.442) - 25);

    CircularSlider channelOneTreble = new CircularSlider(10, false);
    channelOneTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelOneTreble, (screenHeight / 1.87) -75);
    AnchorPane.setLeftAnchor(channelOneTreble, (screenWidth / 3.275) -25);

    CircularSlider channelTwoTreble = new CircularSlider(10, false);
    channelTwoTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelTwoTreble, (screenHeight / 1.87) -75);
    AnchorPane.setLeftAnchor(channelTwoTreble, (screenWidth / 1.442) - 25);

    CircularSlider channelOneSpeed = new CircularSlider(10, false);
    channelOneSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelOneSpeed, ((screenHeight / 1.87) - 150));
    AnchorPane.setLeftAnchor(channelOneSpeed, (screenWidth / 3.275) - 25);

    CircularSlider channelTwoSpeed = new CircularSlider(10, false);
    channelTwoSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(channelTwoSpeed, ((screenHeight / 1.87) - 150));
    AnchorPane.setLeftAnchor(channelTwoSpeed, ((screenWidth / 1.442) - 25));

    ScrollBar channelOneVolumeIndicator = new ScrollBar(); // Temporary implementation
    channelOneVolumeIndicator.setPrefSize(10.0, 300.0);
    channelOneVolumeIndicator.setOrientation(Orientation.VERTICAL);
    AnchorPane.setTopAnchor(channelOneVolumeIndicator, (screenHeight - 500));
    AnchorPane.setLeftAnchor(channelOneVolumeIndicator, ((screenWidth / 2) - 150) - (channelOneVolumeIndicator.getPrefWidth() / 2));

    ScrollBar channelTwoVolumeIndicator = new ScrollBar(); // Temporary implementation
    channelTwoVolumeIndicator.setPrefSize(10.0, 300.0);
    channelTwoVolumeIndicator.setOrientation(Orientation.VERTICAL);
    AnchorPane.setTopAnchor(channelTwoVolumeIndicator, (screenHeight - 500));
    AnchorPane.setLeftAnchor(channelTwoVolumeIndicator, ((screenWidth / 2) + 150) - (channelOneVolumeIndicator.getPrefWidth() / 2));

    primaryPane.getChildren().addAll(crossFader, crossFaderLabel, channelOneCue, channelTwoCue, channelOneVolume,
        channelTwoVolume, channelOneBass, channelTwoBass, channelOneTreble, channelTwoTreble, channelOneSpeed, channelTwoSpeed,
        channelOneVolumeIndicator, channelTwoVolumeIndicator);
  }

  private void initializeZoneFour() {
    CircularSlider effectIntensity = new CircularSlider(10, false);
    effectIntensity.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(effectIntensity, screenHeight / 10);
    AnchorPane.setLeftAnchor(effectIntensity, screenWidth / 1.15);

    CircularSlider effectSelector = new CircularSlider(5, true);
    effectSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(effectSelector, (screenHeight / 2));
    AnchorPane.setLeftAnchor(effectSelector,(screenWidth / 1.15));

    Slider masterVolume = new Slider();
    masterVolume.setPrefSize(10, 150);
    masterVolume.setOrientation(Orientation.VERTICAL);
    masterVolume.setMax(100.0);
    masterVolume.setBlockIncrement(20);
    masterVolume.setMinorTickCount(0);
    masterVolume.setShowTickLabels(true);
    AnchorPane.setTopAnchor(masterVolume, (screenHeight / 1.3) - (masterVolume.getPrefHeight() / 2));
    AnchorPane.setLeftAnchor(masterVolume, (screenWidth / 1.15) +20);

    // listener for setting the volume
    masterVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      float volume = newValue.floatValue();
      controller.setMasterVolume(volume);
    });

    masterVolume.setValue(50);

    Label masterVolumeLabel = new Label();
    masterVolumeLabel.setPrefSize(95, 10);
    masterVolumeLabel.setText("Master Volume");
    AnchorPane.setTopAnchor(masterVolumeLabel, (screenHeight / 1.3) + 85);
    AnchorPane.setLeftAnchor(masterVolumeLabel, ((screenWidth / 1.15) - (masterVolumeLabel.getPrefWidth() / 2)) +25);

    primaryPane.getChildren().addAll(effectIntensity, effectSelector, masterVolume, masterVolumeLabel);
  }

  public void initializeSongsPane() {
    ListView<String> songList = new ListView<>(controller.getSongsGUI());
    songSelector = songList.getSelectionModel();
    songSelector.setSelectionMode(SelectionMode.MULTIPLE);
    songList.setOnMouseClicked(this::handleSongSelection);
    songsPane.setCenter(songList);

    Label infoLabel = new Label();
    infoLabel.setText("Hold CTRL for Multiple Selections");
    infoLabel.setPrefSize(400, 40);
    infoLabel.setAlignment(Pos.CENTER);
    songsPane.setBottom(infoLabel);

    Button importSongs = new Button();
    importSongs.setText("Import");
    importSongs.setOnAction(this::handleImport);

    Button viewPlaylist = new Button();
    viewPlaylist.setText("View Playlist");
    viewPlaylist.setOnAction(this::handleViewPlaylist);

    Button addToPlaylist = new Button();
    addToPlaylist.setText("Add to Playlist");
    addToPlaylist.setOnAction(this::handleAddToPlaylist);

    ChoiceBox<String> playlistBox = new ChoiceBox<>();
    playlistBox.setPrefSize(133, 10);
    playlistBox.setItems(controller.getPlaylistsGUI());
    playlistBox.getSelectionModel().select(0);
    playlistSelector = playlistBox.getSelectionModel();
    playlistSelector.select(0);

    ToolBar songsMenu = new ToolBar(importSongs, viewPlaylist, addToPlaylist, playlistBox);
    songsPane.setTop(songsMenu);
  }

  public void initializePlaylistPane() {
    currentPlaylist = new ListView<>();
    playlistSongSelector = currentPlaylist.getSelectionModel();
    songSelector.setSelectionMode(SelectionMode.MULTIPLE);
    currentPlaylist.setOnMouseClicked(this::handlePlaylistSongSelection);
    playlistsPane.setCenter(currentPlaylist);

    Label infoLabel = new Label();
    infoLabel.setText("Hold CTRL for Multiple Selections");
    infoLabel.setPrefSize(400, 40);
    infoLabel.setAlignment(Pos.CENTER);
    playlistsPane.setBottom(infoLabel);

    Button viewSongs = new Button();
    viewSongs.setText("View Songs");
    viewSongs.setOnAction(this::handleViewSongs);

    ToolBar playlistMenu = new ToolBar(viewSongs);
    playlistsPane.setTop(playlistMenu);
  }

  @Override
  public void handle(ActionEvent actionEvent) {
    playlistStage.showAndWait();
    System.out.println("Playlist Button");
  }

  public void handleSongSelection(MouseEvent mouseEvent) {
    if(mouseEvent.getClickCount() == 2) {
      controller.setSong(1, songSelector.getSelectedItem()); // set only to channel 1 for now
      channelOneContainer.setText("Song loaded: " + songSelector.getSelectedItem()); // TODO: Replace with spectral analyzer
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
        System.out.println("File could not be moved");
      }
    }
  }

  public void handleViewPlaylist(ActionEvent actionEvent) {
    currentPlaylist.setItems(controller.getPlaylistSongs(playlistSelector.getSelectedItem()));
    playlistStage.setScene(playlistsScene);
  }

  public void handleViewSongs(ActionEvent actionEvent) {
    playlistStage.setScene(songsScene);
  }

  public void handleAddToPlaylist(ActionEvent actionEvent) { //TODO: Make switch-case, maybe in Controller
    String playlistSelected = playlistSelector.getSelectedItem();

    if (playlistSelected.equals("New Playlist")) {
        TextInputDialog inputPlaylistName = new TextInputDialog();
        inputPlaylistName.setTitle("New Playlist");
        inputPlaylistName.setHeaderText("Input Playlist Name");
        Optional<String> name = inputPlaylistName.showAndWait();

        if (name.isPresent()) { //TODO: Do not allow same name multiple times
          if (!(name.get().isBlank())) {
            ObservableList<Integer> selections = songSelector.getSelectedIndices();
            controller.createNewPlaylist(name.get(), selections);
          }
        }
    } else System.out.println("not implemented");
  }

  public void handlePlaylistSongSelection(MouseEvent mouseEvent) {
    if(mouseEvent.getClickCount() == 2) {
      controller.setSong(1, playlistSongSelector.getSelectedItem()); // set only to channel 1 for now
      channelOneContainer.setText("Song loaded: " + playlistSongSelector.getSelectedItem()); // TODO: Replace with spectral analyzer
    }
  }

  private void handleChannelOnePlayPause(ActionEvent actionEvent) {
    System.out.println("Channel One Play/Pause");
    controller.playSong();
  }

  private void handleChannelTwoPlayPause(ActionEvent actionEvent) {
    System.out.println("Channel Two Play/Pause");
  }

  public void handleChannelOneSkip(ActionEvent actionEvent) {

  }

  public void handleChannelTwoSkip(ActionEvent actionEvent) {

  }

  private void handleCrossFader(MouseEvent mouseEvent) {
    System.out.println("Crossfader");
  }

}