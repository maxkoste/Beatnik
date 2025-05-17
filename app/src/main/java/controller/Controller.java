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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import dsp.MediaPlayer;
import dsp.SoundPlayer;

public class Controller {
    private MediaPlayer audioPlayer1;
    private MediaPlayer audioPlayer2;
    private MainFrame frame;
    private PlaylistManager playlistManager;
    private TimerThreadOne timerThreadOne;
    private TimerThreadTwo timerThreadTwo;
    private ObservableList<String> playlistSongPaths;
    // finished playing
    private int currentPosInPlaylist;
    private float masterModifier = 0.5F;
    private float crossfaderModifier1 = 1.0F;
    private float crossfaderModifier2 = 1.0F;
    private float latestVolume1 = 50.0F;
    private float latestVolume2 = 50.0F;
    private AudioDispatcher dispatcherOne;
    private AudioDispatcher dispatcherTwo;
    private String currentEffect;
    private Map<String, Float> effectIntensityMap;
    private Map<String, float[]> songsData = new HashMap<>();
    private final Object lock = new Object();
    private Timer timerOne;
    private Timer timerTwo;
    private Semaphore nbrOfThreads = new Semaphore(7);
    private SoundPlayer soundEffect1;
    private SoundPlayer soundEffect2;
    private SoundPlayer soundEffect3;
    private SoundPlayer soundEffect4;
    private SoundPlayer[] soundEffects;

    public Controller(Stage primaryStage) {
        // HashMap saves the state of the Effect-selector knob & the Effect-intensity
        // knob from the GUI
        effectIntensityMap = new HashMap<String, Float>();
        effectIntensityMap.put("delay", 0.0F);
        effectIntensityMap.put("flanger", 0.0F);
        effectIntensityMap.put("filter", 0.0F);
        effectIntensityMap.put("PLACEHOLDER", 0.0F);
        effectIntensityMap.put("PLACEHOLDER2", 0.0F);

        audioPlayer1 = new MediaPlayer();
        audioPlayer2 = new MediaPlayer();
        timerThreadOne = new TimerThreadOne();
        timerThreadTwo = new TimerThreadTwo();
        frame = new MainFrame(this);
        playlistManager = new PlaylistManager(frame);
        frame.setPlaylistManager(playlistManager);
        this.currentEffect = "filter"; // the knob for the effect selector starts at 12 o clock
                                       // which is the filter effect
        startUp(primaryStage);
    }

    public void startUp(Stage primaryStage) {
        frame.start(primaryStage);
        playlistManager.addSongsFromResources();
        playlistManager.loadPlaylistData();
        preloadSongData();
        timerThreadOne.start();
        timerThreadTwo.start();

        soundEffect1 = new SoundPlayer("/SoundEffects/daddy_chill.wav");
        soundEffect2 = new SoundPlayer("/SoundEffects/dundun.wav");
        soundEffect3 = new SoundPlayer("/SoundEffects/fart.wav");
        soundEffect4 = new SoundPlayer("/SoundEffects/yippi.wav");

        this.soundEffects = new SoundPlayer[4];
        soundEffects[0] = soundEffect1;
        soundEffects[1] = soundEffect2;
        soundEffects[2] = soundEffect3;
        soundEffects[3] = soundEffect4;
    }

    private void preloadSongData() {
        ObservableList<String> songFileNames = playlistManager.getSongsGUI();
        for (int i = 0; i < songFileNames.size(); i++) {
            int pos = i;
            Thread extractor = new Thread(() -> {
                String songName = songFileNames.get(pos);
                float[] songData = extract(songName);
                synchronized (lock) {
                    songsData.put(songName, songData);
                    frame.updateLoading(songFileNames.size());
                    nbrOfThreads.release();
                }
            });
            extractor.setDaemon(true);
            extractor.start();
        }
    }

    /**
     * Method for cleaning up resources and stopping the playback of any audio
     */
    public void shutDown() {
        if (audioPlayer1 != null) {
            audioPlayer1.shutDown();
        }
        if (audioPlayer2 != null) {
            audioPlayer2.shutDown();
        }
        if (timerOne != null) {
            timerOne.cancel();
        }
        if (timerTwo != null) {
            timerTwo.cancel();
        }
        if (dispatcherOne != null) {
            dispatcherOne.stop();
        }
        if (dispatcherTwo != null) {
            dispatcherTwo.stop();
        }
    }

    public void setSong(int channel, String songPath) {
        if (channel == 1) {
            audioPlayer1.setSong(songPath);
        } else {
            audioPlayer2.setSong(songPath);
        }
        playSong(channel);
        frame.setWaveformAudioData(songsData.get(songPath), channel);
    }

    public void setSoundboardVolume(float volume) {
        for (SoundPlayer player : soundEffects) {
            player.setVolume(volume);
        }
    }

    public void startPlaylist(int channel, int selectedIndex,
            ObservableList<String> songPaths) {
        playlistSongPaths = songPaths;
        currentPosInPlaylist = selectedIndex;
        setSong(channel, playlistSongPaths.get(currentPosInPlaylist));
    }

    public void playSong(int channel) {
        try {
            if (channel == 1) {
                audioPlayer1.playPause();
                setChannelOneVolume(latestVolume1);
                dispatcherOne = audioPlayer1.getAudioDispatcher();
                if (dispatcherOne != null) {
                    volumeIndicator(dispatcherOne, channel);
                }
            } else {
                audioPlayer2.playPause();
                setChannelTwoVolume(latestVolume2);
                dispatcherTwo = audioPlayer2.getAudioDispatcher();
                if (dispatcherTwo != null) {
                    volumeIndicator(dispatcherTwo, channel);
                }
            }
        } catch (InterruptedException e) {
            playSong(channel);
        }
    }

    public void resetSong(int channel) {
        if (channel == 1) {
            audioPlayer1.resetSong();
        } else {
            audioPlayer2.resetSong();
        }
        playSong(channel);
    }

    public void playSoundEffect(int button) {
        switch (button) {
            case 1:
                soundEffect1.play();
                break;
            case 2:
                soundEffect2.play();
                break;
            case 3:
                soundEffect3.play();
                break;
            case 4:
                soundEffect4.play();
                break;
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
                this.currentEffect = "filter";
                break;
            case 204:
                this.currentEffect = "PLACEHOLDER";
                break;
            case 270:
                this.currentEffect = "PLACEHOLDER2";
                break;
            default:
                System.out.println("Something went wrong...");
        }
    }

    public float getCurrentEffectMix() {
        return this.effectIntensityMap.getOrDefault(currentEffect,
                0.0F);
    }

    public void nextSong(int channel) {
        if (playlistSongPaths != null) {
            if (!(currentPosInPlaylist >= playlistSongPaths.size() - 1)) {
                currentPosInPlaylist++;
                setSong(channel, playlistSongPaths.get(currentPosInPlaylist));
                frame.setInfoText(true, playlistSongPaths
                        .get(currentPosInPlaylist), channel);
            } else {
                frame.userMessage(Alert.AlertType.INFORMATION,
                        "Playlist Finished, Skip now Random");
                playlistSongPaths = null;
                nextSong(channel);
            }
        } else {
            String song = playlistManager.randomSong();
            setSong(channel, song);
            frame.setInfoText(false, song, channel);
        }
    }

    public void setChannelOneVolume(float volume) {

        audioPlayer1.setVolume((volume * masterModifier) * crossfaderModifier1);
        latestVolume1 = volume;
    }

    public void setChannelTwoVolume(float volume) {
        audioPlayer2.setVolume((volume * masterModifier) * crossfaderModifier2);
        latestVolume2 = volume;
    }

    public void setMasterVolume(float masterModifier) {
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

    public void setPlaybackSpeedCh1(double speedFactor) {
        audioPlayer1.setPlaybackSpeed(speedFactor);
    }

    public void setPlaybackSpeedCh2(double speedFactor) {
        audioPlayer2.setPlaybackSpeed(speedFactor);
    }

    /**
     * set the mix value of the effect
     * 0 = 100% dry signal (no effect)
     * 1 = 100% effect signal (only effect)
     * all the effects should be applied on both audio-signals
     * 
     * @param mix float value between 0-1f
     */
    public void setEffectMix(float mix) {
        // Save the state
        effectIntensityMap.put(currentEffect, mix);

        switch (this.currentEffect) {
            case "delay":
                audioPlayer1.setDelayEffectMix(mix);
                audioPlayer2.setDelayEffectMix(mix);
                break;
            case "flanger":
                audioPlayer1.setFlangerEffectMix(mix);
                audioPlayer2.setFlangerEffectMix(mix);
                break;
            case "filter":
                audioPlayer1.setFilterFrequency(mix);
                audioPlayer2.setFilterFrequency(mix);
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
        String fileName = desinationFile.getName();
        playlistManager.getSongsGUI().add(fileName);
        songsData.put(fileName, extract(fileName));
        System.out.println("File moved into the songsGUI folder");
    }

    private float[] extract(String filePath) {
        List<Float> audioSamples = new ArrayList<>();
        try {
            nbrOfThreads.acquire();
            String path = new File("src/main/resources/songs/" + filePath)
                    .getAbsolutePath();
            AudioDispatcher audioDataGetter = AudioDispatcherFactory.fromPipe(path,
                    44100, 4096, 0);
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

    private void volumeIndicator(AudioDispatcher dispatcher, int channel) {
        dispatcher.addAudioProcessor(new AudioProcessor() {
            public boolean process(AudioEvent audioEvent) {
                float[] buffer = audioEvent.getFloatBuffer();
                double rms = 0; // rms = root mean square
                for (float sample : buffer) {
                    rms += sample * sample;
                }

                rms = Math.sqrt(rms / buffer.length);
                final double completeRms = rms;
                if (channel == 1) {
                    frame.updateAudioIndicatorOne(completeRms);
                } else if (channel == 2) {
                    frame.updateAudioIndicatorTwo(completeRms);
                }
                return true;
            }

            @Override
            public void processingFinished() {
            }
        });
    }

    public class TimerThreadOne extends Thread {
        public void run() {
            timerOne = new Timer();
            timerOne.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (dispatcherOne != null) {
                        frame.updateWaveformOne(dispatcherOne.secondsProcessed());
                    }
                }
            }, 0, 5);
        }
    }

    public class TimerThreadTwo extends Thread {
        public void run() {
            timerTwo = new Timer();
            timerTwo.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (dispatcherTwo != null) {
                        frame.updateWaveformTwo(dispatcherTwo.secondsProcessed());
                    }
                }
            }, 0, 5);
        }
    }
}
