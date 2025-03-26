package controller;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import be.tarsos.dsp.effects.DelayEffect; //test to see that TarsosDSP is working
import view.MainFrame;

public class Main {

    public static void main(String[] args) {
        Application.launch(MainFrame.class, args); // Put on GUI-Thread
    }
}
