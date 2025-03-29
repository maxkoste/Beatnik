package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedArrayType;
import java.nio.file.Files;
import java.nio.file.Paths;

import dsp.MediaPlayer;


public class Controller {
  MediaPlayer audioPlayer;
  MainFrame frame;

  public Controller(Stage primaryStage) {
    audioPlayer = new MediaPlayer();
    //this is the frame that contains the playlist and the songs
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
    System.out.println("File moved into the songs folder");
  }

  public void addSongs() {
    File[] files = new File("src/main/resources/songs/").getAbsoluteFile().listFiles();
    String[] songs = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      songs[i] = files[i].getName();
      //wanted to see if it prints the correct song name
      System.out.println("Songs added: " + songs[i]);
    }
    frame.addSongs(songs);
  }

  public void setSong(int channel, String path) {
    if (channel == 1) {
      audioPlayer.setSong(path);
    } else;
  }
}
