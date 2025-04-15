package controller;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.MainFrame;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dsp.MediaPlayer;

public class Controller {
    MediaPlayer audioPlayer1;
    MediaPlayer audioPlayer2;
    MainFrame frame;
    PlaylistManager playlistManager;
    TimerThreadOne timerThreadOne;
    TimerThreadTwo timerThreadTwo;
    ObservableList<String> playlistSongPaths; // TODO: Find way of alerting Controller when a song has naturally
                                              // finished playing
    int currentPosInPlaylist;
    float masterModifier = 0.5F;
    float crossfaderModifier1 = 1.0F;
    float crossfaderModifier2 = 1.0F;
    float latestVolume1 = 50.0F;
    float latestVolume2 = 50.0F;
    AudioDispatcher dispatcherOne;
    AudioDispatcher dispatcherTwo;
    String currentEffect;

    public Controller(Stage primaryStage) {
        audioPlayer1 = new MediaPlayer();
        audioPlayer2 = new MediaPlayer();
        timerThreadOne = new TimerThreadOne();
        timerThreadTwo = new TimerThreadTwo();
        frame = new MainFrame(this);
        playlistManager = new PlaylistManager(frame);
        frame.registerPlaylistManager(playlistManager);
        this.currentEffect = "delay";
        startUp(primaryStage);
    }

    public void startUp(Stage primaryStage) {
        frame.start(primaryStage);
        playlistManager.addSongsFromResources();
        playlistManager.loadPlaylistData();
        timerThreadOne.start();
        timerThreadTwo.start();
    }

    public void setSong(int channel, String songPath) {
        if (channel == 1) {
            audioPlayer1.setSong(songPath);
        } else {
            audioPlayer2.setSong(songPath);
        }
        playSong(channel);
        frame.setWaveformAudioData(extract(songPath), channel);
    }

    public void startPlaylist(int channel, int selectedIndex, ObservableList<String> songPaths) {
        playlistSongPaths = songPaths; // TODO: Make into a queue or smth? Might not be needed.
        currentPosInPlaylist = selectedIndex;
        setSong(channel, playlistSongPaths.get(currentPosInPlaylist));
    }

    // plays the song from the MediaPlayer class
    public void playSong(int channel) {
        if (channel == 1) {
            audioPlayer1.playAudio();
            setChannelOneVolume(latestVolume1);
            dispatcherOne = audioPlayer1.getAudioDispatcher();
        } else {
            audioPlayer2.playAudio();
            setChannelTwoVolume(latestVolume2);
            dispatcherTwo = audioPlayer2.getAudioDispatcher();
        }
    }

    public void setEffect(int effectSelectorValue) {
        /**
         * 0 = 1 delay
         * 68 = 2 Flanger
         * 135 = 3
         * 204 = 4
         * 270 = 5
         */
        switch (effectSelectorValue) {
            case 0:
                this.currentEffect = "delay";
                break;
            case 68:
                this.currentEffect = "flanger";
                break;
            case 135:
                System.out.println("Effect nr 3");
                break;
            case 204:
                System.out.println("Effect nr 4 ");
                break;
            case 270:
                System.out.println("Effect nr 5");
                break;
            default:
                System.out.println("Something went wrong...");
        }
    }

    public void nextSong(int channel) { // TODO: Update GUI with names etc
        if (playlistSongPaths != null) {
            if (!(currentPosInPlaylist >= playlistSongPaths.size() - 1)) {
                currentPosInPlaylist++;
                setSong(channel, playlistSongPaths.get(currentPosInPlaylist));
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

    public void setMasterVolume(float masterModifier) { // TODO: Kan vara långsamt
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
    }

    public void setTreble1(float trebleGain) {
        audioPlayer1.setTreble(trebleGain);
    }

    public void setTreble2(float trebleGain) {
        audioPlayer2.setTreble(trebleGain);
    }

    public void setBass1(float bassGain) {
        audioPlayer1.setBass(bassGain);
    }

    public void setBass2(float bassGain) {
        audioPlayer2.setBass(bassGain);
    }

    /**
     * set the mix value of the effect
     * 0 = 100% dry signal (no effect)
     * 1 = 100% effect signal (only effect)
     * all the effects should be applied on both audio-signals
     * 
     * @param mix        float value between 0-1f
     * @param effectType indicating the effect being applied.
     */
    public void setEffectMix(float mix) {
        switch (this.currentEffect) {
            case "delay":
                audioPlayer1.setDelayEffectMix(mix);
                audioPlayer2.setDelayEffectMix(mix);
                break;
            case "flanger":
                audioPlayer1.setFlangerEffectMix(mix);
                audioPlayer2.setFlangerEffectMix(mix);
                break;
            default:
                break;
        }
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

    private float[] extract(String filePath) { // TODO: Collect all fileData att app launch
        List<Float> audioSamples = new ArrayList<>();
        try {
            String path = new File("src/main/resources/songs/" + filePath).getAbsolutePath();
            AudioDispatcher audioDataGetter = AudioDispatcherFactory.fromPipe(path, 44100, 4096, 0);
            audioDataGetter.addAudioProcessor(new AudioProcessor() {
                @Override
                public boolean process(AudioEvent audioEvent) {
                    for (float sample : audioEvent.getFloatBuffer()) {
                        audioSamples.add(sample);
                    }
                    return true;
                }

                @Override
                public void processingFinished() {
                    System.out.println("File extracted");
                }
            });
            audioDataGetter.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
        float[] audioSamplesFinal = new float[audioSamples.size()];
        for (int i = 0; i < audioSamples.size(); i++) {
            audioSamplesFinal[i] = audioSamples.get(i);
        }
        return audioSamplesFinal;
    }

    public class TimerThreadOne extends Thread {
        public void run() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (dispatcherOne != null) {
                        frame.updateWaveformOne(dispatcherOne.secondsProcessed());
                    }
                }
            }, 0, 4);
        }
    }

    public class TimerThreadTwo extends Thread {
        public void run() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (dispatcherTwo != null) {
                        frame.updateWaveformTwo(dispatcherTwo.secondsProcessed());
                    }
                }
            }, 0, 4);
        }
    }
}
