package view;

import controller.Controller;
import controller.PlaylistManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
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
	private StackPane startUpPane;
	private Stage primaryStage;
	private Stage playlistStage;
	private Scene primaryScene;
	private Scene songsScene;
	private Scene playlistsScene;
	private ProgressBar progressBar;
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
	private CenterPnl centerPnl;
	private int progressCounter;
	private Soundboard soundboard;
	private RightPnl rightPnl;

	public MainFrame(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Starts up all windows and graphical elements in the application
	 * @param primaryStage the main window for the application, created in the Main class
	 * @return the class that handles selecting mixers.
	 */
	public MixerSelectionView start(Stage primaryStage) {
		Dimension screenResolution = Toolkit.getDefaultToolkit().getScreenSize();
		double screenHeight = screenResolution.getHeight();

		primaryStage.setTitle("Beatnik");
		primaryStage.setResizable(true);
		primaryStage.getIcons().add(new Image("/Logo/beatnik-logo4.png"));

		playlistStage = new Stage();
		playlistStage.setTitle("All Songs");
		playlistStage.setResizable(false);

		StackPane root = new StackPane(); // Root layout with padding background
		startUpPane = new StackPane(); // Pane which contains startup content
		primaryPane = new GridPane(); // Pane which contains all content
		songsPane = new BorderPane(); // Pane which contains songs popup content
		playlistsPane = new BorderPane(); // Pane which contains currentPlaylist popup content

		primaryPane.maxWidthProperty().bind(Bindings.createDoubleBinding(
				() -> Math.min(root.getWidth(), root.getHeight()), root.widthProperty(), root.heightProperty()));

		primaryPane.maxHeightProperty().bind(primaryPane.maxWidthProperty());
		primaryPane.minWidthProperty().bind(primaryPane.maxWidthProperty());
		primaryPane.minHeightProperty().bind(primaryPane.maxHeightProperty());

		primaryPane.setAlignment(Pos.CENTER);
		primaryPane.setGridLinesVisible(false);

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

		initializeStartUpPane(screenHeight);
		initializeSongsPane();
		initializePlaylistPane();
		topPnl = new TopPnl(this, controller, primaryPane, numCols);
		soundboard = new Soundboard(controller);
    new LeftPnl(soundboard, this, primaryPane, numCols, this.controller);
    rightPnl = new RightPnl(this, primaryPane, numCols);
		centerPnl = new CenterPnl(controller, primaryPane, numCols);

		primaryScene = new Scene(root, (screenHeight * 0.9), (screenHeight * 0.9)); // Add pane to scene
		Scene startUpScene = new Scene(startUpPane, (screenHeight * 0.9), (screenHeight * 0.9));

		songsScene = new Scene(songsPane, (screenHeight * 0.7), (screenHeight * 0.7));
		playlistsScene = new Scene(playlistsPane, (screenHeight * 0.7), (screenHeight * 0.7));

		startUpScene.getStylesheets().add("mainStyle.css");
		primaryScene.getStylesheets().add("mainStyle.css");
		playlistsScene.getStylesheets().add("mainStyle.css");
		songsScene.getStylesheets().add("mainStyle.css");
		primaryStage.setScene(startUpScene); // Finalize window to be shown
		primaryStage.show();

		playlistStage.setScene(songsScene);
		playlistStage.initModality(Modality.APPLICATION_MODAL);

		onClose(primaryStage);
		this.primaryStage = primaryStage;

		MixerSelectionView mixerSelectionView = new MixerSelectionView();
		mixerSelectionView.showAndWait(primaryStage);

		return mixerSelectionView;
	}

	/**
	 * Updates the progress bar of the startup screen.
	 * @param nbrOfSongsToLoad the total number of songs being loaded
	 */
	public void updateLoading(double nbrOfSongsToLoad) {
		Platform.runLater(() -> {
			progressCounter++;
			progressBar.setProgress(progressCounter / nbrOfSongsToLoad);
			if (progressCounter >= nbrOfSongsToLoad) {
				primaryStage.setScene(primaryScene);
			}
		});
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

	/**
	 * Creates the window shown on startup
	 * @param screenHeight height of the users screen
	 */
	public void initializeStartUpPane(double screenHeight) {
		ImageView logo = new ImageView(new Image("/Logo/beatnik-logo2.png"));
		ImageView logoText = new ImageView(new Image("/Logo/beatnik-logo3.png"));

		logo.setFitHeight(screenHeight * 0.35);
		logo.setFitWidth(screenHeight * 0.35);

		logoText.setScaleX(0.4);
		logoText.setScaleY(0.4);

		progressBar = new ProgressBar();

		progressBar.setPrefWidth(screenHeight * 0.35);
		progressBar.setScaleX(1.0);

		HBox progressBarContainer = new HBox(progressBar);
		progressBarContainer.setAlignment(Pos.CENTER);

		VBox logoBox = new VBox(logoText, logo);
		logoBox.setAlignment(Pos.CENTER);

		logoBox.setSpacing(-95);

		// logoTextContainer.setAlignment(Pos.TOP_CENTER);
		VBox container = new VBox(logoBox, progressBarContainer);
		container.setTranslateY(-screenHeight * 0.05);
		container.setSpacing(40);
		container.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
		startUpPane.setAlignment(Pos.CENTER);
		startUpPane.getChildren().add(container);
		// startUpPane.getChildren().add(logoTextContainer);
	}

	/**
	 * Creates the window shown when viewing all songs.
	 */
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

	/**
	 * Creates the window shown when viewing a playlist
	 */
	public void initializePlaylistPane() {
		currentPlaylist = new ListView<>();
		activateDragAndDrop();
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

	/**
	 * Override from eventHandler interface, shows the window with all songs
	 */
	@Override
	public void handle(ActionEvent actionEvent) { // Songsbutton method, basic name from interface
		playlistStage.showAndWait();
	}

	/**
	 * Sets a song outside a playlist if the user double-clicked.
	 */
	public void handleSongSelection(MouseEvent mouseEvent) {
		if (mouseEvent.getClickCount() == 2) {
			if (channelOneActive) {
				controller.setSong(1, songSelector.getSelectedItem());
				setInfoText(false, songSelector.getSelectedItem(), 1);
			} else {
				controller.setSong(2, songSelector.getSelectedItem());
				setInfoText(false, songSelector.getSelectedItem(), 2);
			}
			controller.deactivatePlaylist();
		}
	}

	/**
	 * Opens an OS window where the user can select a .wav or .mp3 file, then adds it to the "resources" folder.
	 */
	public void handleImport(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Import a song");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.mp4", "*.FLAC"));
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

	/**
	 * Shows the selected playlist if one is selected.
	 */
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

	/**
	 * Shows the all songs window if the user is in the playlist window
	 */
	public void handleViewSongs(ActionEvent actionEvent) {
		playlistStage.setTitle("All Songs");
		playlistStage.setScene(songsScene);
	}

	/**
	 * Allows the user to attempt to edit the name of a selected playlist
	 */
	public void handleEditPlaylistName(ActionEvent actionEvent) {
		playlistStage.setTitle(playlistManager.editPlaylistName(playlistSelector.getSelectedItem()));
		playlistManager.savePlaylistData();
	}

	/**
	 * Allows the user to attempt to delete a selected playlist then returns to the all songs window
	 */
	public void handleDeletePlaylist(ActionEvent actionEvent) {
		playlistManager.deletePlaylist(playlistStage.getTitle());
		handleViewSongs(actionEvent);
		playlistManager.savePlaylistData();
	}

	/**
	 * Allows the user to remove any amount of selected songs from a playlist.
	 */
	public void handleRemoveSongsFromPlaylist(ActionEvent actionEvent) {
		playlistManager.removeSongsFromPlaylist(playlistStage.getTitle(), playlistSongSelector.getSelectedItems());
		currentPlaylist.getItems().removeAll(playlistSongSelector.getSelectedItem());
		playlistManager.savePlaylistData();
	}

	/**
	 * Allows the user to add any amount of songs to a playlist.
	 */
	public void handleAddToPlaylist(ActionEvent actionEvent) {
		String playlistSelected = playlistSelector.getSelectedItem();
		playlistManager.addToPlaylist(playlistSelected, songSelector.getSelectedIndices());
		playlistManager.savePlaylistData();
	}

	/**
	 * Adds listeners for a variety of mouse movements to allow a user to drag and drop items
	 * in a playlist. Due to how playlists are shown and stores, the order is not permanently
	 * changed, but will remain active until the playlist is closed and opened again.
	 */
	private void activateDragAndDrop() {
		currentPlaylist.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<>() {
				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : item);
				}
			};

			cell.setOnDragDetected(event -> {
				if (!cell.isEmpty()) {
					Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
					ClipboardContent content = new ClipboardContent();
					content.putString(cell.getItem());
					db.setContent(content);
					event.consume();
				}
			});

			cell.setOnDragOver(event -> {
				if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
					event.acceptTransferModes(TransferMode.MOVE);
				}
				event.consume();
			});

			cell.setOnDragEntered(event -> {
				if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
					cell.setStyle("-fx-background-color: lightgray;");
				}
			});

			cell.setOnDragExited(event -> {
				cell.setStyle("");
			});

			cell.setOnDragDropped(event -> {
				Dragboard db = event.getDragboard();
				boolean success = false;
				if (db.hasString()) {
					int draggedIdx = currentPlaylist.getItems().indexOf(db.getString());
					int thisIdx = cell.getIndex();
					if (cell.isEmpty()) {
						event.setDropCompleted(false);
						event.consume();
						return;
					}

					if (draggedIdx != thisIdx) {
						String temp = currentPlaylist.getItems().remove(draggedIdx);
						currentPlaylist.getItems().add(thisIdx, temp);
					}
					success = true;
				}
				event.setDropCompleted(success);
				event.consume();
			});

			cell.setOnDragDone(DragEvent::consume);

			return cell;
		});
	}

	/**
	 * Switches which channel is active for both the playlist and all songs windows
	 */
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

	/**
	 * Plays the selected song in a playlist and activates the playlist from the index of the selected
	 * song if the user double-clicked.
	 */
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

	/**
	 * Prompts the user to input a String, currently only used for the playlist name.
	 */
	public String promptUserInput(String title, String headerText) {
		TextInputDialog inputPlaylistName = new TextInputDialog();
		inputPlaylistName.setTitle(title);
		inputPlaylistName.setHeaderText(headerText);
		Optional<String> name = inputPlaylistName.showAndWait();

		return name.orElse(null); // A Java-suggested improvement to an isPresent check. Returns null if the user
		// closed the window etc. instead of throwing an exception.
	}

	/**
	 * Sends a heads-up message to the user.
	 * @param alertType type of alert (info, warning, etc.) which changes window design.
	 */
	public void userMessage(Alert.AlertType alertType, String headerText) {
		Alert message = new Alert(alertType);
		message.setHeaderText(headerText);
		message.showAndWait();
	}

	/**
	 * Allows the user to confirm or exit while performing a risky operation.
	 */
	public boolean userConfirm(String headerText) {
		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
		confirm.setHeaderText(headerText);
		confirm.showAndWait();
		return confirm.getResult() == ButtonType.OK;
	}

	public void resetEffectIntensityAndSelector(){
		rightPnl.resetEffectIntensity();
		rightPnl.resetEffectSelector();
	}
	public void resetChannelOneEffects() {
		centerPnl.resetChannelOneBassTreblePitch();
	}

	public void resetChannelTwoEffects() {
		centerPnl.resetChannelTwoBassTreblePitch();
	}

	public void resetChannelOneVolume(){
		centerPnl.resetChannelOneVolumeSlider();
	}

	public void resetChannelTwoVolume(){
		centerPnl.resetChannelTwoVolumeSlider();
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
		Platform.runLater(() -> {
			topPnl.setInfoText(playlist, song, channel);
		});
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
