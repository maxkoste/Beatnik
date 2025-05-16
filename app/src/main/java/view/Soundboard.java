package view;

import javafx.event.ActionEvent;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Soundboard implements EventHandler<ActionEvent> {
    private Controller controller;
    private Scene soundbarScene;
    private int gridSize;
    private Stage stage;
    private MainFrame mainFrame;

    public Soundboard(MainFrame mainFrame, Controller controller) {
        this.mainFrame = mainFrame;
        this.gridSize = 4;
        this.controller = controller;

        initializeSoundBoard();
    }

    private void initializeSoundBoard() {
        this.stage = new Stage();
        stage.setTitle("Soundboard");
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

        this.soundbarScene = new Scene(grid, 400, 400);
        soundbarScene.getStylesheets().addAll("styles.css");
        stage.setScene(soundbarScene);

        Button btn1 = new Button("1");
        Button btn2 = new Button("2");
        Button btn3 = new Button("3");
        Button btn4 = new Button("4");

        btn1.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn2.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn3.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn4.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn1.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        btn2.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        btn3.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);
        btn4.setMinSize(Double.MIN_VALUE, Double.MIN_VALUE);

        btn1.setOnAction(e -> controller.playSoundEffect(1));
        btn2.setOnAction(e -> controller.playSoundEffect(2));
        btn3.setOnAction(e -> controller.playSoundEffect(3));
        btn4.setOnAction(e -> controller.playSoundEffect(4));

        grid.add(btn1, 1, 1);
        grid.add(btn2, 1, 2);
        grid.add(btn3, 2, 1);
        grid.add(btn4, 2, 2);
    }

    @Override
    public void handle(ActionEvent arg0) {
        this.stage.show();
    }
}
