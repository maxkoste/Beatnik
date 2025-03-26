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

public class Main extends Application{
    private AudioPlayBackTest audioPlayBackTest = new AudioPlayBackTest();

    //Test to play audio.
    @Override 
    public void start(Stage primaryStage) throws Exception {

        Button playBtn = new Button("Play");
        
        playBtn.setOnAction(e -> audioPlayBackTest.playAudio());
        
        VBox rootBox = new VBox(10, playBtn);

        Scene scene = new Scene(rootBox, 300, 300);
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Audio player test!");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
