package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedArrayType;
import java.nio.file.Files;
import java.nio.file.Paths;

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

  public void moveFile(File sourceFile, String destinationPath) throws IOException {
    System.out.println("File move attempt");
    File desinationFile = new File(destinationPath);
    System.out.println(sourceFile.toPath());
    System.out.println(desinationFile.toPath());
    Files.copy(sourceFile.toPath(), desinationFile.toPath());
    System.out.println("File moved");
  }
}
