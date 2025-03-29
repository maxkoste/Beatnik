package controller;

import javafx.stage.Stage;
import view.MainFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import dsp.MediaPlayer;

public class Controller {
    MediaPlayer audioPlayer;
    MainFrame frame;

    public Controller(Stage primaryStage) {
        audioPlayer = new MediaPlayer();

        frame = new MainFrame(this);
        frame.start(primaryStage);
        addSongs();
        addPlaylists();
    }

    // plays the song from the MediaPlayer class
    public void playSong() {
        audioPlayer.playAudio();
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
        frame.addSong(desinationFile.getName());
        System.out.println("File moved into the songs folder");
    }

    public void addSongs() {
        File[] files = new File("src/main/resources/songs/").getAbsoluteFile().listFiles();
        String[] songs = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            songs[i] = files[i].getName();
        }
        frame.addSong(songs);
    }

    public void addPlaylists() {
        String[] playlists = { "-", "New Playlist", "Beats", "Quinceañera", "Funeral" };
        frame.addPlaylist(playlists);
    }

    public void setSong(int channel, String path) {
        if (channel == 1) {
            audioPlayer.setSong(path);
        } else
            ;
    }
}
