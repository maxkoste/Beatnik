package model;

import java.util.List;

public class SongData {
    private String name;
    private float[] audioData;
    private List<Double> rmsData;

    public SongData(String name, float[] audioData, List<Double> rmsData) {
      this.name = name;
      this.audioData = audioData;
      this.rmsData = rmsData;
    }

    public String getName() {
      return name;
    }

    public float[] getAudioData() {
      return audioData;
    }

    public List<Double> getRmsData() {
      return rmsData;
    }
}
