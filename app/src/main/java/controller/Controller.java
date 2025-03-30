package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.Playlist;
import view.MainFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

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
        HashSet<String> songPaths = new HashSet<>();
        songPaths.add("test.wav");
        playlists.add(new Playlist("Test", songPaths));

        // TODO: Collect playlists from .dat file

        updatePlaylistGUI();
    }

    public ObservableList<String> getPlaylistSongs(String name) {
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(name)) {
                ObservableList<String> songs = FXCollections.observableArrayList();
                HashSet<String> songPaths = playlists.get(i).getSongPaths();
                songs.addAll(songPaths);
                return songs;
            }
        }
        return null;
    }

    public void updatePlaylistGUI() {
        playlistsGUI.clear(); //TODO: Maybe better way of keeping a playlist "clean"
        playlistsGUI.add("New Playlist");
        for (int i = 0; i < playlists.size(); i++) {
            System.out.println(playlists.get(i).getName());
            playlistsGUI.add(playlists.get(i).getName());
        }
    }

    public void createNewPlaylist(String name, ObservableList<Integer> songIndices) {
        HashSet<String> songPaths = new HashSet<>();
        for (int i = 0; i < songIndices.size(); i++) {
            songPaths.add(songsGUI.get(songIndices.get(i)));
        }
        playlists.add(new Playlist(name, songPaths));
        updatePlaylistGUI();
        frame.selectPlaylistIndex(0);
    }

    public void addToPlaylist(String playlistName) {
        ObservableList<Integer> selectedIndices = frame.getSelectedIndices();

        if (playlistName.equals("New Playlist")) {
            String newName = newPlaylistName();
            if (newName != null) {
                createNewPlaylist(newName, selectedIndices);
            }
        } else {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).getName().equals(playlistName)) {
                    for (int j = 0; j < selectedIndices.size(); j++) {
                        playlists.get(i).addSong(songsGUI.get(selectedIndices.get(j)));
                    }
                    return;
                }
            }
        }
    }

    public String newPlaylistName() { //TODO: Maybe improve various null-checks, feels like too many
        String input = frame.promptUserInput("New Playlist", "Input Playlist Name");
        if (input != null) {
            if (input.isEmpty() || input.isBlank()) {
                frame.userMessage(Alert.AlertType.ERROR, "Playlist Name is Blank");
                return null;
            }
        } else return null;
        for (int i = 0; i < playlistsGUI.size(); i++) {
            if (playlistsGUI.get(i).equals(input)) {
                frame.userMessage(Alert.AlertType.ERROR, "Playlist Name Taken");
                return null;
            }
        }
        return input;
    }

    public String editPlaylistName(String playlistName) {
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(playlistName)) {
                String newName = newPlaylistName();
                if (newName != null) {
                    playlists.get(i).setName(newName);
                    updatePlaylistGUI();
                    frame.selectPlaylistIndex(0);
                    return newName;
                }
            }
        }
        return playlistName;
    }

    public void deletePlaylist(String playlistName) { //TODO: Remove from .dat
        if (frame.userConfirm("Are you sure you want to delete " + playlistName + "?")) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).getName().equals(playlistName)) {
                    playlists.remove(i);
                    updatePlaylistGUI();
                    frame.selectPlaylistIndex(0);
                    return;
                }
            }
        }
    }

    public void removeSongsFromPlaylist(String playlistName, ObservableList<String> selectedItems) { //TODO: Playlist finder method for code-reuse
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getName().equals(playlistName)) {
                Playlist playlist = playlists.get(i);
                for (int j = 0; j < selectedItems.size(); j++) {
                    playlist.removeSong(selectedItems.get(j));
                }
            }
        }
    }

    public void setSong(int channel, String songName) {
        if (channel == 1) {
            audioPlayer.setSong(songName);
        } else;
    }
}
