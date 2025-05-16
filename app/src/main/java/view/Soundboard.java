package view;

import javafx.event.ActionEvent;

import controller.Controller;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Soundboard implements EventHandler<ActionEvent> {
    private Controller controller;
    private Scene soundbarScene;
    private int gridSize;
    private Stage stage;
    private MainFrame mainFrame; 

    public Soundboard(MainFrame mainFrame, Controller controller, int gridSize){
        this.mainFrame = mainFrame;
        this.gridSize = gridSize;
        this.controller = controller;
        
        initializeSoundBoard();
    }
    
    private void initializeSoundBoard(){
        this.stage = new Stage();
        stage.setTitle("Soundboard");
        GridPane grid = new GridPane();

        for (int i = 0; i < this.gridSize; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0/gridSize);
            colConstraints.setFillWidth(true);
            
        }

        this.soundbarScene = new Scene(grid);
        stage.setScene(soundbarScene);
    }
    

    @Override
    public void handle(ActionEvent arg0) {
        this.stage.show();
    }
}
