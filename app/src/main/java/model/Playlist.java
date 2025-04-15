package model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Playlist implements Serializable {
    private String name;
    private HashSet<String> songPaths;

    public Playlist(String name, HashSet<String> songPaths) {
        this.name = name;
        this.songPaths = songPaths;
    }

    public void addSong(String songPath) {
        songPaths.add(songPath);
    }

    public void removeSong(String songPath) {
        for (int i = 0; i < songPaths.size(); i++) {
            if (songPaths.contains(songPath)) {
                songPaths.remove(songPath);
                return;
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashSet<String> getSongPaths() {
        return songPaths;
    }
}
