package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.io.*;
import java.nio.file.Files;

import dsp.MediaPlayer;

public class Controller {
    MediaPlayer audioPlayer;
    MainFrame frame;
    PlaylistManager playlistManager;

    public Controller(Stage primaryStage) {
        audioPlayer = new MediaPlayer();
        frame = new MainFrame(this);
        playlistManager = new PlaylistManager(frame);
        frame.registerPlaylistManager(playlistManager);
        startUp(primaryStage);
    }

    public void startUp(Stage primaryStage) {
        frame.start(primaryStage);
        playlistManager.addSongsFromResources();
        playlistManager.loadPlaylistData();
        frame.selectPlaylistIndex(0);
    }

    // plays the song from the MediaPlayer class
    public void playSong() {
        audioPlayer.playAudio();
        //audioPlayer.testEqualizer();
    }

    public void setTreble(float trebleGain){
        audioPlayer.setTreble(trebleGain);
    }
    
    public void setBass(float bassGain){
        audioPlayer.setBass(bassGain);
    }

    public void setMasterVolume(float volume) {
        audioPlayer.setVolume(volume);
    }

    public void moveFile(File sourceFile, String destinationPath) throws IOException {
        System.out.println("File move attempt");
        File desinationFile = new File(destinationPath);
        System.out.println(sourceFile.toPath());
        System.out.println(desinationFile.toPath());
        Files.copy(sourceFile.toPath(), desinationFile.toPath());
        playlistManager.getSongsGUI().add(desinationFile.getName());
        System.out.println("File moved into the songsGUI folder");
    }

    public void setSong(int channel, String songName) {
        if (channel == 1) {
            audioPlayer.setSong(songName);
        } else;
    }
}
