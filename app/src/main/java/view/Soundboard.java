package view;

import javafx.event.ActionEvent;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class Soundboard implements EventHandler<ActionEvent> {
	private Controller controller;
	private ImageView[] images;
	private Scene soundbarScene;
	private int gridSize;
	private Stage stage;

	public Soundboard(Controller controller) {

		this.gridSize = 4;
		this.controller = controller;

		initializeSoundBoard();
	}

	private void initializeSoundBoard() {
		this.stage = new Stage();
		this.stage.setAlwaysOnTop(true);
		stage.setTitle("Soundboard");
		stage.setResizable(false);
		GridPane grid = new GridPane();

		for (int i = 0; i < this.gridSize; i++) {
			ColumnConstraints colConstraints = new ColumnConstraints();
			colConstraints.setPercentWidth(100.0 / gridSize);
			colConstraints.setFillWidth(true);
			grid.getColumnConstraints().add(colConstraints);
		}

		for (int i = 0; i < this.gridSize; i++) {
			RowConstraints rowConstraints = new RowConstraints();
			rowConstraints.setPercentHeight(100.0 / gridSize);
			rowConstraints.setFillHeight(true);
			grid.getRowConstraints().add(rowConstraints);
		}

		grid.setGridLinesVisible(true);

		this.soundbarScene = new Scene(grid, 600, 600);
		soundbarScene.getStylesheets().addAll("soundboard.css");
		stage.setScene(soundbarScene);

		Button[] buttons = new Button[4];
		this.images = new ImageView[8];

		Button btn1 = new Button("1");
		buttons[0] = btn1;
		Button btn2 = new Button("2");
		buttons[1] = btn2;
		Button btn3 = new Button("3");
		buttons[2] = btn3;
		Button btn4 = new Button("4");
		buttons[3] = btn4;

		int counter = 0;
		String[] resources = { "/Symbols/daddy.png", "/Symbols/fart.png", "/Symbols/horns.png", "/Symbols/yipi.png" };

		for (Button button : buttons) {
			button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
			button.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
			button.setScaleX(0.6);
			button.setScaleY(0.6);
			button.setText("");
			ImageView image = new ImageView(new Image(resources[counter]));
			counter++;
			image.setScaleX(0.2);
			image.setScaleY(0.2);
			image.setFitWidth(button.getWidth());
			image.setFitHeight(button.getHeight());
			image.setPreserveRatio(true);
			button.setGraphic(image);
		}

		btn1.setOnAction(e -> controller.playSoundEffect(1));
		btn2.setOnAction(e -> controller.playSoundEffect(2));
		btn3.setOnAction(e -> controller.playSoundEffect(3));
		btn4.setOnAction(e -> controller.playSoundEffect(4));

		grid.add(btn1, 1, 1);
		grid.add(btn2, 1, 2);
		grid.add(btn3, 2, 1);
		grid.add(btn4, 2, 2);

		CircularSlider cueVolume = new CircularSlider(9, false, "/Knobs/knob-blue-fg.png");
		cueVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
			double volume = newValue.doubleValue();
			controller.setSoundboardVolume(newValue.floatValue() / 270);
		});
		cueVolume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		cueVolume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		cueVolume.setScaleX(0.6);
		cueVolume.setScaleY(0.6);
		grid.add(cueVolume, 3, 1);

		ImageView cueImg = new ImageView("/Knobs/knob-bg.png");
		cueImg.fitWidthProperty().bind(cueVolume.widthProperty());
		cueImg.fitHeightProperty().bind(cueVolume.heightProperty());
		cueImg.setMouseTransparent(true);
		cueImg.setScaleX(0.6);
		cueImg.setScaleY(0.6);
		grid.add(cueImg, 3, 1);

		// // Slider
		// Slider masterVolume = new Slider();
		// masterVolume.setOrientation(Orientation.VERTICAL);
		// masterVolume.setMax(100.0);
		// masterVolume.setBlockIncrement(20);
		// masterVolume.setShowTickMarks(true);
		// masterVolume.setShowTickLabels(true);
		// masterVolume.setMinorTickCount(0);
		// masterVolume.valueProperty().addListener((observable, oldValue, newValue) ->
		// {
		// controller.setSoundboardVolume(newValue.floatValue() / 100);
		// });
		// masterVolume.setValue(50);
		//
		// GridPane.setRowSpan(masterVolume, 2);
		// grid.add(masterVolume, 3, 1);
	}

	@Override
	public void handle(ActionEvent arg0) {
		this.stage.show();
	}

	public void exit() {
	}
}
