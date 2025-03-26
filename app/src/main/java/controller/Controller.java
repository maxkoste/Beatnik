package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.lang.reflect.AnnotatedArrayType;

public class Controller {
  AudioPlayBackTest audioPlayBackTest;
  MainFrame frame;

  public Controller(Stage primaryStage) {
    frame = new MainFrame(primaryStage, this);
    audioPlayBackTest = new AudioPlayBackTest();
  }

  public void playSong() {
    audioPlayBackTest.playAudio();
  }
}
