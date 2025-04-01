package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import model.Playlist;
import view.MainFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import dsp.MediaPlayer;

public class Controller {
    MediaPlayer audioPlayer;
    MainFrame frame;
    ObservableList<String> songsGUI = FXCollections.observableArrayList();
    ObservableList<String> playlistsGUI = FXCollections.observableArrayList();
    ArrayList<Playlist> playlists = new ArrayList<>();

    public Controller(Stage primaryStage) {
        audioPlayer = new MediaPlayer();

        addSongsFromResources();
        addPlaylistsFromResources();

        frame = new MainFrame(this);
        frame.start(primaryStage);
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
        songsGUI.add(desinationFile.getName());
        System.out.println("File moved into the songsGUI folder");
    }

    public ObservableList<String> getSongsGUI() {
        return songsGUI;
    }

    public ObservableList<String> getPlaylistsGUI() {
        return playlistsGUI;
    }

    public void addSongsFromResources() {
        File[] files = new File("src/main/resources/songs/").getAbsoluteFile().listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                songsGUI.add(files[i].getName());
            }
        }
    }

    public void addPlaylistsFromResources() {
        ArrayList<String> songPaths = new ArrayList<>();
        songPaths.add("test.wav");
        playlists.add(new Playlist("Test", songPaths));

        // TODO: Collect playlists from .dat file

        playlistsGUI.addAll("Select Playlist", "New Playlist");

        for (int i = 0; i < playlists.size(); i++) {
            playlistsGUI.add(playlists.get(i).getName());
        }
    }

    public ObservableList<String> getPlaylistSongs(String name) {
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(name)) {
                ObservableList<String> songs = FXCollections.observableArrayList();
                ArrayList<String> songPaths = playlists.get(i).getSongPaths();
                songs.addAll(songPaths);
                return songs;
            }
        }
        return null;
    }

    public void createNewPlaylist(String name, ObservableList<Integer> songIndices) {
        ArrayList<String> songPaths = new ArrayList<>();
        for (int i = 0; i < songIndices.size(); i++) {
            songPaths.add(songsGUI.get(songIndices.get(i)));
        }
        playlists.add(new Playlist(name, songPaths));
        playlistsGUI.add(name);
    }

    public void setSong(int channel, String songName) {
        if (channel == 1) {
            audioPlayer.setSong(songName);
        } else
            ;
    }
}
