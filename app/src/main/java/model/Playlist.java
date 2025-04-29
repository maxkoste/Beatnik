package model;

import java.io.Serializable;
import java.util.HashSet;

public class Playlist implements Serializable {
    private String name;
    private HashSet<String> songPaths;

    /**
     * @param name the name of the playlist
     * @param fileName the file name ending with the file format 
     */
    public Playlist(String name, HashSet<String> fileName) {
        this.name = name;
        this.songPaths = fileName;
    }
    /**
     * Adds the songpath to the String HashSet
     * songPath can be null or empty
     * @param songPath
     */
    public void addSong(String songPath) {
        songPaths.add(songPath);
    }
    /**
     * Searches through the array of songs
     * Removes the specified song from the playlist if the song is found,
     * otherwise it does nothing.
     * @param songPath the name of the file ending with the file format.
     */
    public void removeSong(String songPath) {
        for (int i = 0; i < songPaths.size(); i++) {
            if (songPaths.contains(songPath)) {
                songPaths.remove(songPath);
                return;
            }
        }
    }
    /**
     * Gets the instance-variable name
     * may return empty or null string
     * @return
     */
    public String getName() {
        return name;
    }
    /**
     * Setting the name of the instance-variable
     * allows empty and/or null strings
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * Returns a HashSet 
     * Hashsets doesn't:
     * allow any duplicated values
     * element can be null
     * no order guarantee
     * 
     * May return null
     * @return
     */
    public HashSet<String> getSongPaths() {
        return songPaths;
    }
}
