package view;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.List;

/**
 * {@code MixerSelectionView} displays a dialog for choosing audio output
 * devices
 * (mixers) for master and cue channels.
 */
public class MixerSelectionView {

	private Mixer selectedMaster;
	private Mixer selectedCue;

	/**
	 * Displays a modal dialog for choosing audio devices for output.
	 * Blocks until the dialog is closed.
	 *
	 * @param parentStage the stage that owns the dialog
	 */
	public void showAndWait(Stage parentStage) {
		Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(parentStage);
		dialog.setTitle("Välj ljudutgångar");

		// Audio setup
		AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		List<Mixer.Info> compatibleMixers = Arrays.stream(AudioSystem.getMixerInfo())
				.filter(mixerInfo -> AudioSystem.getMixer(mixerInfo).isLineSupported(info))
				.toList();

		// UI Elements
		Label masterLabel = new Label("Master Output:");
		Label cueLabel = new Label("Cue Output:");
		ComboBox<Mixer.Info> masterBox = new ComboBox<>(FXCollections.observableArrayList(compatibleMixers));
		ComboBox<Mixer.Info> cueBox = new ComboBox<>(FXCollections.observableArrayList(compatibleMixers));
		Button confirmBtn = new Button("Bekräfta");

		// Set up ComboBoxes
		setComboBoxCellFactory(masterBox);
		setComboBoxCellFactory(cueBox);
		if (!compatibleMixers.isEmpty()) {
			masterBox.getSelectionModel().selectFirst();
			cueBox.getSelectionModel().selectFirst();
		}

		// Confirm action
		confirmBtn.setOnAction(e -> {
			selectedMaster = AudioSystem.getMixer(masterBox.getSelectionModel().getSelectedItem());
			selectedCue = AudioSystem.getMixer(cueBox.getSelectionModel().getSelectedItem());
			dialog.close();
		});

		// Layout
		GridPane grid = new GridPane();
		grid.setHgap(15);
		grid.setVgap(15);
		grid.setPadding(new Insets(20));
		grid.add(masterLabel, 0, 0);
		grid.add(masterBox, 1, 0);
		grid.add(cueLabel, 0, 1);
		grid.add(cueBox, 1, 1);

		HBox buttonBox = new HBox(confirmBtn);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);

		VBox layout = new VBox(grid, buttonBox);
		layout.setSpacing(20);
		layout.setPadding(new Insets(10));

		Scene scene = new Scene(layout, 450, 200);
		scene.getStylesheets().add("styles.css");
		dialog.setScene(scene);
		dialog.showAndWait();
	}

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
