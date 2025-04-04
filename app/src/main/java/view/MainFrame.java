package view;

import controller.Controller;
import controller.PlaylistManager;
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
import java.util.List;


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
  Boolean channelOneActive = true;
  Controller controller;
  PlaylistManager playlistManager;
  TextArea channelOneContainer;
  WaveFormCanvas waveformOne;
  TextArea channelTwoContainer;
  WaveFormCanvas waveformTwo;
  Button switchChannelOne;
  Button switchChannelTwo;
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
    playlistStage.setTitle("All Songs");
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

    CircularSlider quantize = new CircularSlider(9, false);
    quantize.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
      System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
    });
    AnchorPane.setTopAnchor(quantize, (screenHeight / 2));
    AnchorPane.setLeftAnchor(quantize,(screenWidth / 10));

    CircularSlider cueVolume = new CircularSlider(9, false);
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

    waveformOne = new WaveFormCanvas(screenWidth / 2, 75);
    AnchorPane.setTopAnchor(waveformOne, 75.0);
    AnchorPane.setLeftAnchor(waveformOne, (screenWidth - waveformOne.getWidth()) / 2);

    channelTwoContainer = new TextArea(); // Temporary implementation
    channelTwoContainer.setPrefSize(screenWidth / 2, 75.0);
    AnchorPane.setTopAnchor(channelTwoContainer, (75.0 * 2));
    AnchorPane.setLeftAnchor(channelTwoContainer, (((screenWidth / 2)) - ((screenWidth / 2) / 2)));

    waveformTwo = new WaveFormCanvas(screenWidth / 2, 75);
    AnchorPane.setTopAnchor(waveformTwo, 75.0 * 2);
    AnchorPane.setLeftAnchor(waveformTwo, (screenWidth - waveformTwo.getWidth()) / 2);

    Button channelOnePlayPause = new Button();
    channelOnePlayPause.setPrefSize(30.0, 75.0);
    channelOnePlayPause.setText("⏯");
    channelOnePlayPause.setOnAction(this::handleChannelOnePlayPause);
    AnchorPane.setTopAnchor(channelOnePlayPause, 75.0);
    AnchorPane.setLeftAnchor(channelOnePlayPause, ((((screenWidth / 2)) - ((screenWidth / 2) / 2)) - 30));

    Button channelTwoPlayPause = new Button();
    channelTwoPlayPause.setPrefSize(30.0, 75.0);
    channelTwoPlayPause.setText("⏯");
    channelTwoPlayPause.setOnAction(this::handleChannelTwoPlayPause);
    AnchorPane.setTopAnchor(channelTwoPlayPause, 150.0);
    AnchorPane.setLeftAnchor(channelTwoPlayPause, ((((screenWidth / 2)) - ((screenWidth / 2) / 2)) - 30));

    Button channelOneSkip = new Button();
    channelOneSkip.setPrefSize(30.0, 75.0);
    channelOneSkip.setText("⏭");
    channelOneSkip.setOnAction(this::handleChannelOneSkip);
    AnchorPane.setTopAnchor(channelOneSkip, 75.0);
    AnchorPane.setLeftAnchor(channelOneSkip, ((((screenWidth / 2)) + ((screenWidth / 2) / 2))));

    Button channelTwoSkip = new Button();
    channelTwoSkip.setPrefSize(30.0, 75.0);
    channelTwoSkip.setText("⏭");
    channelTwoSkip.setOnAction(this::handleChannelTwoSkip);
    AnchorPane.setTopAnchor(channelTwoSkip, 150.0);
    AnchorPane.setLeftAnchor(channelTwoSkip, ((((screenWidth / 2)) + ((screenWidth / 2) / 2))));

    primaryPane.getChildren().addAll(channelOneContainer, channelTwoContainer, waveformOne, waveformTwo,
        channelOnePlayPause, channelTwoPlayPause, channelOneSkip, channelTwoSkip);
  }

  private void initializeZoneThree() {
    Slider crossFader = new Slider();
    crossFader.setPrefSize(250, 5);
    crossFader.setMax(100);
    crossFader.setBlockIncrement(20);
    crossFader.setShowTickMarks(true);
    crossFader.setValue(50);
    crossFader.valueProperty().addListener((observable, oldValue, newValue) -> {
      controller.setCrossfaderModifier(newValue.floatValue());
    });
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
    channelOneVolume.setValue(50);
    channelOneVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      controller.setChannelOneVolume(newValue.floatValue());
    });
    AnchorPane.setTopAnchor(channelOneVolume, (screenHeight / 1.635));
    AnchorPane.setLeftAnchor(channelOneVolume, ((screenWidth / 3.275) - (channelOneVolume.getPrefWidth() / 2)));

    Slider channelTwoVolume = new Slider();
    channelTwoVolume.setPrefSize(5, (screenHeight / 4.5));
    channelTwoVolume.setOrientation(Orientation.VERTICAL);
    channelTwoVolume.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
    channelTwoVolume.setMax(100.0);
    channelTwoVolume.setBlockIncrement(20);
    channelTwoVolume.setShowTickMarks(true);
    channelTwoVolume.setValue(50);
    channelTwoVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      controller.setChannelTwoVolume(newValue.floatValue());
    });
    AnchorPane.setTopAnchor(channelTwoVolume, (screenHeight / 1.635));
    AnchorPane.setLeftAnchor(channelTwoVolume, ((screenWidth / 1.453) - (channelOneVolume.getPrefWidth() / 2)));

    CircularSlider channelOneBass = new CircularSlider(9, false);
    channelOneBass.valueProperty().addListener((observable, oldValue, newValue) -> {
      float bassGain = newValue.floatValue();
      // Scale value from 0–270 to 0–100dB
      float bass = (bassGain / 270) * 100;
      controller.setBass1(bass);
    });
    AnchorPane.setTopAnchor(channelOneBass, (screenHeight / 1.87));
    AnchorPane.setLeftAnchor(channelOneBass,(screenWidth / 3.275) -25);

    CircularSlider channelTwoBass = new CircularSlider(9, false);
    channelTwoBass.valueProperty().addListener((observable, oldValue, newValue) -> {
      float bassGain = newValue.floatValue();
      // Scale value from 0–270 to 0–100dB
      bassGain = (bassGain / 270) * 100;
      controller.setBass2(bassGain);
    });
    AnchorPane.setTopAnchor(channelTwoBass, (screenHeight / 1.87));
    AnchorPane.setLeftAnchor(channelTwoBass, (screenWidth / 1.442) - 25);

    CircularSlider channelOneTreble = new CircularSlider(9, false);
    channelOneTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
      //Scale the value from 0-270 to 0-100dB
      float trebleGain = newValue.floatValue();
      trebleGain = (trebleGain / 270) * 100;
      controller.setTreble1(trebleGain);
    });
    AnchorPane.setTopAnchor(channelOneTreble, (screenHeight / 1.87) -75);
    AnchorPane.setLeftAnchor(channelOneTreble, (screenWidth / 3.275) -25);

    CircularSlider channelTwoTreble = new CircularSlider(9, false);
    channelTwoTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
      float trebleGain = newValue.floatValue();
      //Scale the value from 0-270 to 0-100dB
      trebleGain = (trebleGain / 270) * 100;
      controller.setTreble2(trebleGain);  
    });
    AnchorPane.setTopAnchor(channelTwoTreble, (screenHeight / 1.87) -75);
    AnchorPane.setLeftAnchor(channelTwoTreble, (screenWidth / 1.442) - 25);

    CircularSlider channelOneSpeed = new CircularSlider(9, false);
    channelOneSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
    });
    AnchorPane.setTopAnchor(channelOneSpeed, ((screenHeight / 1.87) - 150));
    AnchorPane.setLeftAnchor(channelOneSpeed, (screenWidth / 3.275) - 25);

    CircularSlider channelTwoSpeed = new CircularSlider(9, false);
    channelTwoSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
      double volume = newValue.doubleValue();
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
    CircularSlider effectIntensity = new CircularSlider(9, false);
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
    masterVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
      controller.setMasterVolume(newValue.floatValue() / 100);
    });
    masterVolume.setValue(50);
    AnchorPane.setTopAnchor(masterVolume, (screenHeight / 1.3) - (masterVolume.getPrefHeight() / 2));
    AnchorPane.setLeftAnchor(masterVolume, (screenWidth / 1.15) +20);

    Label masterVolumeLabel = new Label();
    masterVolumeLabel.setPrefSize(95, 10);
    masterVolumeLabel.setText("Master Volume");
    AnchorPane.setTopAnchor(masterVolumeLabel, (screenHeight / 1.3) + 85);
    AnchorPane.setLeftAnchor(masterVolumeLabel, ((screenWidth / 1.15) - (masterVolumeLabel.getPrefWidth() / 2)) +25);

    primaryPane.getChildren().addAll(effectIntensity, effectSelector, masterVolume, masterVolumeLabel);
  }

  public void initializeSongsPane() {
    ListView<String> songList = new ListView<>(playlistManager.getSongsGUI());
    songSelector = songList.getSelectionModel();
    songSelector.setSelectionMode(SelectionMode.MULTIPLE);
    songList.setOnMouseClicked(this::handleSongSelection);
    songsPane.setCenter(songList);

    Label infoLabel = new Label();
    infoLabel.setText("Double Click To Pick Song — Hold CTRL for Multiple Selections");
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
    playlistBox.setPrefSize(122, 10);
    playlistBox.setItems(playlistManager.getPlaylistsGUI());
    playlistSelector = playlistBox.getSelectionModel();
    selectPlaylistIndex(0);

    switchChannelOne = new Button();
    switchChannelOne.setText("1");
    switchChannelOne.setOnAction(this::handleChannelSwitch);

    ToolBar songsMenu = new ToolBar(importSongs, viewPlaylist, addToPlaylist, playlistBox, switchChannelOne);
    songsPane.setTop(songsMenu);
  }

  public void initializePlaylistPane() {
    currentPlaylist = new ListView<>();
    playlistSongSelector = currentPlaylist.getSelectionModel();
    playlistSongSelector.setSelectionMode(SelectionMode.MULTIPLE);
    currentPlaylist.setOnMouseClicked(this::handlePlaylistSongSelection);
    playlistsPane.setCenter(currentPlaylist);

    Label infoLabel = new Label();
    infoLabel.setText("Double Click To Pick Song — Hold CTRL for Multiple Selections");
    infoLabel.setPrefSize(400, 40);
    infoLabel.setAlignment(Pos.CENTER);
    playlistsPane.setBottom(infoLabel);

    Button viewSongs = new Button();
    viewSongs.setPrefSize(90, 10);
    viewSongs.setText("View Songs");
    viewSongs.setOnAction(this::handleViewSongs);

    Button editName = new Button();
    editName.setText("Edit Name");
    editName.setOnAction(this::handleEditPlaylistName);

    Button removeSongsFromPlaylist = new Button();
    removeSongsFromPlaylist.setText("Remove Songs");
    removeSongsFromPlaylist.setOnAction(this::handleRemoveSongsFromPlaylist);

    Button deletePlaylist = new Button();
    deletePlaylist.setText("Delete Playlist");
    deletePlaylist.setOnAction(this::handleDeletePlaylist);

    switchChannelTwo = new Button();
    switchChannelTwo.setText("1");
    switchChannelTwo.setOnAction(this::handleChannelSwitch);

    ToolBar playlistMenu = new ToolBar(viewSongs, editName, removeSongsFromPlaylist, deletePlaylist, switchChannelTwo);
    playlistsPane.setTop(playlistMenu);
  }

  @Override
  public void handle(ActionEvent actionEvent) {
    playlistStage.showAndWait();
  }
  //TODO: Redo Waveform-creation to be initialized in Zone 2, allow it to be blank when no song is active.
  //TODO: Place Waveform in a separate pane like SplitPane which is then attached with anchorPane.
  //TODO: Make Waveform instance variable so that it can be cleared without instanceof.
  public void handleSongSelection(MouseEvent mouseEvent) {
    if(mouseEvent.getClickCount() == 2) {
      if (channelOneActive) {
        String currentSong = songSelector.getSelectedItem();
        controller.setSong(1, currentSong);
        channelOneContainer.setText("Song loaded: " + currentSong); // TODO: Replace with song/playlist info-label
      } else {
        controller.setSong(2, songSelector.getSelectedItem());
        channelTwoContainer.setText("Song loaded: " + songSelector.getSelectedItem());
      }
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
    String playlistName = playlistSelector.getSelectedItem();
    if (playlistName.equals("New Playlist")) {
      userMessage(Alert.AlertType.INFORMATION, "No Playlist Selected");
    } else {
      currentPlaylist.setItems(playlistManager.getPlaylistSongs(playlistName));
      playlistStage.setTitle(playlistName);
      playlistStage.setScene(playlistsScene);
    }
  }

  public void handleViewSongs(ActionEvent actionEvent) {
    playlistStage.setTitle("All Songs");
    playlistStage.setScene(songsScene);
  }

  public void handleEditPlaylistName(ActionEvent actionEvent) {
    playlistStage.setTitle(playlistManager.editPlaylistName(playlistSelector.getSelectedItem()));
    playlistManager.savePlaylistData();
  }

  public void handleDeletePlaylist(ActionEvent actionEvent) {
    playlistManager.deletePlaylist(playlistStage.getTitle());
    handleViewSongs(actionEvent);
    playlistManager.savePlaylistData();
  }

  public void handleRemoveSongsFromPlaylist(ActionEvent actionEvent) {
    playlistManager.removeSongsFromPlaylist(playlistStage.getTitle(), playlistSongSelector.getSelectedItems());
    currentPlaylist.getItems().removeAll(playlistSongSelector.getSelectedItem());
    playlistManager.savePlaylistData();
  }

  public void handleAddToPlaylist(ActionEvent actionEvent) {
    String playlistSelected = playlistSelector.getSelectedItem();
    playlistManager.addToPlaylist(playlistSelected, songSelector.getSelectedIndices());
    playlistManager.savePlaylistData();
  }

  public void handleChannelSwitch(ActionEvent actionEvent) {
    if (channelOneActive) {
      channelOneActive = false;
      switchChannelOne.setText("2");
      switchChannelTwo.setText("2");
    } else {
      channelOneActive = true;
      switchChannelOne.setText("1");
      switchChannelTwo.setText("1");
    }
  }

  public void handlePlaylistSongSelection(MouseEvent mouseEvent) {
    if(mouseEvent.getClickCount() == 2) {
      if (channelOneActive) {
        controller.startPlaylist(1, playlistSongSelector.getSelectedIndex(), currentPlaylist.getItems());
        channelOneContainer.setText("Song loaded: " + playlistSongSelector.getSelectedItem()); // TODO: Replace with spectral analyzer
      } else {
        controller.startPlaylist(2, playlistSongSelector.getSelectedIndex(), currentPlaylist.getItems());
        channelTwoContainer.setText("Song loaded: " + playlistSongSelector.getSelectedItem()); // TODO: Replace with spectral analyzer
      }
    }
  }

  private void handleChannelOnePlayPause(ActionEvent actionEvent) {
    controller.playSong(1);
  }

  private void handleChannelTwoPlayPause(ActionEvent actionEvent) {
    controller.playSong(2);
  }

  public void handleChannelOneSkip(ActionEvent actionEvent) {
    controller.nextSong(1);
  }

  public void handleChannelTwoSkip(ActionEvent actionEvent) {
    controller.nextSong(2);
  }

  public String promptUserInput(String title, String headerText) {
    TextInputDialog inputPlaylistName = new TextInputDialog();
    inputPlaylistName.setTitle(title);
    inputPlaylistName.setHeaderText(headerText);
    Optional<String> name = inputPlaylistName.showAndWait();

    return name.orElse(null); // A Java-suggested improvement to an isPresent check. Returns null if the user
    // closed the window etc instead of throwing an exception.
  }

  public void userMessage(Alert.AlertType alertType, String headerText) {
    Alert message = new Alert(alertType);
    message.setHeaderText(headerText);
    message.showAndWait();
  }

  public boolean userConfirm(String headerText) {
    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
    confirm.setHeaderText(headerText);
    confirm.showAndWait();
    return confirm.getResult() == ButtonType.OK;
  }

  public void selectPlaylistIndex(int index) {
    playlistSelector.select(index);
  }

  public void registerPlaylistManager(PlaylistManager playlistManager) {
    this.playlistManager = playlistManager;
  }

  public void setWaveformAudioData(float[] originalAudioData, int channel) {
    if (channel == 1) {
      waveformOne.setOriginalAudioData(originalAudioData);
    } else waveformTwo.setOriginalAudioData(originalAudioData);
  }

  public void updateWaveformOne(float currentSecond) {
    waveformOne.update(currentSecond);
  }

  public void updateWaveformTwo(float currentSecond) {
    waveformTwo.update(currentSecond);
  }
}