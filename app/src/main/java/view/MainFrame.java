package view;

import controller.Controller;
import controller.PlaylistManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;

public class MainFrame implements EventHandler<ActionEvent> {
    private GridPane primaryPane;
    private BorderPane songsPane;
    private BorderPane playlistsPane;
    private Stage playlistStage;
    private Scene songsScene;
    private Scene playlistsScene;
    private MultipleSelectionModel<String> songSelector;
    private SelectionModel<String> playlistSelector;
    private ListView<String> currentPlaylist;
    private MultipleSelectionModel<String> playlistSongSelector;
    private Boolean channelOneActive = true;
    private Controller controller;
    private PlaylistManager playlistManager;
    private Button switchChannelOne;
    private Button switchChannelTwo;
    private TopPnl topPnl;
    private LeftPnl leftPnl;
    private RightPnl rightPnl;
    private CenterPnl centerPnl;

    public MainFrame(Controller controller) {
        this.controller = controller;
    }

    public void start(Stage primaryStage) {
        Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
      double screenHeight = screenResolution.getHeight();

        primaryStage.setTitle("Beatnik");
        primaryStage.setResizable(true);

        playlistStage = new Stage();
        playlistStage.setTitle("All Songs");
        playlistStage.setResizable(true);

        primaryPane = new GridPane(); // Pane which contains all content
        songsPane = new BorderPane(); // Pane which contains songs popup content
        playlistsPane = new BorderPane(); // Pane which contains currentPlaylist popup content

        primaryPane.setGridLinesVisible(true); //TODO: TEMPORARY
        final int numCols = 13;
        final int numRows = 13;
        for (int i = 0; i < numCols; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPercentWidth(100.0 / numCols);
            colConst.setFillWidth(true);
            primaryPane.getColumnConstraints().add(colConst);
        }
        for (int i = 0; i < numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPercentHeight(100.0 / numRows);
            rowConst.setFillHeight(true);
            primaryPane.getRowConstraints().add(rowConst);
        }

        initializeSongsPane();
        initializePlaylistPane();
        topPnl = new TopPnl(this, controller, primaryPane, numCols);
        leftPnl = new LeftPnl(this, primaryPane, numCols);
        rightPnl = new RightPnl(this, primaryPane, numCols);
        centerPnl = new CenterPnl(controller, primaryPane, numCols);

        Scene primaryScene = new Scene(primaryPane, (screenHeight * 0.9), (screenHeight * 0.9)); // Add pane to scene

        songsScene = new Scene(songsPane, (screenHeight * 0.7), (screenHeight * 0.7));
        playlistsScene = new Scene(playlistsPane, (screenHeight * 0.7), (screenHeight * 0.7));

        playlistsScene.getStylesheets().add("styles.css");
        songsScene.getStylesheets().add("styles.css");
        Image logo = new Image("/Logo/beatnik-logo.png"); // Add icon
        primaryStage.getIcons().add(logo);
        primaryStage.setScene(primaryScene); // Finalize window to be shown
        primaryScene.getStylesheets().add("styles.css");
        primaryStage.show();

        playlistStage.setScene(songsScene);
        playlistStage.initModality(Modality.APPLICATION_MODAL);

        onClose(primaryStage);
    }

    /**
     * Closes resources in the controller before exiting
     * 
     * @param primaryStage
     */
    private void onClose(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            controller.shutDown();
        });
    }

    public void initializeSongsPane() {
        ListView<String> songList = new ListView<>(playlistManager.getSongsGUI());
        songSelector = songList.getSelectionModel();
        songSelector.setSelectionMode(SelectionMode.MULTIPLE);
        songList.setOnMouseClicked(this::handleSongSelection);
        songsPane.setCenter(songList);

        Label infoLabel = new Label("Double Click To Pick Song — Hold CTRL for Multiple Selections");
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setPrefHeight(50);
        infoLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        infoLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        songsPane.setBottom(infoLabel);

        Button importSongs = new Button("Import");
        importSongs.setOnAction(this::handleImport);

        Button viewPlaylist = new Button("View Playlist");
        viewPlaylist.setOnAction(this::handleViewPlaylist);

        Button addToPlaylist = new Button("Add to Playlist");
        addToPlaylist.setOnAction(this::handleAddToPlaylist);

        ChoiceBox<String> playlistBox = new ChoiceBox<>();
        playlistBox.setPrefSize(122, 10);
        HBox.setHgrow(playlistBox, Priority.ALWAYS);
        playlistBox.setMaxWidth(Double.MAX_VALUE);
        playlistBox.setItems(playlistManager.getPlaylistsGUI());
        playlistSelector = playlistBox.getSelectionModel();
        selectPlaylistIndex(0);

        switchChannelOne = new Button("1");
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

        Label infoLabel = new Label("Double Click To Pick Song — Hold CTRL for Multiple Selections");
        infoLabel.setAlignment(Pos.CENTER);
        infoLabel.setPrefHeight(50);
        infoLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        infoLabel.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        playlistsPane.setBottom(infoLabel);

        Button viewSongs = new Button("View Songs");
        viewSongs.setOnAction(this::handleViewSongs);

        Button editName = new Button("Edit Name");
        editName.setOnAction(this::handleEditPlaylistName);

        Button removeSongsFromPlaylist = new Button("Remove Songs");
        HBox.setHgrow(removeSongsFromPlaylist, Priority.ALWAYS);
        removeSongsFromPlaylist.setMaxWidth(Double.MAX_VALUE);
        removeSongsFromPlaylist.setOnAction(this::handleRemoveSongsFromPlaylist);

        Button deletePlaylist = new Button("Delete Playlist");
        deletePlaylist.setOnAction(this::handleDeletePlaylist);

        switchChannelTwo = new Button("1");
        switchChannelTwo.setOnAction(this::handleChannelSwitch);

        ToolBar playlistMenu = new ToolBar(viewSongs, editName, removeSongsFromPlaylist, deletePlaylist,
                switchChannelTwo);
        playlistsPane.setTop(playlistMenu);
    }

    @Override
    public void handle(ActionEvent actionEvent) { // Songsbutton method, basic name from interface
        playlistStage.showAndWait();
    }

    public void handleSongSelection(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (channelOneActive) {
                controller.setSong(1, songSelector.getSelectedItem());
                setInfoText(false, songSelector.getSelectedItem(), 1);
            } else {
                controller.setSong(2, songSelector.getSelectedItem());
                setInfoText(false, songSelector.getSelectedItem(), 2);
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
                controller.moveFile(selectedFile,
                        String.valueOf(Paths.get("src/main/resources/songs/" + selectedFile.getName())));
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
        if (mouseEvent.getClickCount() == 2) {
            if (channelOneActive) {
                controller.startPlaylist(1, playlistSongSelector.getSelectedIndex(), currentPlaylist.getItems());
                setInfoText(true, playlistSongSelector.getSelectedItem(), 1);
            } else {
                controller.startPlaylist(2, playlistSongSelector.getSelectedIndex(), currentPlaylist.getItems());
                setInfoText(true, playlistSongSelector.getSelectedItem(), 2);
            }
        }
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

    public void updateAudioIndicatorOne(double rms) {
        centerPnl.updateAudioIndicatorOne(rms);
    }

    public void updateAudioIndicatorTwo(double rms) {
        centerPnl.updateAudioIndicatorTwo(rms);
    }

    public void updateWaveformOne(float currentSecond) {
        topPnl.updateWaveformOne(currentSecond);
    }

    public void updateWaveformTwo(float currentSecond) {
        topPnl.updateWaveformTwo(currentSecond);
    }

    public void setPlaylistManager(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;
    }

    public void setWaveformAudioData(float[] originalAudioData, int channel) {
        topPnl.setWaveformAudioData(originalAudioData, channel);
    }

    public void setInfoText(boolean playlist, String song, int channel) {
        topPnl.setInfoText(playlist, song, channel);
    }

    public void setEffectMix(float mix) {
        controller.setEffectMix(mix);
    }

    public void setEffect(int effectSelectorValue) {
        controller.setEffect(effectSelectorValue);
    }

    public void setMasterVolume(float masterVolume) {
        controller.setMasterVolume(masterVolume);
    }

    public float getCurrentEffectMix() {
        return controller.getCurrentEffectMix();
    }

    public String getSelectedPlaylist() {
        return playlistSelector.getSelectedItem();
    }

    /*
    private void initializeZoneOne() {
        Button songsButton = new Button();
        songsButton.setPrefSize(50, 50);
        songsButton.setText("⏏");
        songsButton.setOnAction(this);
        AnchorPane.setTopAnchor(songsButton, screenHeight / 10);
        AnchorPane.setLeftAnchor(songsButton, screenWidth / 10);

        CircularSlider quantize = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
        quantize.valueProperty().addListener((observable, oldValue, newValue) -> {
            double volume = newValue.doubleValue();
            System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
        });

        AnchorPane.setTopAnchor(quantize, (screenHeight / 2));
        AnchorPane.setLeftAnchor(quantize, (screenWidth / 10));

        // image for the knob
        ImageView quantizeImg = new ImageView(KNOB_BG);

        quantizeImg.setFitWidth(70);
        quantizeImg.setFitHeight(70);

        AnchorPane.setTopAnchor(quantizeImg, (screenHeight / 2)); // Same position as knob
        AnchorPane.setLeftAnchor(quantizeImg, (screenWidth / 10)); // Same position as knob

        Label quantizeLabel = new Label("Quantizer");
        AnchorPane.setTopAnchor(quantizeLabel, screenHeight / 1.76);
        AnchorPane.setLeftAnchor(quantizeLabel, screenWidth / 10.1);

        CircularSlider cueVolume = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
        cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            double volume = newValue.doubleValue();
            System.out.println("volume: " + ((int) (Math.ceil(volume / 2.7))));
        });
        AnchorPane.setTopAnchor(cueVolume, screenHeight / 1.25);
        AnchorPane.setLeftAnchor(cueVolume, screenWidth / 10);

        // Image for the next knob
        ImageView cueImg = new ImageView(KNOB_BG);
        cueImg.setFitHeight(70);
        cueImg.setFitWidth(70);

        AnchorPane.setTopAnchor(cueImg, screenHeight / 1.25);
        AnchorPane.setLeftAnchor(cueImg, screenWidth / 10);

        Label cueVolumeLabel = new Label("Cue Volume");
        AnchorPane.setTopAnchor(cueVolumeLabel, screenHeight / 1.15);
        AnchorPane.setLeftAnchor(cueVolumeLabel, screenWidth / 10.7);

        primaryPane.getChildren().addAll(songsButton, quantize, quantizeLabel, cueVolume, cueVolumeLabel, quantizeImg,
                cueImg);
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

        // Image overlay for channel one
        ImageView channelOneCueImage = new ImageView(
                new Image(getClass().getResourceAsStream("/Buttons/cue-passive.png")));
        channelOneCueImage.setFitWidth(80);
        channelOneCueImage.setFitHeight(80);
        channelOneCueImage.setMouseTransparent(true); // Allow mouse events to pass through
        AnchorPane.setTopAnchor(channelOneCueImage, (screenHeight / 1.15)-20);
        AnchorPane.setLeftAnchor(channelOneCueImage, ((screenWidth / 3.275)) - (channelOneCue.getPrefWidth() / 2) - 13);

        // Toggle image on button state change
        channelOneCue.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            String imagePath = isNowSelected ? "/Buttons/cue-engaged.png" : "/Buttons/cue-passive.png";
            channelOneCueImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        });

        ToggleButton channelTwoCue = new ToggleButton(); // Button that can be tied to boolean value
        channelTwoCue.setPrefSize(50, 30);
        channelTwoCue.setText("CUE");
        AnchorPane.setTopAnchor(channelTwoCue, (screenHeight / 1.15));
        AnchorPane.setLeftAnchor(channelTwoCue, ((screenWidth / 1.442) - (channelTwoCue.getPrefWidth() / 2)));

        // Image overlay for channel two
        ImageView channelTwoCueImage = new ImageView(
                new Image(getClass().getResourceAsStream("/Buttons/cue-passive.png")));
        channelTwoCueImage.setFitWidth(80);
        channelTwoCueImage.setFitHeight(80);
        channelTwoCueImage.setMouseTransparent(true); // Allow mouse events to pass through
        AnchorPane.setTopAnchor(channelTwoCueImage, (screenHeight / 1.15)- 20);
        AnchorPane.setLeftAnchor(channelTwoCueImage, ((screenWidth / 1.442) - (channelTwoCue.getPrefWidth() / 2))- 13);

        // Toggle image on button state change
        channelTwoCue.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            String imagePath = isNowSelected ? "/Buttons/cue-engaged.png" : "/Buttons/cue-passive.png";
            channelTwoCueImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        });

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

        CircularSlider channelOneBass = new CircularSlider(9, false, "/Knobs/knob-blue-fg.png");
        channelOneBass.valueProperty().addListener((observable, oldValue, newValue) -> {
            float bassGain = newValue.floatValue();
            // Scale value from 0–270 to 0–100dB
            float bass = (bassGain / 270) * 100;
            controller.setBass1(bass);
        });
        AnchorPane.setTopAnchor(channelOneBass, (screenHeight / 1.87));
        AnchorPane.setLeftAnchor(channelOneBass, (screenWidth / 3.275) - 25);

        ImageView chOneBassImg = new ImageView(KNOB_BG);
        chOneBassImg.setFitHeight(70);
        chOneBassImg.setFitWidth(70);

        AnchorPane.setTopAnchor(chOneBassImg, screenHeight / 1.87);
        AnchorPane.setLeftAnchor(chOneBassImg, (screenWidth / 3.275) - 25);

        Label bassLabelOne = new Label("B");
        AnchorPane.setTopAnchor(bassLabelOne, (screenHeight / 1.8));
        AnchorPane.setLeftAnchor(bassLabelOne, (screenWidth / 3.3));

        CircularSlider channelTwoBass = new CircularSlider(9, false, "/Knobs/knob-blue-fg.png");
        channelTwoBass.valueProperty().addListener((observable, oldValue, newValue) -> {
            float bassGain = newValue.floatValue();
            // Scale value from 0–270 to 0–100dB
            bassGain = (bassGain / 270) * 100;
            controller.setBass2(bassGain);
        });
        AnchorPane.setTopAnchor(channelTwoBass, (screenHeight / 1.87));
        AnchorPane.setLeftAnchor(channelTwoBass, (screenWidth / 1.442) - 25);

        ImageView chTwoBassImg = new ImageView(KNOB_BG);
        chTwoBassImg.setFitHeight(70);
        chTwoBassImg.setFitWidth(70);

        AnchorPane.setTopAnchor(chTwoBassImg, (screenHeight / 1.87));
        AnchorPane.setLeftAnchor(chTwoBassImg, (screenWidth / 1.442) - 25);

        Label bassLabelTwo = new Label("B");
        AnchorPane.setTopAnchor(bassLabelTwo, (screenHeight / 1.8));
        AnchorPane.setLeftAnchor(bassLabelTwo, (screenWidth / 1.446));

        CircularSlider channelOneTreble = new CircularSlider(9, false, "/Knobs/knob-green-fg.png");
        channelOneTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Scale the value from 0-270 to 0-100dB
            float trebleGain = newValue.floatValue();
            trebleGain = (trebleGain / 270) * 100;
            controller.setTreble1(trebleGain);

        });
        AnchorPane.setTopAnchor(channelOneTreble, (screenHeight / 1.87) - 75);
        AnchorPane.setLeftAnchor(channelOneTreble, (screenWidth / 3.275) - 25);

        ImageView chOneTreImg = new ImageView(KNOB_BG);
        chOneTreImg.setFitHeight(70);
        chOneTreImg.setFitWidth(70);

        AnchorPane.setTopAnchor(chOneTreImg, (screenHeight / 1.87) - 75);
        AnchorPane.setLeftAnchor(chOneTreImg, (screenWidth / 3.275) - 25);

        Label trebleLabelOne = new Label("T");
        AnchorPane.setTopAnchor(trebleLabelOne, (screenHeight / 1.8) - 75);
        AnchorPane.setLeftAnchor(trebleLabelOne, (screenWidth / 3.3));

        CircularSlider channelTwoTreble = new CircularSlider(9, false, "/Knobs/knob-green-fg.png");
        channelTwoTreble.valueProperty().addListener((observable, oldValue, newValue) -> {
            float trebleGain = newValue.floatValue();
            // Scale the value from 0-270 to 0-100dB
            trebleGain = (trebleGain / 270) * 100;
            controller.setTreble2(trebleGain);
            System.out.println("volume: " + ((int) (Math.ceil(trebleGain / 2.7))));
        });
        AnchorPane.setTopAnchor(channelTwoTreble, (screenHeight / 1.87) - 75);
        AnchorPane.setLeftAnchor(channelTwoTreble, (screenWidth / 1.442) - 25);

        ImageView chTwoTreImg = new ImageView(KNOB_BG);
        chTwoTreImg.setFitHeight(70);
        chTwoTreImg.setFitWidth(70);

        AnchorPane.setTopAnchor(chTwoTreImg, (screenHeight / 1.87) - 75);
        AnchorPane.setLeftAnchor(chTwoTreImg, (screenWidth / 1.442) - 25);

        Label trebleLabelTwo = new Label("T");
        AnchorPane.setTopAnchor(trebleLabelTwo, (screenHeight / 1.8) - 75);
        AnchorPane.setLeftAnchor(trebleLabelTwo, (screenWidth / 1.446));

        CircularSlider channelOneSpeed = new CircularSlider(9, false, "/Knobs/knob-red-fg.png");
        channelOneSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            double rawValue = newValue.doubleValue(); // 0.0 - 270.0
            // Map 0.0 - 270.0 to 0.8 - 1.2
            double mappedValue = 1.2 - (rawValue / 270.0) * (1.2 - 0.8);
            // Round to 2 decimal places
            mappedValue = Math.round(mappedValue * 100.0) / 100.0;

            controller.setPlaybackSpeedCh1(mappedValue);
        });
        AnchorPane.setTopAnchor(channelOneSpeed, ((screenHeight / 1.87) - 150));
        AnchorPane.setLeftAnchor(channelOneSpeed, (screenWidth / 3.275) - 25);

        ImageView chOneSpeedImg = new ImageView(KNOB_BG);
        chOneSpeedImg.setFitHeight(70);
        chOneSpeedImg.setFitWidth(70);

        AnchorPane.setTopAnchor(chOneSpeedImg, ((screenHeight / 1.87) - 150));
        AnchorPane.setLeftAnchor(chOneSpeedImg, (screenWidth / 3.275) - 25);

        Label speedLabelOne = new Label("S");
        AnchorPane.setTopAnchor(speedLabelOne, (screenHeight / 1.8) - 150);
        AnchorPane.setLeftAnchor(speedLabelOne, (screenWidth / 3.3));

        CircularSlider channelTwoSpeed = new CircularSlider(9, false, "/Knobs/knob-red-fg.png");
        channelTwoSpeed.valueProperty().addListener((observable, oldValue, newValue) -> {
            double rawValue = newValue.doubleValue(); // 0.0 - 270.0
            // Map 0.0 - 270.0 to 0.8 -1.2
            double mappedValue = 1.2 - (rawValue / 270.0) * (1.2 - 0.8);
            // Round to 2 decimal places
            mappedValue = Math.round(mappedValue * 100.0) / 100.0;

            controller.ssetPlaybackSpeedCh2(mappedValue);
        });
        AnchorPane.setTopAnchor(channelTwoSpeed, ((screenHeight / 1.87) - 150));
        AnchorPane.setLeftAnchor(channelTwoSpeed, ((screenWidth / 1.442) - 25));

        ImageView chTwoSpeedImg = new ImageView(KNOB_BG);
        chTwoSpeedImg.setFitHeight(70);
        chTwoSpeedImg.setFitWidth(70);

        AnchorPane.setTopAnchor(chTwoSpeedImg, ((screenHeight / 1.87) - 150));
        AnchorPane.setLeftAnchor(chTwoSpeedImg, ((screenWidth / 1.442) - 25));

        VBox audioIndicatorOne = new VBox(8);
        audioIndicatorOne.setPrefHeight(100);
        audioIndicatorOne.setLayoutX((screenWidth / 2) - 200);
        audioIndicatorOne.setLayoutY(screenHeight / 2);
        audioIndicatorOne.setAlignment(Pos.BOTTOM_CENTER);

        for (int i = auIndicatorCirclesOne.length - 1; i >= 0; i--) {
            Circle dot = new Circle(10);
            dot.setFill(Color.LIGHTGRAY);
            auIndicatorCirclesOne[i] = dot;
            audioIndicatorOne.getChildren().add(dot);
        }

        Label speedLabelTwo = new Label("S");
        AnchorPane.setTopAnchor(speedLabelTwo, (screenHeight / 1.8) - 150);
        AnchorPane.setLeftAnchor(speedLabelTwo, (screenWidth / 1.446));

        VBox audioIndicatorTwo = new VBox(8);
        audioIndicatorTwo.setPrefHeight(100);
        audioIndicatorTwo.setLayoutX((screenWidth / 2) + 200);
        audioIndicatorTwo.setLayoutY(screenHeight / 2);
        audioIndicatorTwo.setAlignment(Pos.BOTTOM_CENTER);

        for (int i = auIndicatorCirclesTwo.length - 1; i >= 0; i--) {
            Circle dot = new Circle(10);
            dot.setFill(Color.LIGHTGRAY);
            auIndicatorCirclesTwo[i] = dot;
            audioIndicatorTwo.getChildren().add(dot);
        }

        primaryPane.getChildren().addAll(crossFader, crossFaderLabel, channelOneCue, channelTwoCue, channelOneVolume,
                channelTwoVolume, channelOneBass, bassLabelOne, channelTwoBass, bassLabelTwo, channelOneTreble,
                trebleLabelOne, channelTwoTreble, trebleLabelTwo, channelOneSpeed, speedLabelOne, channelTwoSpeed,
                speedLabelTwo, audioIndicatorOne, audioIndicatorTwo, chOneBassImg, chTwoBassImg, chOneSpeedImg,
                chOneTreImg, chTwoSpeedImg, chTwoTreImg, channelOneCueImage, channelTwoCueImage);
    }

    private void initializeZoneFour() {
        CircularSlider effectIntensity = new CircularSlider(9, false, "/Knobs/knob-black-fg.png");
        effectIntensity.updateAngle(0.0); // Starts of at 0 degrees
        effectIntensity.valueProperty().addListener((observable, oldValue, newValue) -> {
            float volume = newValue.floatValue();
            float mixValue = volume / 270.0f;
            mixValue = Math.max(0.0f, Math.min(1.0f, mixValue));
            controller.setEffectMix(mixValue);
        });
        AnchorPane.setTopAnchor(effectIntensity, screenHeight / 10);
        AnchorPane.setLeftAnchor(effectIntensity, screenWidth / 1.15);

        ImageView effectIntensityImg = new ImageView(KNOB_BG);
        effectIntensityImg.setFitHeight(70);
        effectIntensityImg.setFitWidth(70);

        AnchorPane.setTopAnchor(effectIntensityImg, screenHeight / 10);
        AnchorPane.setLeftAnchor(effectIntensityImg, screenWidth / 1.15);

        Label effectIntensityLabel = new Label("Effect Intensity");
        AnchorPane.setTopAnchor(effectIntensityLabel, screenHeight / 6);
        AnchorPane.setLeftAnchor(effectIntensityLabel, screenWidth / 1.163);

        CircularSlider effectSelector = new CircularSlider(5, true, "/Knobs/knob-black-fg.png");
        effectSelector.valueProperty().addListener((observable, oldValue, newValue) -> {

            int effectSelectorValue = newValue.intValue();
            controller.setEffect(effectSelectorValue);
            // Gets the saved state of the selected effects mix settings and redraws the
            // knob
            float savedMix = controller.getCurrentEffectMix();
            float knobValue = savedMix * 270.0f;
            effectIntensity.updateAngle(knobValue);
        });
        AnchorPane.setTopAnchor(effectSelector, (screenHeight / 2));
        AnchorPane.setLeftAnchor(effectSelector, (screenWidth / 1.15));

        ImageView effectSelectorImg = new ImageView(EFFECT_SELECTOR_KNOB);
        effectSelectorImg.setFitHeight(70);
        effectSelectorImg.setFitWidth(70);

        AnchorPane.setTopAnchor(effectSelectorImg, (screenHeight / 2));
        AnchorPane.setLeftAnchor(effectSelectorImg, (screenWidth / 1.15));

        Label flanger = new Label("Echo");
        AnchorPane.setTopAnchor(flanger, (screenHeight / 1.90));
        AnchorPane.setLeftAnchor(flanger, (screenWidth / 1.18));

        Label delay = new Label("Flanger");
        AnchorPane.setTopAnchor(delay, (screenHeight / 2));
        AnchorPane.setLeftAnchor(delay, (screenWidth / 1.18));

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
        AnchorPane.setLeftAnchor(masterVolume, (screenWidth / 1.15) + 20);

        Label masterVolumeLabel = new Label();
        masterVolumeLabel.setPrefSize(95, 10);
        masterVolumeLabel.setText("Master Volume");
        AnchorPane.setTopAnchor(masterVolumeLabel, (screenHeight / 1.3) + 85);
        AnchorPane.setLeftAnchor(masterVolumeLabel,
                ((screenWidth / 1.15) - (masterVolumeLabel.getPrefWidth() / 2)) + 25);

        primaryPane.getChildren().addAll(effectIntensity, effectIntensityLabel, effectSelector, delay, flanger,
                masterVolume,
                masterVolumeLabel, effectIntensityImg, effectSelectorImg);
    }
*/
}