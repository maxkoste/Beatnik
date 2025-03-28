package controller;

import javafx.application.Application;
import javafx.stage.Stage;
import view.CircularSlider;

public class Main extends Application {

    public static void main(String[] args) {
        // Run the audio test
        // AudioPlayBackTest test = new AudioPlayBackTest();
        // test.setUp();
        // test.playAudio();

        // // After 2 seconds, gradually increase the effect mix
        // try {
        //     Thread.sleep(2000);
        //     test.setEffectMix(1.0f);
        //     Thread.sleep(500);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Controller controller = new Controller(primaryStage);
    }
}
