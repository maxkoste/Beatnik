package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.List;

/**
 * {@code MixerSelectionView} displays a JavaFX dialog allowing the user to select
 * output audio devices (Mixers) for master and cue channels.
 */
public class MixerSelectionView {

    private Mixer selectedMaster;
    private Mixer selectedCue;
    /**
     * Shows a modal dialog that lets the user choose a master and cue audio output device.
     * Blocks until the dialog is closed.
     *
     * @param parentStage the parent stage from which this dialog is shown
     */
    public void showAndWait(Stage parentStage) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parentStage);
        dialog.setTitle("Välj ljudutgångar");

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(15));
        pane.setHgap(10);
        pane.setVgap(10);

        Label masterLabel = new Label("Master Output:");
        Label cueLabel = new Label("Cue Output:");

        ComboBox<Mixer.Info> masterBox = new ComboBox<>();
        ComboBox<Mixer.Info> cueBox = new ComboBox<>();

        // Endast kompatibla mixrar
        AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        List<Mixer.Info> compatibleMixers = Arrays.stream(AudioSystem.getMixerInfo())
                .filter(mixerInfo -> {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    return mixer.isLineSupported(info);
                })
                .toList();

        masterBox.setItems(FXCollections.observableArrayList(compatibleMixers));
        cueBox.setItems(FXCollections.observableArrayList(compatibleMixers));

        // Cell rendering
        setComboBoxCellFactory(masterBox);
        setComboBoxCellFactory(cueBox);

        // Välj första som standard
        if (!compatibleMixers.isEmpty()) {
            masterBox.getSelectionModel().selectFirst();
            cueBox.getSelectionModel().selectFirst();
        }

        Button confirmBtn = new Button("Bekräfta");
        confirmBtn.setOnAction(e -> {
            selectedMaster = AudioSystem.getMixer(masterBox.getSelectionModel().getSelectedItem());
            selectedCue = AudioSystem.getMixer(cueBox.getSelectionModel().getSelectedItem());
            dialog.close();
        });

        pane.add(masterLabel, 0, 0);
        pane.add(masterBox, 1, 0);
        pane.add(cueLabel, 0, 1);
        pane.add(cueBox, 1, 1);
        pane.add(confirmBtn, 0, 2, 2, 1);

        Scene scene = new Scene(pane, 400, 180);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    /**
     * Sets a custom cell factory to display only the name of the audio device in the ComboBox.
     *
     * @param comboBox the ComboBox to apply the custom renderer to
     */
    private void setComboBoxCellFactory(ComboBox<Mixer.Info> comboBox) {
        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Mixer.Info item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getName());
            }
        });
        comboBox.setButtonCell(comboBox.getCellFactory().call(null));
    }

    public Mixer getSelectedMasterMixer() {
        return selectedMaster;
    }

    public Mixer getSelectedCueMixer() {
        return selectedCue;
    }
}