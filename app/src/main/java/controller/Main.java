package controller;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import be.tarsos.dsp.effects.DelayEffect; //test to see that TarsosDSP is working

public class Main extends Application{

    @Override
    //This is just a test to get a window working and see that
    //JavaFx is running correctly, will be removed. 
    public void start(Stage primaryStage) throws Exception {

        Group root = new Group();
        Scene scene = new Scene(root, Color.BEIGE);

        Image flowers = new Image("flowers.JPG");
        primaryStage.getIcons().add(flowers);
        primaryStage.setScene(scene);
        primaryStage.setTitle("My first Window!");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
