package view;

import controller.Controller;
import controller.PlaylistManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
        primaryStage.getIcons().add(new Image("/Logo/beatnik-logo.png"));

        playlistStage = new Stage();
        playlistStage.setTitle("All Songs");
        playlistStage.setResizable(true);

        StackPane root = new StackPane(); // Root layout with padding background
        primaryPane = new GridPane(); // Pane which contains all content
        songsPane = new BorderPane(); // Pane which contains songs popup content
        playlistsPane = new BorderPane(); // Pane which contains currentPlaylist popup content

        primaryPane.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(root.getWidth(), root.getHeight()), root.widthProperty(), root.heightProperty()));

        primaryPane.maxHeightProperty().bind(primaryPane.maxWidthProperty());
        primaryPane.minWidthProperty().bind(primaryPane.maxWidthProperty());
        primaryPane.minHeightProperty().bind(primaryPane.maxHeightProperty());

        primaryPane.setAlignment(Pos.CENTER);
        primaryPane.setGridLinesVisible(true); // TODO: TEMPORARY?

        root.getChildren().add(primaryPane);

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

        Scene primaryScene = new Scene(root, (screenHeight * 0.9), (screenHeight * 0.9)); // Add pane to scene

        songsScene = new Scene(songsPane, (screenHeight * 0.7), (screenHeight * 0.7));
        playlistsScene = new Scene(playlistsPane, (screenHeight * 0.7), (screenHeight * 0.7));

        playlistsScene.getStylesheets().add("styles.css");
        songsScene.getStylesheets().add("styles.css");
        primaryStage.getIcons().add(new Image("/Logo/beatnik-logo.png"));
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
}