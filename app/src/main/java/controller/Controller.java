package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.lang.reflect.AnnotatedArrayType;

import dsp.AudioPlayer;


public class Controller {
  AudioPlayer audioPlayer;
  MainFrame frame;

  public Controller(Stage primaryStage) {
    frame = new MainFrame(primaryStage, this);
    audioPlayer = new AudioPlayer();
  }

  public void playSong() {
    audioPlayer.playAudio();
  }

  public void setMasterVolume(float volume){
    audioPlayer.setVolume(volume);
  }
}
