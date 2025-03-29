package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

public class Playlist {
  private String name;
  private ArrayList<String> songPaths;

  public Playlist(String name, ArrayList<String> songPaths) {
    this.name = name;
    this.songPaths = songPaths;
  }

  public void addSong(String songPath) {
    songPaths.add(songPath);
  }

  public String getName() {
    return name;
  }

  public ArrayList<String> getSongPaths() {
    return songPaths;
  }
}
