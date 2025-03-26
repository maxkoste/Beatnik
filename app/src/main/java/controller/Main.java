package controller;

import javafx.application.Application;
import controller.AudioPlayBackTest;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import be.tarsos.dsp.effects.DelayEffect; //test to see that TarsosDSP is working
import view.MainFrame;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Controller controller = new Controller(primaryStage);
    }
}
