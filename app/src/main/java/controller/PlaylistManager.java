package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import model.Playlist;
import view.MainFrame;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class PlaylistManager {

	ObservableList<String> songsGUI = FXCollections.observableArrayList();
	ObservableList<String> playlistsGUI = FXCollections.observableArrayList();
	ArrayList<Playlist> playlists = new ArrayList<>();
	File dataDestinationFile;
	Random randomSongPicker = new Random();
	MainFrame frame;

	/**
	 * The destinationfile is set to the .dat file where all the playlist objects
	 * are stored
	 * 
	 * @param frame
	 */
	public PlaylistManager(MainFrame frame) {
		this.frame = frame;
		dataDestinationFile = new File("src/main/resources/data.dat").getAbsoluteFile();
	}

	/**
	 * Saves the playlist objects to a .dat file in order to maintain state when
	 * closing the application
	 */
	public void savePlaylistData() {
		try (FileOutputStream fos = new FileOutputStream(dataDestinationFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeInt(playlists.size());
			for (int i = 0; i < playlists.size(); i++) {
				oos.writeObject(playlists.get(i));
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads the .dat file, checks if the .dat file is corrupted and refreshes the
	 * GUI
	 */
	public void loadPlaylistData() {
		try (FileInputStream fis = new FileInputStream(dataDestinationFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(bis)) {

			int playlistAmount = ois.readInt();
			for (int i = 0; i < playlistAmount; i++) {
				playlists.add((Playlist) ois.readObject());
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("No Playlists Found or data.dat file Corrupted");
		}
		updatePlaylistGUI();
		frame.selectPlaylistIndex(0);
	}

	/**
	 * utility method for finding a playlist
	 * 
	 * @param playlistName
	 */
	public Playlist findPlaylist(String playlistName) { // UTILITY
		for (int i = 0; i < playlists.size(); i++) {
			if (playlists.get(i).getName().equals(playlistName)) {
				return playlists.get(i);
			}
		}
		return null;
	}

	/**
	 *
	 * @param the name of the playlist
	 * @return ObservableList with the songs in a playlist
	 */
	public ObservableList<String> getPlaylistSongs(String name) {
		Playlist playlist = findPlaylist(name);
		ObservableList<String> songs = FXCollections.observableArrayList();
		HashSet<String> songPaths = playlist.getSongPaths();
		songs.addAll(songPaths);
		return songs;
	}

	public void updatePlaylistGUI() {
		playlistsGUI.clear();
		playlistsGUI.add("New Playlist");
		for (int i = 0; i < playlists.size(); i++) {
			playlistsGUI.add(playlists.get(i).getName());
		}
	}

	/**
	 * loops through the songs folder to load all the songs on startup
	 */
	public void addSongsFromResources() {
		File[] files = new File("src/main/resources/songs/").getAbsoluteFile().listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				songsGUI.add(files[i].getName());
			}
		}
	}

	/**
	 * Creates a new playlist if the user chooses more then one index they are all
	 * added to the new Playlist
	 *
	 * @param name
	 * @param songIndices
	 */
	public void createNewPlaylist(String name, ObservableList<Integer> songIndices) {
		HashSet<String> songPaths = new HashSet<>();
		for (int i = 0; i < songIndices.size(); i++) {
			songPaths.add(songsGUI.get(songIndices.get(i)));
		}
		playlists.add(new Playlist(name, songPaths));
		updatePlaylistGUI();
		frame.selectPlaylistIndex(0);
	}

	/**
	 * adds the songs to a new playlist if the user chooses more then one index they
	 * are all
	 * added to the new Playlist
	 *
	 * @param playlistName
	 * @param selectedIndices
	 */

	public void addToPlaylist(String playlistName, ObservableList<Integer> selectedIndices) {
		if (playlistName.equals("New Playlist")) {
			String newName = newPlaylistName();
			if (newName != null) {
				createNewPlaylist(newName, selectedIndices);
			}
		} else {
			Playlist playlist = findPlaylist(playlistName);
			for (int i = 0; i < selectedIndices.size(); i++) {
				playlist.addSong(songsGUI.get(selectedIndices.get(i)));
			}
		}
	}

	/**
	 * Attempts to return a new playlist name, as long as the name isn't taken,
	 * blank, or "New Playlist".
	 */
	public String newPlaylistName() {
		String input = frame.promptUserInput("New Playlist", "Input Playlist Name");
		if (input != null) {
			if (input.isEmpty() || input.isBlank()) {
				frame.userMessage(Alert.AlertType.ERROR, "Playlist Name is Blank");
				return null;
			}
		} else
			return null;
		for (int i = 0; i < playlistsGUI.size(); i++) {
			if (playlistsGUI.get(i).equals(input)) {
				frame.userMessage(Alert.AlertType.ERROR, "Playlist Name Taken");
				return null;
			}
		}
		return input;
	}

	/**
	 * Changes the name of a playlist to the given string.
	 *
	 * @param playlistName
	 */
	public String editPlaylistName(String playlistName) {
		Playlist playlist = findPlaylist(playlistName);
		String newName = newPlaylistName();
		if (newName != null) {
			playlist.setName(newName);
			updatePlaylistGUI();
			frame.selectPlaylistIndex(0);
			return newName;
		}
		return playlistName;
	}

	public String randomSong() {
		return songsGUI.get(randomSongPicker.nextInt(0, songsGUI.size()));
	}

	/**
	 * Removes a given playlist from the list of playlists if a user confirms.
	 *
	 * @param playlistName
	 */
	public void deletePlaylist(String playlistName) {
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

	/**
	 * Removes selected songs from a given playlist.
	 *
	 * @param playlistName
	 * @param selectedItems names of the songs to be removed
	 */
	public void removeSongsFromPlaylist(String playlistName, ObservableList<String> selectedItems) {
		Playlist playlist = findPlaylist(playlistName);
		for (int j = 0; j < selectedItems.size(); j++) {
			playlist.removeSong(selectedItems.get(j));
		}
	}

	public ObservableList<String> getSongsGUI() {
		return songsGUI;
	}

	public ObservableList<String> getPlaylistsGUI() {
		return playlistsGUI;
	}
}
