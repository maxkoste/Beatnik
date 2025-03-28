package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import view.CircularSlider;

public class Main extends Application {

    public static void main(String[] args) {
        //Application.launch(CircularSlider.class, args);
        launch(args);
        AudioPlayBackTest test = new AudioPlayBackTest();
        test.setUp();
        test.playAudio();
    }

    @Override
    public void start(Stage primaryStage) {
        Controller controller = new Controller(primaryStage);
    }
}
