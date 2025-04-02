package controller;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.MainFrame;

import java.io.*;
import java.nio.file.Files;

import dsp.MediaPlayer;


public class Controller {
    MediaPlayer audioPlayer1;
    MediaPlayer audioPlayer2;
    MainFrame frame;
    PlaylistManager playlistManager;
    ObservableList<String> playlistSongPaths; //TODO: Find way of alerting Controller when a song has naturally finished playing
    int currentSongInPlaylist;
    float masterModifier = 0.5F;
    float crossfaderModifier1 = 1.0F;
    float crossfaderModifier2 = 1.0F;
    float latestVolume1 = 50.0F;
    float latestVolume2 = 50.0F;

    public Controller(Stage primaryStage) {
        audioPlayer1 = new MediaPlayer();
        audioPlayer2 = new MediaPlayer();
        frame = new MainFrame(this);
        playlistManager = new PlaylistManager(frame);
        frame.registerPlaylistManager(playlistManager);
        startUp(primaryStage);
    }

    public void startUp(Stage primaryStage) {
        frame.start(primaryStage);
        playlistManager.addSongsFromResources();
        playlistManager.loadPlaylistData();
    }

    public void setSong(int channel, String songName) {
        if (channel == 1) {
            audioPlayer1.setSong(songName);
        } else {
            audioPlayer2.setSong(songName);
        }
        playSong(channel);
    }

    public void startPlaylist(int channel, int selectedIndex, ObservableList<String> songPaths) {
        playlistSongPaths = songPaths; //TODO: Make into a queue or smth? Might not be needed.
        currentSongInPlaylist = selectedIndex;
        setSong(channel, playlistSongPaths.get(currentSongInPlaylist));
    }

    // plays the song from the MediaPlayer class
    public void playSong(int channel) {
        if (channel == 1) {
            audioPlayer1.playAudio();
            setChannelOneVolume(latestVolume1);
        } else {
            audioPlayer2.playAudio();
            setChannelTwoVolume(latestVolume2);
        }
    }

    public void nextSong(int channel) { //TODO: Update GUI with Waveforms and names etc
        if (playlistSongPaths != null) {
            if (!(currentSongInPlaylist >= playlistSongPaths.size() - 1)) {
                currentSongInPlaylist++;
                setSong(channel, playlistSongPaths.get(currentSongInPlaylist));
            } else {
                frame.userMessage(Alert.AlertType.INFORMATION, "Playlist Finished, Skip now Random");
                playlistSongPaths = null;
            }
        } else {
            setSong(channel, playlistManager.randomSong());
        }
    }


    public void setChannelOneVolume(float volume) {
        audioPlayer1.setVolume((volume * masterModifier) * crossfaderModifier1);
        latestVolume1 = volume;
    }

    public void setChannelTwoVolume(float volume) { // Does not use a channel check for speed
        audioPlayer2.setVolume((volume * masterModifier) * crossfaderModifier2);
        latestVolume2 = volume;
    }

    public void setMasterVolume(float masterModifier) { //TODO: Kan vara långsamt
        this.masterModifier = masterModifier;
        setChannelOneVolume(latestVolume1);
        setChannelTwoVolume(latestVolume2);
    }

    public void setCrossfaderModifier(float crossfaderValue) {
        if (crossfaderValue < 50) {
            crossfaderModifier2 = (crossfaderValue / 50.0F);
        } else {
            crossfaderModifier1 = ((100.0F - crossfaderValue) / 50.0F);
        }
        setChannelOneVolume(latestVolume1);
        setChannelTwoVolume(latestVolume2);

    public void setTreble(float trebleCutoff){
        audioPlayer.setTreble(trebleCutoff);
    }
    
    public void setBass(float bassCutoff){
        audioPlayer.setBass(bassCutoff);
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
}
