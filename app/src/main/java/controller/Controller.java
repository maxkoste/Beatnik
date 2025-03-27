package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedArrayType;
import java.nio.file.Files;
import java.nio.file.Paths;

import dsp.AudioPlayer;


public class Controller {
  AudioPlayer audioPlayer;
  MainFrame frame;

  public Controller(Stage primaryStage) {
    audioPlayer = new AudioPlayer();
    frame = new MainFrame(primaryStage, this);
    addSongs();
  }

  public void playSong() {
    audioPlayer.playAudio();
  }

  public void setMasterVolume(float volume){
    audioPlayer.setVolume(volume);
  }

  public void moveFile(File sourceFile, String destinationPath) throws IOException {
    System.out.println("File move attempt");
    File desinationFile = new File(destinationPath);
    System.out.println(sourceFile.toPath());
    System.out.println(desinationFile.toPath());
    Files.copy(sourceFile.toPath(), desinationFile.toPath());
    frame.addSong(desinationFile.getName());
    System.out.println("File moved");
  }

  public void addSongs() {
    File[] files = new File(String.valueOf(Paths.get("src/main/resources/songs/"))).listFiles();
    String[] songs = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      songs[i] = files[i].getName();
    }
    frame.addSongs(songs);
  }

  public void setSong(int channel, String path) {
    if (channel == 1) {

    } else;
  }
}
