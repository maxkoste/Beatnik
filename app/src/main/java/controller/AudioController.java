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

public class AudioController {
    MediaPlayer audioPlayer1;
    MediaPlayer audioPlayer2;
    int currentPosInPlaylist;
    float masterModifier = 0.5f;
    float crossfaderModifier1 = 1.0f;
    float crossfaderModifier2 = 1.0f;
    float latestVolume1 = 50.0f;
    float latestVolume2 = 50.0f;
    AudioDispatcher dispatcherOne;
    AudioDispatcher dispatcherTwo;

    public AudioController() {

    }

    public void setSong(int channel, String songPath) {
        if (channel == 1) {
            audioPlayer1.setSong(songPath);
        } else {
            audioPlayer2.setSong(songPath);
        }
        playSong(channel);
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
     * @param mix float value between 0-1f
     */
    public void setEffectMix(float mix) {
        audioPlayer1.setEffectMix(mix);
        audioPlayer2.setEffectMix(mix);
    }

}
