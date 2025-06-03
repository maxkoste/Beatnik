package view;

import javafx.event.ActionEvent;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class Soundboard implements EventHandler<ActionEvent> {
	private Controller controller;
	private Scene soundbarScene;
	private int gridSize;
	private Stage stage;

	/**
	 * @param controller
	 */
	public Soundboard(Controller controller, Stage primaryStage) {

		this.gridSize = 4;
		this.controller = controller;

		initializeSoundBoard(primaryStage);
	}

	/**
	 * Creates a stage for the pop up window that is this class
	 * populates it with buttons and loads its own .css file for styling
	 * applies images for the gui artefacts and creates a gridPane layout for
	 * managing the layout
	 */
	private void initializeSoundBoard(Stage primaryStage) {
		this.stage = new Stage();
		this.stage.setAlwaysOnTop(true);
		stage.setTitle("Soundboard");
		stage.setResizable(false);
		stage.initOwner(primaryStage);
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

		Button[] buttons = new Button[12];

		Button btn1 = new Button("1");
		buttons[0] = btn1;
		Button btn2 = new Button("2");
		buttons[1] = btn2;
		Button btn3 = new Button("3");
		buttons[2] = btn3;
		Button btn4 = new Button("4");
		buttons[3] = btn4;
		Button btn5 = new Button("5");
		buttons[4] = btn5;
		Button btn6 = new Button("6");
		buttons[5] = btn6;
		Button btn7 = new Button("7");
		buttons[6] = btn7;
		Button btn8 = new Button("8");
		buttons[7] = btn8;
		Button btn9 = new Button("9");
		buttons[8] = btn9;
		Button btn10 = new Button("10");
		buttons[9] = btn10;
		Button btn11 = new Button("11");
		buttons[10] = btn11;
		Button btn12 = new Button("12");
		buttons[11] = btn12;

		int counter = 0;
		String[] resources = { "/Symbols/daddy.png", "/Symbols/fart.png", "/Symbols/horns.png", "/Symbols/yipi.png",
				"/Symbols/1.png", "/Symbols/2.png", "/Symbols/3.png", "/Symbols/4.png",
				"/Symbols/5.png", "/Symbols/6.png", "/Symbols/7.png", "/Symbols/8.png" };

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
		btn5.setOnAction(e -> controller.playSoundEffect(1));
		btn6.setOnAction(e -> controller.playSoundEffect(2));
		btn7.setOnAction(e -> controller.playSoundEffect(3));
		btn8.setOnAction(e -> controller.playSoundEffect(4));
		btn9.setOnAction(e -> controller.playSoundEffect(1));
		btn10.setOnAction(e -> controller.playSoundEffect(2));
		btn11.setOnAction(e -> controller.playSoundEffect(3));
		btn12.setOnAction(e -> controller.playSoundEffect(4));

		grid.add(btn1, 0, 1);
		grid.add(btn2, 0, 2);
		grid.add(btn3, 1, 1);
		grid.add(btn4, 1, 2);
		grid.add(btn5, 2, 1);
		grid.add(btn6, 2, 2);
		grid.add(btn7, 3, 1);
		grid.add(btn8, 3, 2);
		grid.add(btn9, 0, 3);
		grid.add(btn10, 1, 3);
		grid.add(btn11, 2, 3);
		grid.add(btn12, 3, 3);

		CircularSlider volume = new CircularSlider(9, false, "/Knobs/knob-blue-fg.png");
		volume.valueProperty().addListener((observable, oldValue, newValue) -> {
			controller.setSoundboardVolume(newValue.floatValue() / 270);
		});
		volume.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		volume.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
		volume.setScaleX(0.6);
		volume.setScaleY(0.6);
		grid.add(volume, 3, 0);

		ImageView cueImg = new ImageView("/Knobs/knob-bg.png");
		cueImg.fitWidthProperty().bind(volume.widthProperty());
		cueImg.fitHeightProperty().bind(volume.heightProperty());
		cueImg.setMouseTransparent(true);
		cueImg.setScaleX(0.6);
		cueImg.setScaleY(0.6);
		grid.add(cueImg, 3, 0);
	}

	/**
	 * Is activated when the button on the leftpanel is pressed
	 * shows this stage
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		this.stage.show();
	}
}
